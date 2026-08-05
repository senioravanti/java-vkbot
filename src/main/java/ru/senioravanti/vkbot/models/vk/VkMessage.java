package ru.senioravanti.vkbot.models.vk;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import org.jspecify.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import ru.senioravanti.vkbot.models.vk.attachments.VkAttachment;

/**
 * <a href="https://dev.vk.com/ru/reference/objects/message">Личное сообщение</a>
 * @param id              идентификатор сообщения
 * @param date            время отправки в Unixtime
 * @param peerId          идентификатор назначения
 * @param fromId          идентификатор отправителя
 * @param text
 * @param formatData      информация о форматировании текста сообщения
 * @param attachments     медиавложения сообщения (фотографии, ссылки и т.п.).
 * @param payload         дополнительные данные в формате JSON-строки
 * @param type            тип сообщения
 * @param forwardMessages пересланные сообщения
 */
public record VkMessage(
    Long id,
    Instant date,
    Long peerId,
    Long fromId,
    String text,
    @Nullable
    VkFormatData formatData,
    List<VkAttachment> attachments,
    @Nullable
    String payload,
    @JsonProperty("out")
    Type type,
    @JsonProperty("fwd_messages")
    List<VkMessage> forwardMessages
) {
    @Accessors(fluent = true)
    @Getter
    @RequiredArgsConstructor
    public enum Type {
        REPLIED(-1), // Пересланное
        RECEIVED(0), // Полученное
        SENT(1); // Отправленное

        @JsonValue
        private final int code;

        @JsonCreator
        public static Type of(Integer code) {
            if (code == null) return REPLIED;
            return Arrays.stream(Type.values())
                .filter(it -> it.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unknown message type `%s`".formatted(code)));
        }
    }
}
