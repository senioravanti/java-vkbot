package ru.senioravanti.vkbot.bot;

import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.time.Duration;

import static java.util.Map.entry;
import static java.util.stream.Collectors.joining;

import ru.senioravanti.commons.loggers.CustomMapMessage;
import ru.senioravanti.vkbot.exception.CustomException;
import ru.senioravanti.vkbot.exception.VkApiException;
import ru.senioravanti.vkbot.models.ConfigurationProperties;
import ru.senioravanti.vkbot.models.vk.method.groups.GroupsGetLongPollServer;
import ru.senioravanti.vkbot.models.vk.LongPollResponse;
import ru.senioravanti.vkbot.models.vk.LongPollResponse.Failure;
import ru.senioravanti.vkbot.models.vk.VkResponse;

import static ru.senioravanti.vkbot.utils.JacksonUtils.OBJECT_TYPE_REFERENCE;

@RequiredArgsConstructor
public class Bot implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(Bot.class);

    private final ConfigurationProperties.Vk props;
    private final HttpClient httpClient;
    private final JsonMapper jsonMapper;
    private final Router router;

    private <T1, T2> T2 call(String method, T1 params, Class<T2> responseType) throws IOException, InterruptedException {
        var queryParams = jsonMapper
            .convertValue(params, OBJECT_TYPE_REFERENCE)
            .entrySet().stream()
            .map(it -> {
                var v = String.valueOf(it.getValue());
                return it.getKey() + "=" + URLEncoder.encode(v, StandardCharsets.UTF_8);
            })
            .collect(joining("&"));
        var uri = "https://api.vk.com/method/"
            + method
            + "?access_token=" + props.api().token()
            + "&scope=manage"
            + "&v=" + props.api().version()
            + "&" + queryParams;
        var response = httpClient.send(
            HttpRequest
                .newBuilder(URI.create(uri))
                .GET()
                .build(),
            HttpResponse.BodyHandlers.ofInputStream()
        );
        VkResponse<T2> body = jsonMapper.readValue(response.body(), jsonMapper.getTypeFactory().constructParametricType(VkResponse.class, responseType));
        if (body.error() != null) throw new VkApiException(method, body.error());
        return body.response();
    }

    private void poll(GroupsGetLongPollServer.Response session) throws IOException, InterruptedException {
        var ts = session.ts();
        while (!Thread.currentThread().isInterrupted()) {
            var uri = session.server()
                + "?act=a_check"
                + "&key=" + session.key()
                + "&ts=" + ts
                + "&wait=25";
            var response = httpClient.send(
                HttpRequest
                    .newBuilder(URI.create(uri))
                    .build(),
                HttpResponse.BodyHandlers.ofInputStream()
            );
            var body = jsonMapper.readValue(response.body(), LongPollResponse.class);
            var failed = body.failed();
            if (failed != null) {
                LOGGER.warn("update failed {}: {}", failed.getCode(), failed.getMessage());
                if (failed == Failure.TS_TOO_OLD) {
                    ts = body.ts();
                    continue;
                }
                return;
            }
            ts = body.ts();
            body.updates().forEach(router::dispatch);
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                var longPollServer = call("groups.getLongPollServer", new GroupsGetLongPollServer.Request(props.groupId()), GroupsGetLongPollServer.Response.class);
                LOGGER.info(CustomMapMessage.of("vk bot successfully started ...",
                    entry("server", longPollServer.server())
                ));
                poll(longPollServer);
            } catch (IOException ex) {
                LOGGER.error(CustomMapMessage.of("an I/O error occurs when sending or receiving, or the client has closing shut down", ex));
                try {
                    Thread.sleep(Duration.ofSeconds(5));
                } catch (InterruptedException _) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (InterruptedException ex) {
                LOGGER.error(new CustomMapMessage("request is interrupted", ex));
            } catch (CustomException ex) {
                LOGGER.error(new CustomMapMessage(ex.getMessage(), ex.getParams()));
            } catch (Exception ex) {
                LOGGER.error(new CustomMapMessage("unhandled exception", ex));
            }
        }
    }
}
