package ru.senioravanti.vkbot.stores;

import lombok.RequiredArgsConstructor;

import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import ru.senioravanti.vkbot.clients.VkApiClient;
import ru.senioravanti.vkbot.exception.SessionNotFoundException;
import ru.senioravanti.vkbot.models.commands.Session;
import ru.senioravanti.vkbot.models.vk.method.messages.MessagesGetHistory;
import ru.senioravanti.vkbot.models.vk.method.users.UserGet;

@RequiredArgsConstructor
public class SessionStore {
    private final ConcurrentHashMap<Long, Session> store = new ConcurrentHashMap<>();
    private final VkApiClient vkApiClient;

    private Session init(Long userId, Locale locale) {
        var user = vkApiClient
            .call(new UserGet(UserGet.Request.of(userId)))
            .getFirst();
        var session = new Session(new Session.User(
            userId,
            user.lastName(),
            user.firstName(),
            user.screenName(),
            user.birthday(),
            locale
        ));
        var lastMessage = vkApiClient
            .call(new MessagesGetHistory(new MessagesGetHistory.Request(user.id())))
            .items()
            .getFirst();
        // from_id для сообщества -> идентификатор сообщества, умноженный на -1
        Consumer<Long> setter = Long.signum(lastMessage.fromId()) == -1
            ? session::setLastBotMessageId
            : session::setLastUserMessageId;
        setter.accept(lastMessage.id());
        return session;
    }

    public @Nullable Session get(Long userId) {
        return store.get(userId);
    }

    public Session getOrInit(Long userId, Locale locale) {
        return store.computeIfAbsent(userId, it -> init(it, locale));
    }
}
