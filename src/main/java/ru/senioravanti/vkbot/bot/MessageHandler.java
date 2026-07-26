package ru.senioravanti.vkbot.bot;

import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.ResourceBundle;

import ru.senioravanti.commons.loggers.CustomMapMessage;
import ru.senioravanti.vkbot.clients.VkApiClient;
import ru.senioravanti.vkbot.config.ResourceBundleConfig;
import ru.senioravanti.vkbot.models.vk.VkMessage;
import ru.senioravanti.vkbot.models.vk.button.VkButton;
import ru.senioravanti.vkbot.models.vk.button.actions.VkButtonActionCallback;
import ru.senioravanti.vkbot.models.vk.events.VkEventMessage;
import ru.senioravanti.vkbot.models.vk.method.messages.MessageSendKeyboard;
import ru.senioravanti.vkbot.models.vk.method.messages.MessagesSend;

@RequiredArgsConstructor
public class MessageHandler {
    private static final Logger LOGGER = LogManager.getLogger(MessageHandler.class);

    private final ResourceBundle.Control control;
    private final VkApiClient vkApiClient;

    public void onNew(VkEventMessage.New u) {
        var b = ResourceBundle.getBundle(ResourceBundleConfig.NAME, u.clientInfo().lang().getLocale(), control);
        var prefix = "main-menu";
        var request = MessagesSend.Request.builder()
            .userId(u.message().fromId())
            .message(b.getString(prefix + ".greeting"))
            .keyboard(MessageSendKeyboard.newInline(List.of(
                List.of(new VkButton(new VkButtonActionCallback(b.getString(prefix + ".keyboard.images"), "1"))),
                List.of(new VkButton(new VkButtonActionCallback(b.getString(prefix + ".keyboard.template"), "2"))),
                List.of(new VkButton(new VkButtonActionCallback(b.getString(prefix + ".keyboard.references"), "3")))
            )))
            .build();
        var responseType = request.peerIds() != null
            ? MessagesSend.Response.class
            : Long.class;
        var response = vkApiClient.call("messages.send", request, responseType);
        LOGGER.debug(new CustomMapMessage(response));
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
        LOGGER.info(new CustomMapMessage(u));
    }
}
