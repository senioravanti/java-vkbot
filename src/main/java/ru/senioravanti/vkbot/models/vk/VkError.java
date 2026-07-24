package ru.senioravanti.vkbot.models.vk;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jspecify.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

public record VkError(
    @JsonProperty("error_code")
    Code code,
    @JsonProperty("error_msg")
    String message,
    List<Pair> requestParams
) {
    public record Pair(
        String key,
        String value
    ) {
    }

    // https://dev.vk.com/ru/reference/errors
    @Getter
    @RequiredArgsConstructor
    public enum Code {
        UNKNOWN_ERROR(1, "Произошла неизвестная ошибка", "Попробуйте повторить запрос позже"),
        METHOD_NOT_FOUND(3, "Передан неизвестный метод", "Проверьте, правильно ли указано название вызываемого метода: vk.com/dev/methods"),
        AUTH_FAILED(5, "Авторизация пользователя не удалась", "Убедитесь, что Вы используете верную схему авторизации"),
        TOO_MANY_REQUESTS(6, "Слишком много запросов в секунду", "Задайте больший интервал между вызовами или используйте метод execute. Подробнее об ограничениях на частоту вызовов см. на странице vk.com/dev/api_requests"),
        PERMISSION_DENIED(7, "Нет прав для выполнения этого действия", "Проверьте, получены ли нужные права доступа при авторизации. Это можно сделать с помощью метода account.getAppPermissions"),
        INVALID_REQUEST(8, "Неверный запрос", "Проверьте синтаксис запроса и список используемых параметров (его можно найти на странице с описанием метода)"),
        FLOOD_CONTROL(9, "Слишком много однотипных действий", "Нужно сократить число однотипных обращений"),
        INTERNAL_SERVER_ERROR(10, "Произошла внутренняя ошибка сервера", "Попробуйте повторить запрос позже"),
        ACCESS_DENIED(15, "Доступ запрещён", "Убедитесь, что Вы используете верные идентификаторы, и доступ к контенту для текущего пользователя есть в полной версии сайта"),
        VALIDATION_REQUIRED(17, "Требуется подтверждение действия", "Требуется подтверждение действия"),
        USER_DELETED_OR_BANNED(18, "Страница удалена или заблокирована", "Страница пользователя была удалена или заблокирована"),
        METHOD_DISABLED(23, "Метод был выключен", "Все актуальные методы ВК API, которые доступны в настоящий момент, перечислены здесь: vk.com/dev/methods"),
        CONFIRMATION_REQUIRED(24, "Требуется подтверждение действия", null),
        GROUP_AUTH_FAILED(27, "Ключ доступа сообщества недействителен", null),
        RATE_LIMIT(29, "Достигнут количественный лимит на вызов метода", null),
        PRIVATE_PROFILE(30, "Профиль является приватным", "Информация, запрашиваемая о профиле, недоступна с используемым ключом доступа"),
        INVALID_USER_ID(113, "Неверный идентификатор пользователя", "Убедитесь, что Вы используете верный идентификатор. Получить ID по короткому имени можно методом utils.resolveScreenName");

        private final int code;
        private final String message;
        @Nullable
        private final String description;

        @JsonValue
        public int getCode() {
            return code;
        }

        @JsonCreator
        public static Code from(int code) {
            return Arrays.stream(Code.values())
                .filter(error -> error.code == code)
                .findFirst()
                .orElse(UNKNOWN_ERROR);
        }
    }
}
