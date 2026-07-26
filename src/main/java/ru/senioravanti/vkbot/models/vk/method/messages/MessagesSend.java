package ru.senioravanti.vkbot.models.vk.method.messages;

import lombok.RequiredArgsConstructor;
import lombok.Builder;

import org.jspecify.annotations.Nullable;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import com.fasterxml.jackson.annotation.*;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.joining;

import ru.senioravanti.vkbot.exception.ValidationCustomException;
import ru.senioravanti.vkbot.models.vk.method.messages.templates.MessageSendTemplate;

// https://dev.vk.com/ru/method/messages.send
public class MessagesSend {
    private static final Random RANDOM = new SecureRandom();

    private MessagesSend() {
    }

    /**
     * @param userId              идентификатор пользователя, которому отправляется сообщение. Вместо него можно использовать peer_id.
     * @param randomId            идентификатор, предназначенный для предотвращения повторной отправки одного и того же сообщения; 0 -> проверка на уникальность не нужна, Любое другое число в пределах int32 -> проверка на уникальность нужна.
     * @param message             текст личного сообщения. Макс. длина = 9000.
     * @param attachments         объект или несколько объектов, приложенных к записи
     * @param keyboard            объект, описывающий клавиатуру бота.
     * @param template
     * @param replyTo             идентификатор сообщения, на которое требуется ответить
     * @param peerId              идентификатор получателя сообщения.
     * @param peerIdList          идентификаторы получателей сообщения
     * @param domain              короткий адрес пользователя, например: persik_ryzhiy
     * @param chatId              идентификатор беседы, в которую отправляется сообщение.
     * @param stickerId           идентификатор стикера.
     * @param contentSource       объект, описывающий источник пользовательского контента для чат-ботов.
     * @param forwardMessagesList идентификаторы пересылаемых сообщений
     * @param forward
     */
    @Builder
    public record Request(
        Long userId,
        Integer randomId,
        String message,
        @JsonIgnore
        List<Attachment> attachments,
        @JsonInclude(Include.NON_NULL)
        MessageSendKeyboard keyboard,
        @JsonInclude(Include.NON_NULL)
        MessageSendTemplate template,
        @JsonInclude(Include.NON_NULL)
        Integer replyTo,
        Long peerId,
        @JsonIgnore
        List<Long> peerIdList,
        @JsonInclude(Include.NON_NULL)
        String domain,
        @JsonInclude(Include.NON_NULL)
        Long chatId,
        @JsonInclude(Include.NON_NULL)
        Integer stickerId,
        @JsonInclude(Include.NON_NULL)
        ContentSource contentSource,
        @JsonIgnore
        List<Long> forwardMessagesList,
        @JsonInclude(Include.NON_NULL)
        Forward forward
    ) {
        /**
         * Медиавложение
         *
         * @param type
         * @param ownerId идентификатор владельца. Идентификатор сообщества должен начинаться со знака -.
         * @param mediaId
         */
        public record Attachment(
            Type type,
            String ownerId,
            String mediaId
        ) {
            @RequiredArgsConstructor
            public enum Type {
                PHOTO("photo"),
                VIDEO("video"),
                AUDIO("audio"),
                DOCUMENT("doc"),
                WALL("wall"),
                MARKET("market"),
                POLL("poll"),
                QUESTION("question");

                private final String name;

                @JsonValue
                public String getName() {
                    return name;
                }
            }

            @Override
            public String toString() {
                return "%s%s_%s".formatted(type.getName(), ownerId, mediaId);
            }
        }

        /**
         * @param ownerId           владелец сообщений. Укажите это поле, если вы хотите переслать сообщения из сообщества в личный диалог.
         * @param peerId            идентификатор места, из которого необходимо переслать сообщения.
         * @param convertMessageIds массив conversation_message_id сообщений, которые необходимо переслать. В параметр можно передать сообщения, которые:
         *                          <ul>
         *                          <li>находятся в личном диалоге с ботом;</li>
         *                          <li>являются исходящими сообщениями бота;</li>
         *                          <li>написаны после того, как бот вступил в беседу и появился доступ к сообщениям.</li>
         *                          </ul>
         * @param isReply           ответ на сообщения. Укажите это поле, если вы хотите ответить на сообщения в том чате, в котором находятся сообщения. При этом в conversation_message_ids или message_ids должен находиться только один элемент.
         */
        public record Forward(
            @JsonInclude(Include.NON_NULL)
            Long ownerId,
            Long peerId,
            List<Long> convertMessageIds,
            @JsonInclude(Include.NON_NULL)
            Boolean isReply
        ) {
        }

        /**
         * Объект, описывающий источник пользовательского контента для чат-ботов.
         */
        @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            property = "type",
            include = JsonTypeInfo.As.PROPERTY
        )
        @JsonSubTypes({
            @JsonSubTypes.Type(value = ContentSourceMessage.class, name = ContentSourceMessage.TYPE),
            @JsonSubTypes.Type(value = ContentSourceUrl.class, name = ContentSourceUrl.TYPE),
        })
        public sealed interface ContentSource permits ContentSourceMessage, ContentSourceUrl {
            String type();
        }

        /**
         * Сообщение от пользователя боту.
         *
         * @param ownerId               от чьего имени указан peer_id. т.е. вы можете использовать контент из сообщения другой группы.
         * @param peerId                ид диалога.
         * @param conversationMessageId ид сообщения в беседе.
         */
        public record ContentSourceMessage(
            Long ownerId,
            Long peerId,
            Long conversationMessageId
        ) implements ContentSource {
            public static final String TYPE = "message";

            @Override
            public String type() {
                return TYPE;
            }
        }

        /**
         * Любой другой контент на платформе (комментарий, пост, фотография и тд.)
         *
         * @param url адрес контента.
         */
        public record ContentSourceUrl(
            String url
        ) implements ContentSource {
            public static final String TYPE = "url";

            @Override
            public String type() {
                return TYPE;
            }
        }

        public static final int MAX_MESSAGE_LENGTH = 9000;

        @JsonInclude(Include.NON_EMPTY)
        @JsonGetter("attachment")
        public String attachment() {
            if (attachments == null) return null;
            return attachments.stream()
                .map(Attachment::toString)
                .collect(joining(","));
        }

        @JsonInclude(Include.NON_EMPTY)
        @JsonGetter("peer_ids")
        public String peerIds() {
            if (peerIdList == null) return null;
            return peerIdList.stream()
                .map(String::valueOf)
                .collect(joining(","));
        }

        @JsonInclude(Include.NON_EMPTY)
        @JsonGetter("forward_messages")
        public String forwardMessages() {
            if (forwardMessagesList == null) return null;
            return forwardMessagesList.stream()
                .map(String::valueOf)
                .collect(joining(","));
        }

        public Request {
            if (randomId == null) {
                randomId = RANDOM.nextInt(Integer.MAX_VALUE) + 1;
            }
            if (peerId == null) {
                peerId = userId;
            }
            if (message.length() > MAX_MESSAGE_LENGTH) {
                throw new ValidationCustomException(ValidationCustomException.Error.builder()
                    .name("message")
                    .invalidValue(message.length())
                    .message("message length must be <= " + MAX_MESSAGE_LENGTH)
                    .build());
            }
        }
    }

    /**
     * @param messageId             идентификатор сообщения
     * @param peerId                идентификатор назначения
     * @param conversationMessageId идентификатор сообщения в диалоге
     * @param error                 сообщение об ошибке, если сообщение не было доставлено получателю
     */
    public record Response(
        Long messageId,
        Long peerId,
        Long conversationMessageId,
        @Nullable
        String error
    ) {
    }
}
