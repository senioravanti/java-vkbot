package ru.senioravanti.vkbot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.jayway.jsonpath.JsonPath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

import static java.util.Map.entry;

import ru.senioravanti.vkbot.config.JacksonConfig;
import ru.senioravanti.vkbot.exception.CustomException;
import ru.senioravanti.commons.loggers.CustomMapMessage;
import ru.senioravanti.vkbot.models.vk.method.messages.MessagesSend;
import ru.senioravanti.vkbot.models.vk.attachments.VkAttachment;
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

    @Test
    void shouldDeserializeAttachment() {
        var json = """
            {
                "type": "photo",
                "photo": {
                    "id": 228,
                    "owner_id": 777,
                    "date": 1785143582,
                    "sizes": [],
                    "width": 512,
                    "height": 512
                }
            }
            """;
        var jsonMapper = JacksonConfig.newVkApiMapper();
        assertDoesNotThrow(() -> {
            var attachment = jsonMapper.readValue(json, VkAttachment.class);
            LOGGER.info(new CustomMapMessage(attachment));
            assertEquals("photo", attachment.type());
        });
    }

    @Test
    void shouldNotDeserializeAttachmentOfUnknownType() {
        var json = """
            {
                "type": "abiba",
                "abiba": {
                    "id": 228,
                    "width": 512,
                    "height": 512
                }
            }
            """;
        var jsonMapper = JacksonConfig.newVkApiMapper();
        assertThrows(CustomException.class, () -> {
            jsonMapper.readValue(json, VkAttachment.class);
        });
    }

    @Test
    void shouldSerializeSendMessage() {
        var jsonMapper = JacksonConfig.newVkApiMapper();
        var messageSend = MessagesSend.Request.builder()
            .userId(7878L)
            .message("Привет, мир!")
            .peerIdList(List.of(228L, 777L, 666L))
            .attachments(List.of(
                new MessagesSend.Request.Attachment(MessagesSend.Request.Attachment.Type.DOCUMENT, "-222596926", UUID.randomUUID().toString()),
                new MessagesSend.Request.Attachment(MessagesSend.Request.Attachment.Type.PHOTO, "-222596926", UUID.randomUUID().toString())
            ))
            .contentSource(new MessagesSend.Request.ContentSourceUrl("https://vk.com/…"))
            .build();
        var json = jsonMapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(messageSend);
        IO.println(json);
        var jsonPath = JsonPath.parse(json);
        assertEquals("228,777,666", jsonPath.read("$.peer_ids"));
        assertEquals("url", jsonPath.read("$.content_source.type"));
        assertNotNull(jsonPath.read("$.attachment"));
    }
}
