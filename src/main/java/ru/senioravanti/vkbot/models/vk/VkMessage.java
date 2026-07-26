package ru.senioravanti.vkbot.models.vk;

import lombok.RequiredArgsConstructor;

import org.jspecify.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import ru.senioravanti.vkbot.models.vk.attachments.VkAttachment;

// https://dev.vk.com/ru/reference/objects/message

/**
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
    Integer id,
    Instant date,
    Long peerId,
    Long fromId,
    String text,
    @Nullable
    FormatData formatData,
    List<VkAttachment> attachments,
    @Nullable
    String payload,
    @JsonProperty("out")
    Type type,
    @JsonProperty("fwd_messages")
    List<VkMessage> forwardMessages
) {
    @RequiredArgsConstructor
    public enum Type {
        REPLIED(-1), // Пересланное
        RECEIVED(0), // Полученное
        SENT(1); // Отправленное

        private final int code;

        @JsonValue
        public int getCode() {
            return code;
        }

        public static Type from(Integer code) {
            if (code == null) return REPLIED;
            return Arrays.stream(Type.values())
                .filter(it -> it.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unknown message type `%s`".formatted(code)));
        }
    }

    public record FormatData(
        String version, // Версия форматирования
        List<Item> items // Массив объектов с параметрами форматирования
    ) {
        public record Item(
            Long offset, // Позиция символа, начиная с которого применяется указанный тип форматирования
            Long length, // Количество символов, на которые распространяется указанный тип форматирования
            Type type, // Тип форматирования, примененный к диапазону символов
            String url // Адрес ссылки, если в type указан тип url
        ) {
            @RequiredArgsConstructor
            public enum Type {
                BOLD("bold"),
                ITALIC("italic"),
                URL("url"),
                UNDERLINE("underline");

                public final String name;

                @JsonValue
                public String getName() {
                    return name;
                }

                @JsonCreator
                public static Type from(String name) {
                    if (name == null) return null;
                    return Arrays.stream(Type.values())
                        .filter(it -> it.name.equals(name))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("unknown type `%s`".formatted(name)));
                }
            }
        }
    }
}
