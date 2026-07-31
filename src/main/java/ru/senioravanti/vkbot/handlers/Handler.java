package ru.senioravanti.vkbot.handlers;

@FunctionalInterface
public interface Handler<T> {
    void handle(T object);
}
