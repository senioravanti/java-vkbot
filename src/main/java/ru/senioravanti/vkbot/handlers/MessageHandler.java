package ru.senioravanti.vkbot.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

import ru.senioravanti.commons.loggers.CustomMapMessage;
import ru.senioravanti.vkbot.models.commands.Command;
import ru.senioravanti.vkbot.models.vk.VkMessage;
import ru.senioravanti.vkbot.models.vk.events.VkEventMessage;
import ru.senioravanti.vkbot.services.MessageService;
import ru.senioravanti.vkbot.stores.SessionStore;

import static ru.senioravanti.vkbot.models.vk.button.VkButtonPayload.Type;
import static ru.senioravanti.vkbot.handlers.CommandHandler.Callback.CallbackHandler;

public class MessageHandler {
    private static final Logger LOGGER = LogManager.getLogger(MessageHandler.class);

    private final SessionStore sessionStore;
    private final MessageService messageService;

    private final EnumMap<Type, CallbackHandler> callbackHandlers;
    private final Map<Class<? extends Command>, CommandHandler.Message<?>> messageHandlers;

    public MessageHandler(SessionStore sessionStore, MessageService messageService, CommandHandler... handlers) {
        this.sessionStore = sessionStore;
        this.messageService = messageService;
        callbackHandlers = Stream.of(handlers)
            .filter(it -> it instanceof CommandHandler.Callback)
            .map(CommandHandler.Callback.class::cast)
            .map(CommandHandler.Callback::callbackHandlers)
            .flatMap(it -> it.entrySet().stream())
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (_, b) -> b, () -> new EnumMap<>(Type.class)));
        messageHandlers = Stream.of(handlers)
            .filter(CommandHandler.Message.class::isInstance)
            .map(it -> (CommandHandler.Message<?>) it)
            .collect(toMap(CommandHandler.Message::commandType, Function.identity()));

    }

    public void onNew(VkEventMessage.New evt) {
        var userId = evt.message().fromId();
        var locale = evt.clientInfo().lang().locale();
        var session = sessionStore.getOrInit(userId, locale);
        if (session.getUser().locale() == Locale.ROOT) {
            session.setUser(session.getUser().withLocale(locale));
        }
        session.setLastUserMessageId(evt.message().id());
        Optional.ofNullable(session.getCommand())
            .map(Command::getClass)
            .map(messageHandlers::get)
            .ifPresentOrElse(
                it -> it.onMessage(session, evt),
                () -> messageService.sendMainMenu(session)
            );
    }

    public void onEdit(VkMessage evt) {
        LOGGER.info(new CustomMapMessage(evt));
    }

    public void onAllow(VkEventMessage.Allow evt) {
        LOGGER.info(new CustomMapMessage(evt));
    }

    public void onDeny(VkEventMessage.Deny evt) {
        LOGGER.info(new CustomMapMessage(evt));
    }

    public void onEvent(VkEventMessage.Event evt) {
        var userId = evt.userId();
        var buttonMessageId = evt.conversationMessageId();
        var session = sessionStore.getOrInit(userId, Locale.ROOT);
        var handler = callbackHandlers.get(evt.payload().type());
        if (handler == null) {
            messageService.sendCommandPlaceholder(session);
            return;
        }
        try {
            handler.handle(session, evt);
            if (Objects.equals(buttonMessageId, session.getLastBotMessageId())) return;
            Thread.startVirtualThread(() -> messageService.deleteMessages(userId, buttonMessageId));
        } catch (NullPointerException _) {
            Thread.startVirtualThread(() -> messageService.deleteMessages(userId, buttonMessageId));
            messageService.sendMainMenu(session);
        }
    }
}
