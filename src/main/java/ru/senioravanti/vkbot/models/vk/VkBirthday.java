package ru.senioravanti.vkbot.models.vk;

import org.jspecify.annotations.Nullable;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * @param day
 * @param month
 * @param year  null если год рождения скрыт
 */
public record VkBirthday(
    short day,
    short month,
    @Nullable
    Integer year
) {
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
        .appendOptional(DateTimeFormatter.ofPattern("d.M.yyyy"))
        .appendOptional(DateTimeFormatter.ofPattern("d.M"))
        .toFormatter();

    @Override
    public String toString() {
        return year == null
            ? "%d.%d".formatted(day, month)
            : "%02d.%02d.%d".formatted(day, month, year);
    }

    public static class Deserializer extends StdDeserializer<VkBirthday> {
        public Deserializer() {
            super(VkBirthday.class);
        }

        @Override
        public VkBirthday deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            var temporalAccessor = FORMATTER.parse(p.getString());
            var day = (short) temporalAccessor.get(ChronoField.DAY_OF_MONTH);
            var month = (short) temporalAccessor.get(ChronoField.MONTH_OF_YEAR);
            var year = temporalAccessor.isSupported(ChronoField.YEAR)
                ? temporalAccessor.get(ChronoField.YEAR)
                : null;
            return new VkBirthday(day, month, year);
        }
    }
}
