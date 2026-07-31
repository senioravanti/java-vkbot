package ru.senioravanti.vkbot.clients;

import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

import ru.senioravanti.commons.loggers.CustomMapMessage;
import ru.senioravanti.vkbot.exception.CustomException;
import ru.senioravanti.vkbot.exception.VkApiCustomException;
import ru.senioravanti.vkbot.models.ConfigurationProperties;
import ru.senioravanti.vkbot.models.vk.method.Method;
import ru.senioravanti.vkbot.utils.MiscellaneousUtils;

import static ru.senioravanti.vkbot.config.JacksonConfig.OBJECT_TYPE_REFERENCE;

@RequiredArgsConstructor
public class VkApiClient {
    private final static Logger LOGGER = LogManager.getLogger(VkApiClient.class);

    private final ConfigurationProperties.Vk.Api props;
    private final HttpClient httpClient;
    private final JsonMapper jsonMapper;

    public <T1, T2> T2 call(Method<T1, T2> m) {
        var queryParams = jsonMapper
            .convertValue(m.params(), OBJECT_TYPE_REFERENCE)
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
            + m.name()
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
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            var rawBody = jsonMapper.readValue(response.body(), OBJECT_TYPE_REFERENCE);
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
}
