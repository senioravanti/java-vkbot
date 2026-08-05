package ru.senioravanti.vkbot.models.vk.attachments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * <a href="https://dev.vk.com/ru/reference/objects/attachments-message"></a>
 * <pre>
 * [
 *   {
 *     "type": "photo",
 *     "photo": <объект photo>
 *   },
 *   {
 *     "type": "audio",
 *     "audio": <объект audio>
 *   }
 * ]
 * </pre>
 */
public sealed interface VkAttachment {
    @Accessors(fluent = true)
    @Getter
    @RequiredArgsConstructor
    enum Type {
        PHOTO("photo", Photo.class),
        LINK("link", Link.class),
        DOCUMENT("doc", Document.class);

        private static final Map<String, Type> STRING_TO_ENUM = Stream.of(values())
            .collect(toMap(Type::type, Function.identity()));

        private final String type;
        private final Class<? extends VkAttachment> javaType;

        public static Optional<Type> fromString(String type) {
            return Optional.ofNullable(STRING_TO_ENUM.get(type));
        }
    }

    /**
     * @param id
     * @param ownerId идентификатор владельца фотографии
     * @param date
     * @param sizes   массив с копиями изображения в разных размерах
     * @param width   ширина оригинала фотографии в пикселях
     * @param height  высота оригинала фотографии в пикселях
     */
    record Photo(
        Long id,
        Long ownerId,
        Instant date,
        List<Size> sizes,
        Integer width,
        Integer height
    ) implements VkAttachment {
        public record Size(
            String type,
            String url,
            Integer height,
            Integer width
        ) {
        }

    }

    /**
     * @param url
     * @param title
     * @param caption     подпись ссылки.
     * @param description
     * @param photo       изображение превью.
     */
    record Link(
        String url,
        String title,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String caption,
        String description,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Photo photo
    ) implements VkAttachment {
    }

    /**
     * @param id
     * @param ownerId
     * @param title
     * @param size
     * @param ext
     * @param url
     * @param date
     */
    record Document(
        Long id,
        Long ownerId,
        String title,
        Long size, // Размер файла в байтах
        String ext, // Расширение файла
        String url, // Адрес файла, по которому его можно загрузить
        Instant date
    ) implements VkAttachment {
    }
}
