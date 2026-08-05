package ru.senioravanti.vkbot.models.vk.method.messages;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.type.TypeReference;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape;

import java.util.List;

import ru.senioravanti.vkbot.models.vk.VkMessage;
import ru.senioravanti.vkbot.models.vk.VkResponse;
import ru.senioravanti.vkbot.models.vk.method.Method;

/**
 * <a href="https://dev.vk.com/ru/method/messages.getHistory">Возвращает историю сообщений для указанного диалога</a>
 */
public record MessagesGetHistory(Request params) implements Method<MessagesGetHistory.Request, MessagesGetHistory.Response> {
    @Override
    public String name() { return "messages.getHistory"; }

    @Override
    public TypeReference<VkResponse<Response>> responseType() { return new TypeReference<>() { }; }

    /**
     * @param peerId идентификатор назначения
     * @param count количество сообщений, которое необходимо получить (но не более 200).
     * @param offset смещение, необходимое для выборки определённого подмножества сообщений, должен быть >= 0, если не передан параметр start_message_id, и должен быть <= 0, если передан.
     * @param startMessageId если значение > 0, то это идентификатор сообщения, начиная с которого нужно вернуть историю переписки, если передано значение -1, то к значению параметра offset прибавляется количество входящих непрочитанных сообщений в конце диалога (подробности смотрите ниже).
     * @param isAscending возвращать сообщения в хронологическом порядке. 0 — возвращать сообщения в обратном хронологическом порядке (по умолчанию).
     */
    public record Request(
        Long peerId,
        Short count,
        @JsonInclude(Include.NON_NULL)
        Integer offset,
        @JsonInclude(Include.NON_NULL)
        Long startMessageId,
        @JsonProperty("rev")
        @JsonInclude(Include.NON_NULL)
        @JsonFormat(shape = Shape.NUMBER)
        Boolean isAscending
    ) {
        public Request(Long peerId) {
            this(peerId, (short) 1, null, null, false);
        }
    }

    public record Response(
        Integer count,
        List<VkMessage> items
    ) {
    }
}
