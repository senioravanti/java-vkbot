package ru.senioravanti.vkbot.models.vk.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

import ru.senioravanti.vkbot.exception.UnknownVkEventCustomException;

// https://dev.vk.com/ru/api/community-events/json-schema
@RequiredArgsConstructor
@Getter
public enum VkEvent {
    MESSAGE_NEW("message_new"), // Входящее сообщение
    MESSAGE_EDIT("message_edit"), // Редактирование сообщения
    MESSAGE_ALLOW("message_allow"), // Разрешение на получение
    MESSAGE_DENY("message_deny"), // Запрет на получение
    MESSAGE_READ("message_read"), // Информация о том, что сообщение прочитано
    MESSAGE_EVENT("message_event"); // Действие с сообщением. Пользователь нажал на Callback-кнопку

    private final String name;

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static VkEvent from(String name) {
        if (name == null) return null;
        return Arrays.stream(VkEvent.values())
            .filter(it -> it.getName().equals(name))
            .findFirst()
            .orElseThrow(() -> new UnknownVkEventCustomException(name));
    }
}
