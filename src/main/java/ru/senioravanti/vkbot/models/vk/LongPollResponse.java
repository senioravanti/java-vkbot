package ru.senioravanti.vkbot.models.vk;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;

// https://dev.vk.com/ru/api/bots-long-poll/getting-started
public record LongPollResponse(
    String ts,
    Failure failed,
    List<VkUpdate> updates
) {
    @RequiredArgsConstructor
    @Getter
    public enum Failure {
        TS_TOO_OLD(1, "история событий устарела или была частично утеряна"),
        KEY_EXPIRED(2, "истекло время действия ключа"),
        INFO_LOST(3, "информация утрачена");

        private final int code;
        private final String message;

        @JsonValue
        public int getCode() {
            return code;
        }

        @JsonCreator
        public static Failure from(Integer code) {
            if (code == null) return null;
            return Arrays.stream(Failure.values())
                .filter(it -> it.code == code)
                .findFirst()
                .orElse(null);
        }
    }
}
