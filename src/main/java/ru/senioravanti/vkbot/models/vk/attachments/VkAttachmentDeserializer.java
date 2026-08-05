package ru.senioravanti.vkbot.models.vk.attachments;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.Map;

import ru.senioravanti.vkbot.exception.CustomException;

public class VkAttachmentDeserializer extends StdDeserializer<VkAttachment> {
    public VkAttachmentDeserializer() { super(VkAttachment.class); }

    @Override
    public VkAttachment deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        var node = ctxt.readTree(p);
        var type = node.get("type").asString();
        return VkAttachment.Type.fromString(type)
            .map(it -> ctxt.readTreeAsValue(node.get(it.type()), it.javaType()))
            .orElseThrow(() -> new CustomException("unknown attachment", Map.of("type", type)));
    }
}
