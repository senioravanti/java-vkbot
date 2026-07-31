package ru.senioravanti.vkbot.exception;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException() {
        super("session not found");
    }
}
