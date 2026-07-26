package ru.senioravanti.vkbot.models.vk.attachments;

import java.time.Instant;

// https://dev.vk.com/ru/reference/objects/doc
public record VkAttachmentDocument(
    Integer id,
    Long ownerId,
    String title,
    Long size, // Размер файла в байтах
    String ext, // Расширение файла
    String url, // Адрес файла, по которому его можно загрузить
    Instant date
) implements VkAttachment {
    public static final String TYPE = "doc";

    @Override
    public String type() {
        return TYPE;
    }
}
