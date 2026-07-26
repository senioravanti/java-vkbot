package ru.senioravanti.vkbot.models.vk.button;

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.senioravanti.vkbot.exception.ValidationCustomException;
import ru.senioravanti.vkbot.models.vk.button.actions.VkButtonAction;

public record VkButton(
    // Объект, описывающий тип кнопки и её параметры.
    VkButtonAction action,
    // Цвет кнопки.
    @JsonInclude(Include.NON_NULL)
    Color color
) {
    public VkButton {
        if (color != null && !action.supportsColor()) {
            throw new ValidationCustomException(ValidationCustomException.Error.builder()
                .name("color")
                .message("button with action type %s does not support color".formatted(action.type()))
                .build());
        }
    }

    public VkButton(VkButtonAction action) {
        this(action, null);
    }

    @RequiredArgsConstructor
    public enum Color {
        PRIMARY("primary"), // Используется для кнопок, выполняющих основное действие
        SECONDARY("secondary"), //Используется для обычных кнопок
        NEGATIVE("negative"), // Используется для кнопок, выполняющих опасное действие или отмену, например «Удалить» или «Отклонить»
        POSITIVE("positive"); // Используется для кнопок, с помощью которых пользователь выражает согласие или подтверждает свои действия, например «Согласиться» или «Подтвердить»

        private final String name;

        @JsonValue
        public String getName() {
            return name;
        }
    }
}
