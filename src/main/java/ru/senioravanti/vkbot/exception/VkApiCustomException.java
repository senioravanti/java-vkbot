package ru.senioravanti.vkbot.exception;

import lombok.Getter;

import java.util.Map;

import ru.senioravanti.vkbot.models.vk.VkError;

@Getter
public class VkApiCustomException extends CustomException {
    public VkApiCustomException(String method, VkError error) {
        super("vk api error response", Map.of(
            "method", method,
            "error", error
        ));
    }
}
