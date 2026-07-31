package ru.senioravanti.vkbot.models.vk.method.messages.templates;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// https://dev.vk.com/ru/api/bots/development/messages#%D0%A8%D0%B0%D0%B1%D0%BB%D0%BE%D0%BD%D1%8B%20%D1%81%D0%BE%D0%BE%D0%B1%D1%89%D0%B5%D0%BD%D0%B8%D0%B9
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type",
    include = JsonTypeInfo.As.PROPERTY
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = MessageTemplateCarousel.class, name = MessageTemplateCarousel.TYPE)
})
public sealed interface MessageTemplate
    permits MessageTemplateCarousel
{
    String type();
}
