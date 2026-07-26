package ru.senioravanti.vkbot.clients;

import lombok.RequiredArgsConstructor;

import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static java.util.stream.Collectors.joining;

import ru.senioravanti.vkbot.exception.CustomException;
import ru.senioravanti.vkbot.exception.VkApiCustomException;
import ru.senioravanti.vkbot.models.ConfigurationProperties;
import ru.senioravanti.vkbot.models.vk.VkResponse;
import ru.senioravanti.vkbot.utils.MiscellaneousUtils;

import static ru.senioravanti.vkbot.config.JacksonConfig.OBJECT_TYPE_REFERENCE;

@RequiredArgsConstructor
public class VkApiClient {
    private final ConfigurationProperties.Vk.Api props;
    private final HttpClient httpClient;
    private final JsonMapper jsonMapper;

    public <T1, T2> T2 call(String method, T1 params, Class<T2> responseType) {
        var queryParams = jsonMapper
            .convertValue(params, OBJECT_TYPE_REFERENCE)
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
        var uri = "https://api.vk.com/method/"
            + method
            + "?access_token=" + props.token()
            + "&scope=manage"
            + "&v=" + props.version()
            + "&" + queryParams;
        try {
            var response = httpClient.send(
                HttpRequest
                    .newBuilder(URI.create(uri))
                    .GET()
                    .build(),
                HttpResponse.BodyHandlers.ofInputStream()
            );
            VkResponse<T2> body = jsonMapper.readValue(response.body(), jsonMapper.getTypeFactory().constructParametricType(VkResponse.class, responseType));
            if (body.error() != null) throw new VkApiCustomException(method, body.error());
            return body.response();
        } catch (IOException | InterruptedException ex) {
            throw new CustomException("failed to execute request", ex, Map.of(
                "method", method,
                "params", params
            ));
        }
    }
}
