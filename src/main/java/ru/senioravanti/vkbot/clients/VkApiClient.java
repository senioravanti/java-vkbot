package ru.senioravanti.vkbot.clients;

import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.joining;

import ru.senioravanti.commons.loggers.CustomMapMessage;
import ru.senioravanti.vkbot.exception.CustomException;
import ru.senioravanti.vkbot.exception.VkApiCustomException;
import ru.senioravanti.vkbot.models.vk.VkResponse;
import ru.senioravanti.vkbot.models.vk.method.docs.DocsSave;
import ru.senioravanti.vkbot.models.vk.method.docs.DocsGetMessageUploadServer;
import ru.senioravanti.vkbot.models.ConfigurationProperties;
import ru.senioravanti.vkbot.models.vk.method.Method;
import ru.senioravanti.vkbot.utils.MiscellaneousUtils;
import ru.senioravanti.vkbot.utils.HttpClientUtils;

import static ru.senioravanti.vkbot.config.JacksonConfig.JSON_MAP;

@RequiredArgsConstructor
public class VkApiClient {
    private static final Logger LOGGER = LogManager.getLogger(VkApiClient.class);
    private static final String BASE_URI = "https://api.vk.com/method/";

    private final ConfigurationProperties.Vk props;
    private final HttpClient httpClient;
    private final JsonMapper jsonMapper;

    public <T1, T2> T2 call(Method<T1, T2> m) {
        var queryParams = jsonMapper
            .convertValue(m.params(), JSON_MAP)
            .entrySet().stream()
            .map(it -> {
                var value = it.getValue();
                String stringValue;
                if (value instanceof String) {
                    stringValue = (String) value;
                } else if (MiscellaneousUtils.isPrimitiveOrWrapper(value.getClass())) {
                    stringValue = String.valueOf(it.getValue());
                } else {
                    stringValue = jsonMapper.writeValueAsString(value);
                }
                return it.getKey() + "=" + URLEncoder.encode(stringValue, StandardCharsets.UTF_8);
            })
            .collect(joining("&"));
        var uri = BASE_URI
            + m.name()
            + "?access_token=" + props.api().token()
            + "&scope=manage"
            + "&v=" + props.api().version()
            + "&" + queryParams;
        try {
            var response = httpClient.send(
                HttpRequest
                    .newBuilder(URI.create(uri))
                    .GET()
                    .build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            var rawBody = jsonMapper.readValue(response.body(), JSON_MAP);
            LOGGER.trace(new CustomMapMessage(rawBody));
            var body = jsonMapper.convertValue(rawBody, m.responseType());
            if (body.error() != null) throw new VkApiCustomException(m.name(), body.error());
            return body.response();
        } catch (IOException | InterruptedException ex) {
            throw new CustomException("failed to execute request", ex, Map.of(
                "method", m.name(),
                "params", m.params()
            ));
        }
    }

    public <T1, T2> T2 callMultipart(String uri, T1 form, Class<T2> responseType, Map<String, String> headers) {
        var boundary = UUID.randomUUID().toString();
        try {
            var formFieldMap = new HashMap<>(jsonMapper.convertValue(form, JSON_MAP));
            formFieldMap.put("v", props.api().version());
            var requestBuilder = HttpRequest.newBuilder(URI.create(uri))
                .POST(HttpClientUtils.ofMimeMultipartData(formFieldMap, boundary))
                .header("Content-Type", "multipart/form-data;boundary=" + boundary);
            headers.forEach(requestBuilder::header);
            var response = httpClient.send(
                requestBuilder.build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            var rawBody = jsonMapper.readValue(response.body(), JSON_MAP);
            LOGGER.trace(new CustomMapMessage(rawBody));
            var responseParametricType = jsonMapper.getTypeFactory()
                .constructParametricType(VkResponse.class, responseType);
            VkResponse<T2> body = jsonMapper.convertValue(rawBody, responseParametricType);
            if (body.error() != null) throw new VkApiCustomException(uri, body.error());
            return body.response();
        } catch (IOException | InterruptedException ex) {
            throw new CustomException("failed to post multipart form", ex, Map.of(
                "uri", uri,
                "form", form
            ));
        }
    }

    public String uploadPresigned(String uri, InputStream is) {
        var boundary = UUID.randomUUID().toString();
        try {
            var response = httpClient.send(
                HttpRequest.newBuilder(URI.create(uri))
                    .POST(HttpClientUtils.ofMimeMultipartData(Map.of("file", is), boundary))
                    .header("Content-Type", "multipart/form-data;boundary=" + boundary)
                    .build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            LOGGER.trace(response.body());
            var body = jsonMapper.readTree(response.body());
            return body.get("file").asString();
        } catch (Exception ex) {
            throw new IllegalStateException("failed to upload presigned", ex);
        }
    }

    /**
     * <a href="https://dev.vk.com/ru/api/upload/document-in-profile">Документация</a>
     */
    public DocsSave.Response.Document uploadDocument(Long userId, InputStream is) {
        var headers = Map.of("Authorization", "Bearer " + props.api().token());
        var serverResponse = callMultipart(
            BASE_URI + "docs.getMessagesUploadServer",
            DocsGetMessageUploadServer.Request.newDoc(userId),
            DocsGetMessageUploadServer.Response.class,
            headers
        );
        var fileResponse = uploadPresigned(serverResponse.uploadUrl(), is);
        return (DocsSave.Response.Document) callMultipart(
            BASE_URI + "docs.save",
            new DocsSave.Request(fileResponse),
            DocsSave.Response.class,
            headers
        );
    }
}
