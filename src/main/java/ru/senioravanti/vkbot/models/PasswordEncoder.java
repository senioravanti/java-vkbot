package ru.senioravanti.vkbot.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Base64;
import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

import ru.senioravanti.vkbot.utils.Ascii85;

@RequiredArgsConstructor
@Accessors(fluent = true)
public enum PasswordEncoder {
    BASE16("base16") {
        @Override
        public String encode(byte[] b) { return HexFormat.of().formatHex(b); }

        @Override
        public int bytes(int length) { return (length + 1) / 2; }
    },
    BASE64("base64") {
        @Override
        public String encode(byte[] b) { return Base64.getUrlEncoder().withoutPadding().encodeToString(b); }

        @Override
        public int bytes(int length) { return (length * 3 + 3) / 4; }
    },
    BASE85("base85") {
        @Override
        public String encode(byte[] b) { return Ascii85.encode(b); }

        @Override
        public int bytes(int length) { return (length * 4 + 4) / 5; }
    };

    private static final Map<String, PasswordEncoder> STRING_TO_ENUM = Stream.of(values())
        .collect(toMap(PasswordEncoder::code, Function.identity()));

    @Getter
    @JsonValue
    private final String code;

    public abstract String encode(byte[] bytes);

    public abstract int bytes(int length);

    @JsonCreator
    public static Optional<PasswordEncoder> fromString(String code) {
        return Optional.ofNullable(STRING_TO_ENUM.get(code));
    }
}
