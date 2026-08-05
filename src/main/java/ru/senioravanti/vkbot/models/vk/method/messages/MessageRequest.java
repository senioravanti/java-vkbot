package ru.senioravanti.vkbot.models.vk.method.messages;

import lombok.Builder;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;

import static java.util.stream.Collectors.joining;

import ru.senioravanti.vkbot.exception.ValidationCustomException;
import ru.senioravanti.vkbot.models.vk.VkFormatData;
import ru.senioravanti.vkbot.models.vk.method.messages.templates.MessageTemplate;

/**
 * @param userId      идентификатор пользователя, которому отправляется сообщение. Вместо него можно использовать peer_id.
 * @param text        текст личного сообщения. Макс. длина = 9000.
 * @param attachments объект или несколько объектов, приложенных к записи
 * @param keyboard    объект, описывающий клавиатуру бота.
 * @param template
 * @see ru.senioravanti.vkbot.models.vk.VkMessage
 */
@Builder
public record MessageRequest(
    @JsonProperty("peer_id")
    Long userId,
    @JsonProperty("message")
    String text,
    @JsonInclude(Include.NON_NULL)
    VkFormatData formatData,
    @JsonIgnore()
    List<MessageAttachment> attachments,
    @JsonInclude(Include.NON_NULL)
    MessageKeyboard keyboard,
    @JsonInclude(Include.NON_NULL)
    MessageTemplate template
) {
    public static final int MAX_MESSAGE_LENGTH = 9000;

    public MessageRequest {
        if (text != null && text.length() > MAX_MESSAGE_LENGTH) {
            throw new ValidationCustomException(ValidationCustomException.Error.builder()
                .name("message")
                .invalidValue(text.length())
                .message("message length must be <= " + MAX_MESSAGE_LENGTH)
                .build());
        }
    }

    @JsonInclude(Include.NON_EMPTY)
    @JsonGetter("attachment")
    public String attachment() {
        if (attachments == null) return null;
        return attachments.stream()
            .map(MessageAttachment::toString)
            .collect(joining(","));
    }
}
