package ru.senioravanti.vkbot.models.vk.method.messages;

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Медиавложение
 * @param type
 * @param ownerId идентификатор владельца. Идентификатор сообщества должен начинаться со знака -.
 * @param mediaId
 */
public record MessageAttachment(
    Type type,
    String ownerId,
    String mediaId
) {
    @RequiredArgsConstructor
    public enum Type {
        PHOTO("photo"),
        VIDEO("video"),
        AUDIO("audio"),
        DOCUMENT("doc"),
        WALL("wall"),
        MARKET("market"),
        POLL("poll"),
        QUESTION("question");

        private final String name;

        @JsonValue
        public String getName() {
            return name;
        }
    }

    @Override
    public String toString() {
        return "%s%s_%s".formatted(type.getName(), ownerId, mediaId);
    }
}
