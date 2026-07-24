package ru.senioravanti.vkbot.models.vk;

import java.time.Instant;

// https://dev.vk.com/ru/reference/objects/doc
public record VkDocument(
    Integer id,
    Long ownerId,
    String title,
    Long size, // Размер файла в байтах
    String ext, // Расширение файла
    String url, // Адрес файла, по которому его можно загрузить
    Instant date
) {
}
