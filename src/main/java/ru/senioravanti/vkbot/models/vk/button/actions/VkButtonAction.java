package ru.senioravanti.vkbot.models.vk.button.actions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.senioravanti.vkbot.exception.ValidationCustomException;

// https://dev.vk.com/ru/api/bots/development/keyboard#%D0%9E%D1%82%D0%BF%D1%80%D0%B0%D0%B2%D0%BA%D0%B0%20%D0%BA%D0%BB%D0%B0%D0%B2%D0%B8%D0%B0%D1%82%D1%83%D1%80%D1%8B
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type",
    include = JsonTypeInfo.As.PROPERTY
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = VkButtonActionText.class, name = VkButtonActionText.TYPE),
    @JsonSubTypes.Type(value = VkButtonActionLink.class, name = VkButtonActionLink.TYPE),
    @JsonSubTypes.Type(value = VkButtonActionCallback.class, name = VkButtonActionCallback.TYPE),
})
public sealed interface VkButtonAction permits
    VkButtonActionText,
    VkButtonActionLink,
    VkButtonActionCallback {
    int MAX_PAYLOAD_LENGTH = 255;

    String type();

    default boolean supportsColor() {
        return false;
    }

    // Дополнительные данные в формате JSON-строки. Они будут отправлены серверу бота в сообщении message_new, в поле payload. Максимальная длина — 255 символов
    @JsonInclude(Include.NON_NULL)
    String payload();

    default void validatePayload() {
        var payload = payload();
        if (payload != null && payload.length() > MAX_PAYLOAD_LENGTH) {
            throw new ValidationCustomException(ValidationCustomException.Error.builder()
                .name("payload")
                .message("payload length must be <= " + MAX_PAYLOAD_LENGTH)
                .invalidValue(payload.length())
                .build());
        }
    }
}
