package ru.senioravanti.vkbot.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

import ru.senioravanti.commons.loggers.CustomMapMessage;
import ru.senioravanti.vkbot.exception.SessionNotFoundException;
import ru.senioravanti.vkbot.models.vk.VkMessage;
import ru.senioravanti.vkbot.models.vk.events.VkEventMessage;
import ru.senioravanti.vkbot.services.MessageService;
import ru.senioravanti.vkbot.stores.SessionStore;

import static ru.senioravanti.vkbot.models.vk.button.VkButtonPayload.Code;
import static ru.senioravanti.vkbot.handlers.CommandHandler.Callback.CallbackHandler;

public class MessageHandler {
    private static final Logger LOGGER = LogManager.getLogger(MessageHandler.class);

    private final SessionStore sessionStore;
    private final MessageService messageService;
    private final EnumMap<Code, CallbackHandler> callbackHandlers;

    public MessageHandler(SessionStore sessionStore, MessageService messageService, CommandHandler.Callback... handlers) {
        this.sessionStore = sessionStore;
        this.messageService = messageService;
        callbackHandlers = Arrays.stream(handlers)
            .map(CommandHandler.Callback::callbackHandlers)
            .flatMap(it -> it.entrySet().stream())
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (_, b) -> b, () -> new EnumMap<>(Code.class)));
    }

    public void onNew(VkEventMessage.New u) {
        var userId = u.message().fromId();
        var locale = u.clientInfo().lang().locale();
        var session = sessionStore.get(userId);
        if (session == null) {
            session = sessionStore.init(userId, locale);
        } else if (session.getUser().locale() == Locale.ROOT) {
            session.setUser(session.getUser().withLocale(locale));
        }
        messageService.sendMainMenu(session);
    }

    public void onEdit(VkMessage u) {
        LOGGER.info(new CustomMapMessage(u));
    }

    public void onAllow(VkEventMessage.Allow u) {
        LOGGER.info(new CustomMapMessage(u));
    }

    public void onDeny(VkEventMessage.Deny u) {
        LOGGER.info(new CustomMapMessage(u));
    }

    public void onEvent(VkEventMessage.Event u) {
        try {
            var session = sessionStore.getOrThrow(u.userId());
            var handler = callbackHandlers.get(u.payload().code());
            if (handler == null) {
                messageService.sendCommandPlaceholder(session);
                return;
            }
            handler.handle(u, session);
        } catch (SessionNotFoundException ex) {
            var userId = u.userId();
            LOGGER.warn(new CustomMapMessage(ex.getMessage(), Map.of("userId", userId)));
            var session = sessionStore.init(userId, Locale.ROOT);
            messageService.sendMainMenu(session, true);
        }
    }
}
