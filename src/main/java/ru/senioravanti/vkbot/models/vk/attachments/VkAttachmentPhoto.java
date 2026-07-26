package ru.senioravanti.vkbot.models.vk.attachments;

import java.time.Instant;
import java.util.List;

// https://dev.vk.com/ru/reference/objects/photo
public record VkAttachmentPhoto(
    Integer id,
    Long ownerId, // Идентификатор владельца фотографии
    Instant date,
    List<Size> sizes, // Массив с копиями изображения в разных размерах
    Integer width, // Ширина оригинала фотографии в пикселях
    Integer height // Высота оригинала фотографии в пикселях
) implements VkAttachment {
    public static final String TYPE = "photo";

    public record Size(
        String type,
        String url,
        Integer height,
        Integer width
    ) {
    }

    @Override
    public String type() {
        return TYPE;
    }
}
