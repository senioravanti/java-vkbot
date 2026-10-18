package ru.senioravanti.vkbot.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.util.List;
import java.util.Map;

import ru.senioravanti.vkbot.models.vk.VkBirthday;
import ru.senioravanti.vkbot.models.vk.attachments.VkAttachment;
import ru.senioravanti.vkbot.models.vk.attachments.VkAttachmentDeserializer;
import ru.senioravanti.vkbot.models.vk.method.docs.DocsSave;
import tools.jackson.dataformat.yaml.YAMLMapper;

@Accessors(fluent = true)
@Getter
public enum JacksonConfig {
    INSTANCE;

    public static final TypeReference<Map<String, Object>> JSON_MAP = new TypeReference<>() { };
    public static final TypeReference<List<Object>> JSON_ARRAY = new TypeReference<>() { };

    private final YAMLMapper yamlMapper = new YAMLMapper();

    private final JsonMapper jsonMapper = new JsonMapper();

    private final JsonMapper vkJsonMapper = JsonMapper.builder()
        .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .addModule(new SimpleModule()
            .addDeserializer(VkAttachment.class, new VkAttachmentDeserializer())
            .addDeserializer(VkBirthday.class, new VkBirthday.Deserializer())
            .addDeserializer(DocsSave.Response.class, new DocsSave.ResponseDeserializer())
        )
        .build();
}
