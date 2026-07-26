package ru.senioravanti.vkbot.models.vk.button.actions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VkButtonActionLink(
    // Ссылка, которая будет открыта при нажатии на кнопку.
    String link,
    // Текст кнопки.
    String label,
    String payload
) implements VkButtonAction {
    public static final String TYPE = "open_link";

    @Override
    public String type() {
        return TYPE;
    }

    public VkButtonActionLink {
        validatePayload();
    }
}
