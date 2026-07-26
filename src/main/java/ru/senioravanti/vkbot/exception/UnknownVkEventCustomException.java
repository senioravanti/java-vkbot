package ru.senioravanti.vkbot.exception;

import java.util.Map;

public class UnknownVkEventCustomException extends CustomException {
    public UnknownVkEventCustomException(String name) {
        super("unknown event name", Map.of("name", name));
    }
}
