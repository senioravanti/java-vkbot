package ru.senioravanti.vkbot.utils;

import tools.jackson.core.type.TypeReference;

import java.util.Map;

public class JacksonUtils {
    private JacksonUtils() {
    }

    public static final TypeReference<Map<String, Object>> OBJECT_TYPE_REFERENCE = new TypeReference<>() {
    };
}
