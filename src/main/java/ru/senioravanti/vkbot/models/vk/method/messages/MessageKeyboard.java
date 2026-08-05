package ru.senioravanti.vkbot.models.vk.method.messages;

import org.jspecify.annotations.Nullable;

import java.util.List;

import ru.senioravanti.vkbot.models.vk.button.VkButton;

/**
 * <a href="https://dev.vk.com/ru/api/bots/development/keyboard#%D0%A2%D0%B8%D0%BF%D1%8B%20%D0%BA%D0%BD%D0%BE%D0%BF%D0%BE%D0%BA">Документация</a>
 *
 * @param buttons массив, элементы которого соответствуют рядам клавиатуры. Каждый элемент, в свою очередь, тоже является массивом
 * @param inline  определяет тип клавиатуры
 *                <ul>
 *                   <li>true :: клавиатура может иметь до 10 кнопок, сгруппированных в 6 рядов до 5 кнопок в каждом.</li>
 *                   <li>false :: клавиатура 40 кнопок, которые могут быть сгруппированы в 10 рядов до 5 кнопок в каждом.</li>
 *                <ul/>
 * @param oneTime работает, только если "inline": false
 */
public record MessageKeyboard(
    List<List<VkButton>> buttons,
    @Nullable
    Boolean oneTime,
    @Nullable
    Boolean inline
) {
    public static MessageKeyboard fromGrid(List<List<VkButton>> grid) {
        return new MessageKeyboard(grid, null, true);
    }

    public static MessageKeyboard fromRow(List<VkButton> row) {
        return fromGrid(List.of(row));
    }

    public static MessageKeyboard fromButton(VkButton button) {
        return fromRow(List.of(button));
    }
}
