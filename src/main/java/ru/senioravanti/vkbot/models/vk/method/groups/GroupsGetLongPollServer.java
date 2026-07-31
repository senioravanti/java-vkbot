package ru.senioravanti.vkbot.models.vk.method.groups;

import ru.senioravanti.vkbot.models.vk.VkResponse;
import ru.senioravanti.vkbot.models.vk.method.Method;
import tools.jackson.core.type.TypeReference;

/**
 * <a href="https://dev.vk.com/ru/method/groups.getLongPollServer">Документация</a>
 */
public record GroupsGetLongPollServer(
    Request params) implements Method<GroupsGetLongPollServer.Request, GroupsGetLongPollServer.Response> {
    @Override
    public String name() {
        return "groups.getLongPollServer";
    }

    @Override
    public TypeReference<VkResponse<Response>> responseType() {
        return new TypeReference<>() {
        };
    }

    public record Request(
        long groupId // Идентификатор сообщества
    ) {
    }

    public record Response(
        String key, // Секретный ключ сессии
        String server, // Адрес сервера
        String ts // Номер последнего события, начиная с которого нужно получать данные
    ) {
    }
}
