package ru.senioravanti.vkbot.models.commands;

import lombok.Getter;
import lombok.Setter;
import lombok.With;

import org.jspecify.annotations.Nullable;

import ru.senioravanti.vkbot.models.vk.VkBirthday;

import java.util.Locale;

@Getter
@Setter
public final class Session {
    private User user;
    private Command command;
    @Nullable
    private Long lastMessageId;

    public Session(User user) {
        this.user = user;
    }

    public record User(
        long id,
        String lastName,
        String firstName,
        String screenName,
        VkBirthday birthday,
        @With
        Locale locale
    ) {
    }
}
