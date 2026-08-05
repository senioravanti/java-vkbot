package ru.senioravanti.vkbot.utils;

import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Stream;

import ru.senioravanti.vkbot.models.PasswordEncoder;
import ru.senioravanti.vkbot.models.vk.button.VkButton;
import ru.senioravanti.vkbot.models.vk.button.VkButtonPayload;
import ru.senioravanti.vkbot.models.vk.button.actions.VkButtonActionCallback;
import ru.senioravanti.vkbot.models.vk.method.messages.MessageKeyboard;

import static ru.senioravanti.vkbot.models.vk.button.VkButtonPayload.Type;

public class KeyboardUtils {
    public static final int NUMBER_BUTTONS_PER_ROW = 4;

    public static VkButton newCancelButton(String text) {
        return new VkButton(new VkButtonActionCallback(text, new VkButtonPayload(Type.BUTTON_CANCEL)));
    }

    public static VkButton newCancelButton(ResourceBundle b) {
        return newCancelButton(b.getString("button.cancel"));
    }

    public static List<VkButton> newCancelButtonRow(ResourceBundle b) {
        return List.of(newCancelButton(b));
    }

    public static List<List<VkButton>> newNumKeyboard(Type code, int perRow, int[] arr) {
        List<List<VkButton>> grid = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            int rowIdx = i / perRow;
            var label = String.valueOf(arr[i]);
            var payload = new VkButtonPayload(code, label);
            if (grid.size() <= rowIdx) grid.add(new ArrayList<>());
            grid.get(rowIdx)
                .add(new VkButton(new VkButtonActionCallback(label, payload)));
        }
        return grid;
    }

    public static List<List<VkButton>> newNumKeyboard(Type code, int[] arr) {
        return newNumKeyboard(code, NUMBER_BUTTONS_PER_ROW, arr);
    }

    public static MessageKeyboard newPasswordEncoderKeyboard(ResourceBundle b) {
        List<List<VkButton>> grid = new ArrayList<>(new ArrayList<>());
        grid.add(Stream
            .of(PasswordEncoder.values())
            .map(it -> new VkButtonActionCallback(it.code(), new VkButtonPayload(Type.PASSWORD_SELECT_ENCODER, it.code())))
            .map(VkButton::new)
            .toList()
        );
        grid.add(KeyboardUtils.newCancelButtonRow(b));
        return MessageKeyboard.fromGrid(grid);
    }
}
