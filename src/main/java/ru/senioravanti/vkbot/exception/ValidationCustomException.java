package ru.senioravanti.vkbot.exception;

import lombok.Builder;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ValidationCustomException extends CustomException {
    public ValidationCustomException(String message, Error... errors) {
        Map<String, Object> params = errors.length > 0
            ? Map.of("errors", Arrays.asList(errors))
            : null;
        super(message, params);
    }

    public ValidationCustomException(List<Error> errors) {
        super("validation", Map.of("errors", errors));
    }

    public ValidationCustomException(Error error) {
        super("validation", Map.of("error", error));
    }

    @Builder
    public record Error(
        String name,
        String message,
        @JsonInclude(Include.NON_NULL)
        Object invalidValue,
        @JsonInclude(Include.NON_NULL)
        List<Object> validValues
    ) {
    }
}
