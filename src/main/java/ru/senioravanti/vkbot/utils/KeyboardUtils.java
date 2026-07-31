package ru.senioravanti.vkbot.utils;

import ru.senioravanti.vkbot.models.vk.button.VkButton;
import ru.senioravanti.vkbot.models.vk.button.VkButtonPayload;
import ru.senioravanti.vkbot.models.vk.button.actions.VkButtonActionCallback;

import java.util.ResourceBundle;

public class KeyboardUtils {
    public static VkButton newCancelButton(ResourceBundle b) {
        return new VkButton(new VkButtonActionCallback(b.getString("button.cancel"), new VkButtonPayload(VkButtonPayload.Code.BUTTON_CANCEL)));
    }
}
