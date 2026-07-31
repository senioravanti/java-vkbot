package ru.senioravanti.vkbot.models.vk.method.messages;

import lombok.Builder;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.core.type.TypeReference;

import ru.senioravanti.vkbot.models.vk.VkResponse;
import ru.senioravanti.vkbot.models.vk.method.Method;

/**
 * <a href="https://dev.vk.com/ru/method/messages.edit">Документация</a>
 */
public record MessageEdit(Request params) implements Method<MessageEdit.Request, Integer> {
    @Override
    public String name() {
        return "messages.edit";
    }

    @Override
    public TypeReference<VkResponse<Integer>> responseType() {
        return new TypeReference<>() {
        };
    }

    @Builder
    public record Request(
        Long messageId,
        @JsonUnwrapped
        MessageRequest message,
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        Boolean keepForwardMessages,
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        Boolean keepSnippets
    ) {
        public Request {
            if (keepForwardMessages == null) keepForwardMessages = true;
            if (keepSnippets == null) keepSnippets = true;
        }
    }
}
