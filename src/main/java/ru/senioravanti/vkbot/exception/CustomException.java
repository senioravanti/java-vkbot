package ru.senioravanti.vkbot.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class CustomException extends RuntimeException {
    private final Map<String, Object> params;

    public CustomException(String message, Map<String, Object> params) {
        super(message);
        this.params = params;
    }

    public CustomException(String message, Throwable ex, Map<String, Object> params) {
        super(message, ex);
        this.params = params;
    }
}
