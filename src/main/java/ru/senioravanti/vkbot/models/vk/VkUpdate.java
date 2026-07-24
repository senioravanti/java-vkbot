package ru.senioravanti.vkbot.models.vk;

import java.util.Map;
import ru.senioravanti.vkbot.models.vk.events.VkEvent;

public record VkUpdate(
    VkEvent type, // Тип события
    String eventId, // Идентификатор события
    Map<String, Object> object // Объект, инициировавший событием
) {
}
