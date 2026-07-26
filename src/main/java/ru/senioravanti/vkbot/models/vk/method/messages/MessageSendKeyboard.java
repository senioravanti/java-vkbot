package ru.senioravanti.vkbot.models.vk.method.messages;

import org.jspecify.annotations.Nullable;

import ru.senioravanti.vkbot.models.vk.button.VkButton;

import java.util.List;

// https://dev.vk.com/ru/api/bots/development/keyboard#%D0%A2%D0%B8%D0%BF%D1%8B%20%D0%BA%D0%BD%D0%BE%D0%BF%D0%BE%D0%BA
public record MessageSendKeyboard(
    List<List<VkButton>> buttons, // Массив, элементы которого соответствуют рядам клавиатуры. Каждый элемент, в свою очередь, тоже является массивом
    @Nullable
    Boolean oneTime, // Работает, только если "inline": false
    @Nullable
    Boolean inline // Определяет тип клавиатуры
) {
    public static MessageSendKeyboard newInline(List<List<VkButton>> buttons) {
        return new MessageSendKeyboard(buttons, null, true);
    }
}
