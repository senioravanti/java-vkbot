package ru.senioravanti.vkbot.models.vk.attachments;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

// https://dev.vk.com/ru/reference/objects/link
public record VkAttachmentLink(
    String url,
    String title,
    // Подпись ссылки.
    @JsonInclude(Include.NON_NULL)
    String caption,
    String description,
    // Изображение превью.
    @JsonInclude(Include.NON_NULL)
    VkAttachmentPhoto photo
) implements VkAttachment {
    public static final String TYPE = "link";

    @Override
    public String type() {
        return TYPE;
    }
}
