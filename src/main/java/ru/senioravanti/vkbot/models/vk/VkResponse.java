package ru.senioravanti.vkbot.models.vk;

import org.jspecify.annotations.Nullable;

public record VkResponse<T>(
    T response,
    @Nullable VkError error
) {
}
