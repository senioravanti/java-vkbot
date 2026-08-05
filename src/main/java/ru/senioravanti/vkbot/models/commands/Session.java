package ru.senioravanti.vkbot.models.commands;

import lombok.Getter;
import lombok.Setter;
import lombok.With;
import lombok.AccessLevel;

import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import ru.senioravanti.vkbot.models.vk.VkBirthday;

@Getter
@Setter
public final class Session {
    private User user;
    @Nullable
    private Command command;
    @Nullable
    private Long lastBotMessageId;
    @Nullable
    private Long lastUserMessageId;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Nullable
    private ScheduledFuture<?> task;

    public Session(User user) {
        this.user = user;
    }

    public synchronized void cancelTask() {
        if (task == null) return;
        task.cancel(false);
        task = null;
    }

    public synchronized void schedule(ScheduledExecutorService scheduledExecutorService, Runnable runnable, Duration duration) {
        this.task = scheduledExecutorService.schedule(runnable, duration.getSeconds(), TimeUnit.SECONDS);
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
