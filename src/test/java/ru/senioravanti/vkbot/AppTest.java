package ru.senioravanti.vkbot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.ResourceBundle;

import static java.util.Map.entry;

import ru.senioravanti.commons.loggers.CustomMapMessage;
import ru.senioravanti.vkbot.utils.YamlResourceBundle;

public class AppTest {
    private static final Logger LOGGER = LogManager.getLogger(AppTest.class);

    @Test
    void shouldLoadResourceBundle() {
        var control = new YamlResourceBundle.Control();
        var bundle = "i18n.messages";
        var ruLocale = Locale.forLanguageTag("ru");
        var enBundle = ResourceBundle.getBundle(bundle, Locale.ENGLISH, control);
        var enTr = enBundle.getString("hello.my.world");
        var ruBundle = ResourceBundle.getBundle(bundle, ruLocale, control);
        var ruTr = ruBundle.getString("hello.my.world");
        var frBundle = ResourceBundle.getBundle(bundle, Locale.FRENCH, control);
        var frTr = frBundle.getString("hello.my.world");
        LOGGER.info(CustomMapMessage.of("translations",
            entry("ruCandidateLocales", control.getCandidateLocales(bundle, ruLocale)),
            entry("frCandidateLocales", control.getCandidateLocales(bundle, Locale.FRENCH)),
            entry("fallbackLocale", control.getFallbackLocale(bundle, Locale.FRENCH)),
            entry("ru", ruTr),
            entry("en", enTr),
            entry("fr", frTr)
        ));
        assertEquals("Hello world!", enTr);
        assertEquals("Привет мир!", ruTr);
        // Для французского fallback locale -> en-US => трансляции из messages.yaml будут использованы только если нет messages_en.yaml
        assertEquals("Hello world!", frTr);
    }
}
