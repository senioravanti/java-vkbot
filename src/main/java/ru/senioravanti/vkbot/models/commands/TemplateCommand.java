package ru.senioravanti.vkbot.models.commands;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.experimental.Accessors;

import org.jspecify.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;

import ru.senioravanti.vkbot.exception.CustomException;

@Getter
@Setter
@AllArgsConstructor
public final class TemplateCommand implements Command {
    private Step step;
    @Nullable
    private Type type;
    @Nullable
    private Integer number;
    @Nullable
    private String title;
    @Nullable
    private String content;

    public TemplateCommand() {
        step = Step.TYPE;
    }

    public enum Step {
        TYPE,
        NUMBER,
        TITLE,
        CONTENT;
    }

    @RequiredArgsConstructor
    @Accessors(fluent = true)
    @Getter
    public enum Type {
        LAB("lab", true),
        PRACTICE("practice", true),
        COURSE_WORK("course-work", false);

        @JsonValue
        private final String code;
        private final boolean numerable;

        @JsonCreator
        public static Type of(String code) {
            return Arrays.stream(Type.values())
                .filter(it -> it.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new CustomException("unknown template type", Map.of("type", code)));
        }
    }
}
