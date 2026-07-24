package ru.senioravanti.vkbot.models.vk;

import java.time.Instant;
import java.util.List;

// https://dev.vk.com/ru/reference/objects/photo
public record VkPhoto(
    Integer id,
    Long ownerId, // Идентификатор владельца фотографии
    Instant date,
    List<Size> sizes, // Массив с копиями изображения в разных размерах
    Integer width, // Ширина оригинала фотографии в пикселях
    Integer height // Высота оригинала фотографии в пикселях
) {
    public record Size(
        String type,
        String url,
        Integer height,
        Integer width
    ) {
    }
}
