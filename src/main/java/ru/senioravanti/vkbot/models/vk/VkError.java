package ru.senioravanti.vkbot.models.vk;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record VkError(
    @JsonProperty("error_code")
    Integer code,
    @JsonProperty("error_msg")
    String message,
    List<Pair> requestParams
) {
    public record Pair(
        String key,
        String value
    ) {
    }
}
