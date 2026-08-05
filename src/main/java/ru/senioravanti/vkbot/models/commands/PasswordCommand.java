package ru.senioravanti.vkbot.models.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import ru.senioravanti.vkbot.models.PasswordEncoder;

@Getter
@Setter
public final class PasswordCommand implements Command {
    private Step step;
    private Integer count;
    private Short length;
    private PasswordEncoder encoder;

    public PasswordCommand() {
        step = Step.LENGTH;
    }

    public enum Step {
        LENGTH,
        COUNT,
        ENCODER;
    }
}
