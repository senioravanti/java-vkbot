package ru.senioravanti.vkbot.models.vk.method.messages;

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * <a href="https://dev.vk.com/ru/method/messages.send">Медиавложение</a>
 * @param type
 * @param mediaId
 * @param ownerId идентификатор владельца. Идентификатор сообщества должен начинаться со знака -.
 * @see ru.senioravanti.vkbot.models.vk.attachments.VkAttachment
 */
public record MessageAttachment(
    Type type,
    Long mediaId,
    Long ownerId
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
