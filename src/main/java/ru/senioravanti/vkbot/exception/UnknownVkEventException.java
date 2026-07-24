package ru.senioravanti.vkbot.exception;

import java.util.Map;

public class UnknownVkEventException extends CustomException {
    public UnknownVkEventException(String name) {
        super("unknown event name", Map.of("name", name));
    }
}
