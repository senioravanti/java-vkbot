package ru.senioravanti.vkbot.models.vk.button;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import org.jspecify.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;

import ru.senioravanti.vkbot.exception.CustomException;

/**
 * @param code код кнопки.
 * @param id   идентификатор объекта.
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
    Code code,
    @Nullable
    String id
) {
    @Accessors(fluent = true)
    @Getter
    @RequiredArgsConstructor
    public enum Code {
        IMAGES("JpIc5A"),
        TEMPLATE("HgbKag"),
        TEMPLATE_SELECT_TYPE("VfVcbw"),
        TEMPLATE_SELECT_NUMBER("E7UYyA"),
        TEMPLATE_IMPORT_MARKDOWN_WITH_METADATA("8KK9Pg"),
        TEMPLATE_IMPORT_MARKDOWN("emK28w"),
        REFERENCES("KP4Ozw"),
        BUTTON_CANCEL("6zAccQ");

        @JsonValue
        private final String code;

        @JsonCreator
        public static Code of(String code) {
            return Arrays.stream(Code.values())
                .filter(it -> it.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new CustomException("unknown button", Map.of("code", code)));
        }
    }

    public VkButtonPayload(Code code) {
        this(code, null);
    }
}
