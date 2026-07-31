package ru.senioravanti.vkbot.handlers;

import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

import static java.util.Map.entry;

import ru.senioravanti.commons.loggers.CustomMapMessage;
import ru.senioravanti.vkbot.models.vk.events.VkEvent;
import ru.senioravanti.vkbot.models.vk.events.VkEventMessage;
import ru.senioravanti.vkbot.models.vk.VkMessage;
import ru.senioravanti.vkbot.models.vk.VkUpdate;

@RequiredArgsConstructor
public class Router {
    @FunctionalInterface
    public interface VkUpdateHandler {
        void handle(VkUpdate update);
    }

    private static final Logger LOGGER = LogManager.getLogger(Router.class);

    private final JsonMapper jsonMapper;
    private final Map<VkEvent, VkUpdateHandler> router;

    public Router(
        JsonMapper jsonMapper,
        MessageHandler messageHandler
    ) {
        this.jsonMapper = jsonMapper;
        router = Map.of(
            VkEvent.MESSAGE_NEW, unmarshal(VkEventMessage.New.class, messageHandler::onNew),
            VkEvent.MESSAGE_EDIT, unmarshal(VkMessage.class, messageHandler::onEdit),
            VkEvent.MESSAGE_ALLOW, unmarshal(VkEventMessage.Allow.class, messageHandler::onAllow),
            VkEvent.MESSAGE_DENY, unmarshal(VkEventMessage.Deny.class, messageHandler::onDeny),
            VkEvent.MESSAGE_EVENT, unmarshal(VkEventMessage.Event.class, messageHandler::onEvent)
        );
    }

    private <T> VkUpdateHandler unmarshal(Class<T> type, Handler<T> handler) {
        return update -> {
            try {
                var object = jsonMapper.convertValue(update.object(), type);
                handler.handle(object);
            } catch (IllegalArgumentException ex) {
                LOGGER.error(CustomMapMessage.of("failed to convert", ex,
                    entry("event", update.type().getName()),
                    entry("object", update.object()),
                    entry("type", type.getName())
                ));
            }
        };
    }

    public void dispatch(VkUpdate u) {
        LOGGER.debug(new CustomMapMessage(u));
        router.get(u.type()).handle(u);
    }
}
