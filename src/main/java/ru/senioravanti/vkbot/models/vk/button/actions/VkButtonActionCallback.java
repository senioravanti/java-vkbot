package ru.senioravanti.vkbot.models.vk.button.actions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ru.senioravanti.vkbot.exception.ValidationCustomException;
import ru.senioravanti.vkbot.models.vk.button.VkButtonPayload;

/**
 * @param label текст кнопки.
 * @param payload дополнительные данные в JSON-формате. Они будут отправлены серверу бота в сообщении message_event, в поле payload.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record VkButtonActionCallback(
    String label,
    VkButtonPayload payload
) implements VkButtonAction {

    public static final String TYPE = "callback";
    public static final int MAX_LABEL_LENGTH = 40;

    public VkButtonActionCallback {
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
