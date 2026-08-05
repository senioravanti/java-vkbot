package ru.senioravanti.vkbot.models.vk.method.messages;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.core.type.TypeReference;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;

import ru.senioravanti.vkbot.models.vk.VkResponse;
import ru.senioravanti.vkbot.models.vk.method.Method;

/**
 * <a href="https://dev.vk.com/ru/method/messages.delete">Удаляет сообщение</a>
 */
public record MessagesDelete(Request params) implements Method<MessagesDelete.Request, List<MessagesDelete.Response>> {
    @Override
    public String name() { return "messages.delete"; }

    @Override
    public TypeReference<VkResponse<List<Response>>> responseType() { return new TypeReference<>() { }; }

    /**
     * @param peerId
     * @param messageIdList
     * @param deleteForAll  если сообщение нужно удалить для получателей.
     */
    public record Request(
        Long peerId,
        @JsonIgnore
        List<Long> messageIdList,
        @JsonInclude(Include.NON_NULL)
        @JsonFormat(shape = Shape.NUMBER)
        Boolean deleteForAll
    ) {
        public static Request of(Long peerId, Long... messageIds) {
            return new Request(peerId, Arrays.asList(messageIds), true);
        }

        @JsonGetter("message_ids")
        public String messageIds() {
            return messageIdList.stream()
                .map(String::valueOf)
                .collect(joining(","));
        }
    }

    public record Response(
        Long peerId,
        Long messageId,
        Long conversationMessageId,
        Integer response
    ) {
    }
}
