package ru.senioravanti.vkbot.models.vk;

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

// https://dev.vk.com/ru/api/api-requests#%D0%9E%D0%B1%D1%89%D0%B8%D0%B5%20%D0%BF%D0%B0%D1%80%D0%B0%D0%BC%D0%B5%D1%82%D1%80%D1%8B
@RequiredArgsConstructor
public enum VkLang {
    RU(0),
    EN(3);

    private final int id;

    @JsonValue
    public int getId() {
        return id;
    }

    @JsonCreator
    public static VkLang from(int id) {
        return Arrays.stream(VkLang.values())
            .filter(it -> it.id == id)
            .findFirst()
            .orElse(RU);
    }
}
