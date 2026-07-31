package ru.senioravanti.vkbot.models.vk.method;

import tools.jackson.core.type.TypeReference;

import ru.senioravanti.vkbot.models.vk.VkResponse;

public interface Method<P, R> {
    String name();
    P params();
    TypeReference<VkResponse<R>> responseType();
}
