package ru.senioravanti.vkbot;

import org.apache.commons.text.StringSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.util.LinkedHashMap;
import java.util.Map;

import ru.senioravanti.vkbot.config.JacksonConfig;
import ru.senioravanti.commons.loggers.CustomMapMessage;
import ru.senioravanti.commons.loggers.LoggersConfig;
import ru.senioravanti.commons.loggers.LoggersConfigHolder;
import ru.senioravanti.commons.result.Result;
import ru.senioravanti.vkbot.clients.VkApiClient;
import ru.senioravanti.vkbot.bot.Bot;
import ru.senioravanti.vkbot.bot.MessageHandler;
import ru.senioravanti.vkbot.bot.Router;
import ru.senioravanti.vkbot.config.ResourceBundleConfig;
import ru.senioravanti.vkbot.exception.CustomException;
import ru.senioravanti.vkbot.models.ConfigurationProperties;
import ru.senioravanti.vkbot.utils.ConfigurationLoader;

import static java.util.Map.entry;

public class App {
    private static final Logger LOGGER = LogManager.getLogger(App.class);

    private static void printBanner(ConfigurationProperties.App props) {
        try (var is = App.class.getClassLoader().getResourceAsStream("banner.txt")) {
            if (is == null) {
                LOGGER.warn("failed to load banner.txt");
                return;
            }
            var banner = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            var substitutor = new StringSubstitutor(Map.of(
                "groupId", props.groupId(),
                "artifactId", props.artifactId(),
                "version", props.version()
            ));
            System.out.println(substitutor.replace(banner));
        } catch (IOException ex) {
            LOGGER.error(CustomMapMessage.of("failed to load banner", ex));
        }
    }

    static void main() {
        var props = Result
            .from(() -> ConfigurationLoader.loadConfig("config.yaml"))
            .handle(Exception.class, ex -> {
                LOGGER.error(CustomMapMessage.of("failed to load configuration properties", ex));
                System.exit(1);
            })
            .get();
        LoggersConfigHolder.setConfig(new LoggersConfig(
            App.class.getPackageName(),
            props.loggers().maxExternalFrames(),
            props.loggers().maxCauseDepth()
        ));
        printBanner(props.app());
        var app = new App();
        app.start(props);
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            if (ex instanceof CustomException cex) {
                var params = new LinkedHashMap<>(cex.getParams());
                params.put("thread", thread.toString());
                LOGGER.error(new CustomMapMessage(cex.getMessage(), params));
                return;
            }
            LOGGER.error(CustomMapMessage.of("uncaught exception", ex, entry("thread", thread.toString())));
        });
        try {
            Thread.currentThread().join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private Thread botThread;

    private void start(ConfigurationProperties props) {
        var httpClient = HttpClient.newHttpClient();
        var vkApiJsonMapper = JacksonConfig.newVkApiMapper();
        var control = ResourceBundleConfig.newControl();

        var vkApiClient = new VkApiClient(props.vk().api(), httpClient, vkApiJsonMapper);

        var messageHandler = new MessageHandler(control, vkApiClient);

        var router = new Router(vkApiJsonMapper, messageHandler);

        var bot = new Bot(props.vk().groupId(), httpClient, vkApiJsonMapper, vkApiClient, router);
        botThread = Thread.ofPlatform()
            .name("vk-bot")
            .start(bot);
    }

    private void stop() {
        LOGGER.info("stopping vk bot ...");
        if (botThread != null) botThread.interrupt();
    }
}
