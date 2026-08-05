package ru.senioravanti.vkbot.models.vk;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * <a href="https://dev.vk.com/ru/reference/objects/message">Информация о форматировании текста сообщения</a>
 * <a href="https://github.com/vknet/vk/issues/1625">Упоминание о том, что этот параметр может фигурировать и в запросе</a>
 *
 * @param version версия форматирования
 * @param items   массив объектов с параметрами форматирования
 */
public record VkFormatData(
    String version,
    List<Item> items
) {
    public VkFormatData(List<Item> items) {
        this("1", items);
    }

    /**
     * Параметр форматирования
     * За единицу диапазона принимается символ в кодировке UTF-16. Обратите внимание: некоторые объекты, например эмодзи, могут занимать несколько символов в UTF-16.
     * <br/>
     * Пример:
     * <pre><code>
     * {
     *   "version": 1,
     *   "items": [
     *     {
     *       "type": "bold",
     *       "offset": 40,
     *       "length": 12
     *     },
     *     {
     *       "type": "italic",
     *       "offset": 54,
     *       "length": 15
     *     },
     *     {
     *       "type": "underline",
     *       "offset": 72,
     *       "length": 18
     *     }
     *   ]
     * }
     * </code></pre>
     * Для текста: "Это сообщение с форматированием текста. Жирный текст. Курсивный текст.  Подчёркнутый текст."
     *
     * @param offset позиция символа, начиная с которого применяется указанный тип форматирования
     * @param length количество символов, на которые распространяется указанный тип форматирования
     * @param type   тип форматирования, примененный к диапазону символов
     * @param url    адрес ссылки, если в type указан тип url
     */
    public record Item(
        Type type,
        Integer offset,
        Integer length,
        @JsonInclude(Include.NON_NULL)
        String url
    ) {
        public Item(Type type, Integer offset, Integer length) {
            this(type, offset, length, null);
        }

        @RequiredArgsConstructor
        @Accessors(fluent = true)
        @Getter
        public enum Type {
            BOLD("bold"),
            ITALIC("italic"),
            URL("url"),
            UNDERLINE("underline");

            private static final Map<String, Type> STRING_TO_ENUM = Stream.of(values())
                .collect(java.util.stream.Collectors.toMap(Type::code, it -> it));

            @JsonValue
            public final String code;

            @JsonCreator
            public static Type fromString(String code) {
                return STRING_TO_ENUM.get(code);
            }
        }
    }
}
