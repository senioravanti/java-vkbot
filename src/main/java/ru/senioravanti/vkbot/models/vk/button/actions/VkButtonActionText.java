package ru.senioravanti.vkbot.models.vk.button.actions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ru.senioravanti.vkbot.exception.ValidationCustomException;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VkButtonActionText(
    // Текст кнопки.
    String label,
    String payload
) implements VkButtonAction {
    public static final int MAX_LABEL_LENGTH = 40;
    public static final String TYPE = "text";

    public VkButtonActionText {
        validatePayload();
        if (label.length() > MAX_LABEL_LENGTH) {
            throw new ValidationCustomException(ValidationCustomException.Error.builder()
                .name("label")
                .invalidValue(label.length())
                .message("label length must be <= " + MAX_LABEL_LENGTH)
                .build());
        }
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public boolean supportsColor() {
        return true;
    }
}
