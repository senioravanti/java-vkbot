package ru.senioravanti.vkbot.models.vk.button.actions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.senioravanti.vkbot.models.vk.button.VkButtonPayload;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VkButtonActionLink(
    // Ссылка, которая будет открыта при нажатии на кнопку.
    String link,
    // Текст кнопки.
    String label,
    VkButtonPayload payload
) implements VkButtonAction {
    public static final String TYPE = "open_link";

    @Override
    public String type() {
        return TYPE;
    }
}
