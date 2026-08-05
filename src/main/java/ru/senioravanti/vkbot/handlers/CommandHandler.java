package ru.senioravanti.vkbot.handlers;

import ru.senioravanti.vkbot.models.commands.Command;
import ru.senioravanti.vkbot.models.commands.Session;
import ru.senioravanti.vkbot.models.vk.button.VkButtonPayload;
import ru.senioravanti.vkbot.models.vk.events.VkEventMessage;

import java.util.EnumMap;

public interface CommandHandler {
    interface Callback extends CommandHandler {
        EnumMap<VkButtonPayload.Type, CallbackHandler> callbackHandlers();

        @FunctionalInterface
        interface CallbackHandler {
            void handle(Session session, VkEventMessage.Event evt);
        }
    }

    interface Message<T extends Command> extends CommandHandler {
        Class<T> commandType();

        void onMessage(Session session, VkEventMessage.New evt);
    }
}
