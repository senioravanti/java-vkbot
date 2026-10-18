package ru.senioravanti.vkbot.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.commons.text.StringSubstitutor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import ru.senioravanti.commons.loggers.CustomMapMessage;
import ru.senioravanti.vkbot.config.JacksonConfig;

public class BannerUtils {
    private static final Logger LOGGER = LogManager.getLogger(BannerUtils.class);

    public record Props(
        String artifactId,
        String groupId,
        String version
    ) {
        public static Props load() {
            return new Props(
                System.getProperty("app.artifact-id", ""),
                System.getProperty("app.group", ""),
                System.getProperty("app.version", "")
            );
        }
    }

    public static void printBanner(Props props) {
        try (var is = BannerUtils.class.getClassLoader().getResourceAsStream("banner.txt")) {
            if (is == null) {
                LOGGER.warn("failed to load banner.txt");
                return;
            }
            var banner = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            var substitutor = new StringSubstitutor(JacksonConfig.INSTANCE.jsonMapper().convertValue(props, JacksonConfig.JSON_MAP));
            System.out.println(substitutor.replace(banner));
        } catch (IOException ex) {
            LOGGER.error(CustomMapMessage.of("failed to load banner", ex));
        }
    }
}
