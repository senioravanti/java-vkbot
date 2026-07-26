package ru.senioravanti.vkbot.models.vk.attachments;

// https://dev.vk.com/ru/reference/objects/attachments-message
// Только десериализация
/*
[
  {
    "type": "photo",
    "photo": <объект photo>
  },
  {
    "type": "audio",
    "audio": <объект audio>
  }
]
 */
public sealed interface VkAttachment permits
    VkAttachmentPhoto,
    VkAttachmentLink,
    VkAttachmentDocument {
    String type();
}
