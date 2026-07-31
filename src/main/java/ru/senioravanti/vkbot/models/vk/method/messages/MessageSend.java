package ru.senioravanti.vkbot.models.vk.method.messages;

import lombok.Builder;

import com.fasterxml.jackson.annotation.*;
import tools.jackson.core.type.TypeReference;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.stream.Collectors.joining;

import ru.senioravanti.vkbot.models.vk.VkResponse;
import ru.senioravanti.vkbot.models.vk.method.Method;

/**
 * <a href="https://dev.vk.com/ru/method/messages.send">Документация</a>
 */
public record MessageSend(Request params) implements Method<MessageSend.Request, Long> {

    @Override
    public String name() {
        return "messages.send";
    }

    @Override
    public TypeReference<VkResponse<Long>> responseType() {
        return new TypeReference<>() {
        };
    }

    /**
     * @param randomId            идентификатор, предназначенный для предотвращения повторной отправки одного и того же сообщения; 0 -> проверка на уникальность не нужна, Любое другое число в пределах int32 -> проверка на уникальность нужна.
     * @param replyTo             идентификатор сообщения, на которое требуется ответить
     * @param domain              короткий адрес пользователя, например: persik_ryzhiy
     * @param chatId              идентификатор беседы, в которую отправляется сообщение.
     * @param stickerId           идентификатор стикера.
     * @param contentSource       объект, описывающий источник пользовательского контента для чат-ботов.
     * @param forwardMessagesList идентификаторы пересылаемых сообщений
     * @param forward
     */
    @Builder
    public record Request(
        @JsonUnwrapped
        MessageRequest message,
        Integer randomId,
        @JsonInclude(Include.NON_NULL)
        Integer replyTo,
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
                randomId = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE) + 1;
            }
        }
    }
}
