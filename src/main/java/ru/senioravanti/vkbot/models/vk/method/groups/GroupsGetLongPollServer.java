package ru.senioravanti.vkbot.models.vk.method.groups;

// https://dev.vk.com/ru/method/groups.getLongPollServer
public class GroupsGetLongPollServer {
    private GroupsGetLongPollServer() {
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
