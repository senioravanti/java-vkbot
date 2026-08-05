package ru.senioravanti.vkbot.models.vk.method.docs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * <a href="https://dev.vk.com/ru/method/docs.getMessagesUploadServer">Документация</a>
 */
public class DocsGetMessageUploadServer {
    /**
     * @param type   тип файла.
     * @param peerId идентификатор назначения. Возможные значения:
     *               <ul>
     *               <li>для пользователя: {ИДЕНТИФИКАТОР_ПОЛЬЗОВАТЕЛЯ};</li>
     *               <li>для групповой беседы: 2000000000 + {ИДЕНТИФИКАТОР_БЕСЕДЫ};</li>
     *               <li>для сообщества: -{ИДЕНТИФИКАТОР_СООБЩЕСТВА}.</li>
     *               </ul>
     */
    public record Request(
        Type type,
        String peerId
    ) {
        public static Request newDoc(Long userId) {
            return new Request(Type.DOC, userId.toString());
        }

        @RequiredArgsConstructor
        @Getter
        @Accessors(fluent = true)
        public enum Type {
            DOC("doc"),
            AUDIO("audio_message");

            @JsonValue
            private final String code;
        }
    }

    public record Response(
        String uploadUrl
    ) {
    }
}
