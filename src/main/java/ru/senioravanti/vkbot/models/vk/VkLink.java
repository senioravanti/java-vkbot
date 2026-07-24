package ru.senioravanti.vkbot.models.vk;

import org.jspecify.annotations.Nullable;

// https://dev.vk.com/ru/reference/objects/link
public record VkLink(
    String url,
    String title,
    @Nullable String caption, // Подпись ссылки
    String description,
    @Nullable VkPhoto photo // Изображение превью
) {
}
