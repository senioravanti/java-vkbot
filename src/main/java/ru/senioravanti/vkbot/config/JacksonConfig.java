package ru.senioravanti.vkbot.config;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.util.Map;

import ru.senioravanti.vkbot.models.vk.VkBirthday;
import ru.senioravanti.vkbot.models.vk.attachments.VkAttachment;
import ru.senioravanti.vkbot.models.vk.attachments.VkAttachmentDeserializer;
import ru.senioravanti.vkbot.models.vk.method.docs.DocsSave;

public class JacksonConfig {
    private JacksonConfig() {
    }

    public static final TypeReference<Map<String, Object>> OBJECT_TYPE_REFERENCE = new TypeReference<>() {
    };

    public static JsonMapper newVkApiMapper() {
        return JsonMapper.builder()
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .addModule(new SimpleModule()
                .addDeserializer(VkAttachment.class, new VkAttachmentDeserializer())
                .addDeserializer(VkBirthday.class, new VkBirthday.Deserializer())
                .addDeserializer(DocsSave.Response.class, new DocsSave.ResponseDeserializer())
            )
            .build();
    }
}
