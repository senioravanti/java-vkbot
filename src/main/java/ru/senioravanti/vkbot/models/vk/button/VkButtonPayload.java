package ru.senioravanti.vkbot.models.vk.button;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.Arrays;
import java.util.Map;

import ru.senioravanti.vkbot.exception.CustomException;

/**
 * @param type код кнопки.
 * @param text   идентификатор объекта.
 */
/*
Коды можно сгенерировать:
public static final Random RANDOM = new SecureRandom();
public static String randomCode() {
    var b = new byte[4];
    RANDOM.nextBytes(b);
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(b);
}
 */
public record VkButtonPayload(
    Type type,
    @JsonInclude(Include.NON_NULL)
    String text
) {
    @Accessors(fluent = true)
    @Getter
    @RequiredArgsConstructor
    public enum Type {
        SNACKBAR("show_snackbar"),
        IMAGES("JpIc5A"),
        TEMPLATE("HgbKag"),
        TEMPLATE_SELECT_TYPE("VfVcbw"),
        TEMPLATE_SELECT_NUMBER("E7UYyA"),
        TEMPLATE_IMPORT_MARKDOWN_WITH_METADATA("8KK9Pg"),
        TEMPLATE_IMPORT_MARKDOWN("emK28w"),
        REFERENCES("KP4Ozw"),
        PASSWORD("9R9jeA"),
        PASSWORD_SELECT_LENGTH("WOIAwQ"),
        PASSWORD_SELECT_COUNT("AJikHg"),
        PASSWORD_SELECT_ENCODER("ArgOFA"),
        BUTTON_CANCEL("6zAccQ");

        @JsonValue
        private final String code;

        @JsonCreator
        public static Type of(String code) {
            return Arrays.stream(Type.values())
                .filter(it -> it.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new CustomException("unknown button", Map.of("type", code)));
        }
    }

    public VkButtonPayload(Type code) {
        this(code, null);
    }
}
