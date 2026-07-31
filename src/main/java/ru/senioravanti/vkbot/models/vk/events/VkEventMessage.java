package ru.senioravanti.vkbot.models.vk.events;

import org.jspecify.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import ru.senioravanti.vkbot.models.vk.VkLang;
import ru.senioravanti.vkbot.models.vk.VkMessage;
import ru.senioravanti.vkbot.models.vk.button.VkButtonPayload;

public class VkEventMessage {
    private VkEventMessage() {
    }

    /**
     * @param message    данные о сообщении, которое написал пользователь
     * @param clientInfo информация о функциях, доступных пользователю
     */
    public record New(
        VkMessage message,
        ClientInfo clientInfo
    ) {
        /**
         * <a href="https://dev.vk.com/ru/api/bots/getting-started#%D0%98%D0%BD%D1%84%D0%BE%D1%80%D0%BC%D0%B0%D1%86%D0%B8%D1%8F%20%D0%BE%20%D0%B4%D0%BE%D1%81%D1%82%D1%83%D0%BF%D0%BD%D1%8B%D1%85%20%D0%BF%D0%BE%D0%BB%D1%8C%D0%B7%D0%BE%D0%B2%D0%B0%D1%82%D0%B5%D0%BB%D1%8E%20%D1%84%D1%83%D0%BD%D0%BA%D1%86%D0%B8%D1%8F%D1%85">Документация</a>
         *
         * @param buttonActions  массив кнопок, которые поддерживает клиент
         * @param keyboard       поддерживается ли клавиатура ботов клиентом
         * @param inlineKeyboard поддерживается ли inline-клавиатура ботов клиентом
         * @param lang           язык клиента
         */
        public record ClientInfo(
            List<String> buttonActions,
            Boolean keyboard, //
            Boolean inlineKeyboard, //
            @JsonProperty("lang_id")
            VkLang lang
        ) {
        }
    }

    public record Allow(
        Long userId,
        String key // Параметр, переданный в методе messages.allowMessagesFromGroup
    ) {
    }

    public record Deny(
        Long userId
    ) {
    }

    public record Read(
        Long fromId, // Идентификатор пользователя или сообщества, прочитавшего сообщение
        Long peerId, // Идентификатор пользователя или сообщества, отправившего сообщение
        Integer readMessageId, // Идентификатор прочитанного сообщения
        Long conversationMessageId // Уникальный автоматически увеличивающийся номер для всех сообщений с этим peer.
    ) {
    }

    /**
     * @param userId
     * @param peerId                идентификатор диалога со стороны бота
     * @param eventId               случайная строка. Активна в течение минуты, спустя минуту становится недействительной
     * @param payload               дополнительная информация, указанная в клавише
     * @param conversationMessageId идентификатор сообщения в беседе. Не передаётся для клавиатур беседы
     */
    public record Event(
        Long userId,
        Long peerId,
        String eventId,
        VkButtonPayload payload,
        @Nullable
        Long conversationMessageId
    ) {
    }
}
