package ru.senioravanti.vkbot.handlers;

import ru.senioravanti.vkbot.models.commands.Session;
import ru.senioravanti.vkbot.models.vk.button.VkButtonPayload;
import ru.senioravanti.vkbot.models.vk.events.VkEventMessage;

import java.util.EnumMap;

public interface CommandHandler {
    interface Callback {
        EnumMap<VkButtonPayload.Code, CallbackHandler> callbackHandlers();

        @FunctionalInterface
        interface CallbackHandler {
            void handle(VkEventMessage.Event evt, Session session);
        }
    }

    interface Message {
        void onMessage(VkEventMessage.New evt, Session session);
    }
}
