package ru.senioravanti.vkbot.handlers;

import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.Map;

import ru.senioravanti.vkbot.models.commands.Session;
import ru.senioravanti.vkbot.models.vk.events.VkEventMessage;
import ru.senioravanti.vkbot.services.MessageService;

import static ru.senioravanti.vkbot.models.vk.button.VkButtonPayload.Type;

@RequiredArgsConstructor
public class SimpleCommandHandler implements CommandHandler.Callback {
    private final MessageService messageService;

    @Override
    public EnumMap<Type, CallbackHandler> callbackHandlers() {
        return new EnumMap<>(Map.of(
            Type.BUTTON_CANCEL, this::onCancel
        ));
    }

    private void onCancel(Session session, VkEventMessage.Event evt) {
        session.setCommand(null);
        messageService.sendMainMenu(session);
    }
}
