package ru.senioravanti.vkbot.models.vk.method.users;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jspecify.annotations.Nullable;

import com.fasterxml.jackson.annotation.*;
import ru.senioravanti.vkbot.models.vk.VkResponse;
import tools.jackson.core.type.TypeReference;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import ru.senioravanti.vkbot.models.vk.VkBirthday;
import ru.senioravanti.vkbot.models.vk.method.Method;

/**
 * <a href="https://dev.vk.ru/ru/method/users.get">Документация</a>
 */
public record UserGet(Request params) implements Method<UserGet.Request, List<UserGet.Response>> {
    @Override
    public String name() {
        return "users.get";
    }

    @Override
    public TypeReference<VkResponse<List<Response>>> responseType() {
        return new TypeReference<>() {
        };
    }

    /**
     * @param userIdList идентификаторы пользователей или их короткие имена
     * @param fieldList  список дополнительных полей
     * @param nameCase   падеж для склонения имени и фамилии пользователя
     */
    public record Request(
        @JsonIgnore
        List<String> userIdList,
        @JsonIgnore
        List<Field> fieldList,
        @JsonInclude(Include.NON_NULL)
        NameCase nameCase
    ) {
        public static Request of(Long... userIds) {
            return new Request(
                Arrays.stream(userIds).map(String::valueOf).collect(toList()),
                List.of(Field.SCREEN_NAME, Field.BIRTH_DAY),
                NameCase.NOM
            );
        }

        /**
         * Падеж имени и фамилии
         *
         * @see #NOM
         * @see #GEN
         * @see #DAT
         * @see #ACC
         * @see #INS
         * @see #ABL
         */
        @RequiredArgsConstructor
        public enum NameCase {
            /**
             * именительный
             */
            NOM("nom"),
            /**
             * родительный
             */
            GEN("gen"),
            /**
             * дательный
             */
            DAT("dat"),
            /**
             * винительный
             */
            ACC("acc"),
            /**
             * творительный
             */
            INS("ins"),
            /**
             * предложный
             */
            ABL("abl");

            private final String name;

            @JsonValue
            public String getName() {
                return name;
            }
        }

        /**
         * Опциональные поля
         */
        @Getter
        @RequiredArgsConstructor
        public enum Field {
            SCREEN_NAME("screen_name"),
            BIRTH_DAY("bdate");

            private final String name;

            @JsonValue
            public String getName() {
                return name;
            }

            public static Field of(String name) {
                if (name == null) return null;
                return Arrays.stream(Field.values())
                    .filter(it -> it.name.equals(name))
                    .findFirst()
                    .orElse(null);
            }
        }

        @JsonGetter("fields")
        public String fields() {
            return fieldList.stream()
                .map(Field::getName)
                .collect(joining(","));
        }

        @JsonGetter("user_ids")
        public String userIds() {
            return String.join(",", userIdList);
        }
    }

    public record Response(
        Long id,
        String lastName,
        String firstName,
        @Nullable
        Deactivated deactivated,
        Boolean isClosed,
        Boolean canAccessClosed,
        @Nullable
        String screenName,
        @Nullable
        @JsonProperty("bdate")
        VkBirthday birthday
    ) {
        @RequiredArgsConstructor
        public enum Deactivated {
            DELETED("deleted"),
            BANNED("banned");

            private final String name;

            @JsonValue
            public String getName() {
                return name;
            }
        }
    }
}
