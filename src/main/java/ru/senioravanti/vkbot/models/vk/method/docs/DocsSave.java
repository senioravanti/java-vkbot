package ru.senioravanti.vkbot.models.vk.method.docs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

import ru.senioravanti.vkbot.exception.CustomException;

/**
 * <a href="https://dev.vk.com/ru/method/docs.save">Документация</a>
 */
public class DocsSave {
    private DocsSave() {
    }

    public record Request(
        String file,
        @JsonInclude(Include.NON_NULL)
        String title
    ) {
        public Request(String file) {
            this(file, null);
        }
    }

    public sealed interface Response {
        Long id();

        Long ownerId();

        @Accessors(fluent = true)
        @Getter
        @RequiredArgsConstructor
        enum Type {
            DOCUMENT("doc", Document.class),
            AUDIO_MESSAGE("audio_message", AudioMessage.class);

            private static final Map<String, Type> STRING_TO_ENUM = Stream.of(values())
                .collect(toMap(Type::type, Function.identity()));

            private final String type;
            private final Class<? extends Response> javaType;

            public static Optional<Type> fromString(String type) {
                return Optional.ofNullable(STRING_TO_ENUM.get(type));
            }
        }

        /**
         * <a href="https://dev.vk.com/ru/reference/objects/doc">Документация</a>
         */
        record Document(
            @JsonProperty("id")
            Long id,
            Long ownerId,
            String title,
            Long size,
            @JsonProperty("ext")
            String extension,
            Instant date,
            Type type,
            String url
        ) implements Response {
            @Accessors(fluent = true)
            @Getter
            @RequiredArgsConstructor
            public enum Type {
                TEXT(1),
                ARCHIVE(2),
                GIF(3),
                IMAGE(4),
                AUDIO(5),
                VIDEO(6),
                EBOOK(7),
                UNKNOWN(8);

                private static final Map<Integer, Type> STRING_TO_ENUM = Stream.of(values())
                    .collect(toMap(Type::code, Function.identity()));

                @JsonValue
                private final int code;

                @JsonCreator
                public static Type fromString(int code) {
                    return Optional
                        .ofNullable(STRING_TO_ENUM.get(code))
                        .orElse(Type.UNKNOWN);
                }
            }
        }

        /**
         * <a href="https://dev.vk.com/ru/reference/objects/audio-message">Документация</a>
         */
        record AudioMessage(
            @JsonProperty("id")
            Long id,
            Long ownerId,
            Duration duration,
            int[] waveform,
            String linkOgg,
            String linkMp3
        ) implements Response {
        }
    }

    public static class ResponseDeserializer extends StdDeserializer<Response> {
        public ResponseDeserializer() { super(Response.class); }

        @Override
        public Response deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            var node = ctxt.readTree(p);
            var type = node.get("type").asString();
            return Response.Type.fromString(type)
                .map(it -> ctxt.readTreeAsValue(node.get(it.type()), it.javaType()))
                .orElseThrow(() -> new CustomException("unknown doc", Map.of("type", type)));
        }
    }
}
