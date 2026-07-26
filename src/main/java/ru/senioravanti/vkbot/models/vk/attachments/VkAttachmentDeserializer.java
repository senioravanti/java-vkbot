package ru.senioravanti.vkbot.models.vk.attachments;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.Map;
import java.util.Optional;

import ru.senioravanti.vkbot.exception.CustomException;

public class VkAttachmentDeserializer extends StdDeserializer<VkAttachment> {
    private static final Map<String, Class<? extends VkAttachment>> TYPES = Map.of(
        VkAttachmentPhoto.TYPE, VkAttachmentPhoto.class,
        VkAttachmentLink.TYPE, VkAttachmentLink.class,
        VkAttachmentDocument.TYPE, VkAttachmentDocument.class
    );

    public VkAttachmentDeserializer() {
        super(VkAttachment.class);
    }

    @Override
    public VkAttachment deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        var node = ctxt.readTree(p);
        var type = node.get("type").asString();
        return Optional
            .ofNullable(TYPES.get(type))
            .map(it -> ctxt.readTreeAsValue(node.get(type), it))
            .orElseThrow(() -> new CustomException("unknown attachment type", Map.of("type", type)));
    }
}
