package ru.senioravanti.vkbot.bot;

import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.time.Duration;

import static java.util.Map.entry;

import ru.senioravanti.commons.loggers.CustomMapMessage;
import ru.senioravanti.vkbot.clients.VkApiClient;
import ru.senioravanti.vkbot.exception.CustomException;
import ru.senioravanti.vkbot.models.vk.method.groups.GroupsGetLongPollServer;
import ru.senioravanti.vkbot.models.vk.LongPollResponse;
import ru.senioravanti.vkbot.models.vk.LongPollResponse.Failure;

@RequiredArgsConstructor
public class Bot implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(Bot.class);

    private final long groupId;
    private final HttpClient httpClient;
    private final JsonMapper jsonMapper;
    private final VkApiClient vkApiClient;
    private final Router router;

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
            body.updates().forEach(u -> Thread.startVirtualThread(() -> router.dispatch(u)));
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                var longPollServer = vkApiClient.call("groups.getLongPollServer", new GroupsGetLongPollServer.Request(groupId), GroupsGetLongPollServer.Response.class);
                LOGGER.info(CustomMapMessage.of("vk bot successfully started ...",
                    entry("server", longPollServer.server())
                ));
                poll(longPollServer);
            } catch (Exception ex) {
                LOGGER.error(new CustomMapMessage("unhandled exception", ex));
            }
        }
    }
}
