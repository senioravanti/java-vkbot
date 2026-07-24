package ru.senioravanti.vkbot.bot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.senioravanti.commons.loggers.CustomMapMessage;
import ru.senioravanti.vkbot.models.vk.VkMessage;
import ru.senioravanti.vkbot.models.vk.events.VkEventMessage;

public class MessageHandler {
    private static final Logger LOGGER = LogManager.getLogger(MessageHandler.class);

    public void onNew(VkEventMessage.New update) {
        LOGGER.info(new CustomMapMessage(update));
    }

    public void onReply(VkMessage update) {
        LOGGER.info(new CustomMapMessage(update));
    }

    public void onEdit(VkMessage update) {
        LOGGER.info(new CustomMapMessage(update));
    }

    public void onAllow(VkEventMessage.Allow update) {
        LOGGER.info(new CustomMapMessage(update));
    }

    public void onDeny(VkEventMessage.Deny update) {
        LOGGER.info(new CustomMapMessage(update));
    }

    public void onEvent(VkEventMessage.Event update) {
        LOGGER.info(new CustomMapMessage(update));
    }
}
