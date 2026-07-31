package ru.senioravanti.vkbot.models.vk.method.messages.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

import ru.senioravanti.vkbot.exception.ValidationCustomException;
import ru.senioravanti.vkbot.models.vk.button.VkButton;

// https://dev.vk.com/ru/api/bots/development/messages#%D0%9E%D1%82%D0%BF%D1%80%D0%B0%D0%B2%D0%BA%D0%B0%20%D0%BA%D0%B0%D1%80%D1%83%D1%81%D0%B5%D0%BB%D0%B5%D0%B9
@JsonIgnoreProperties(ignoreUnknown = true)
public record MessageTemplateCarousel(
    List<Element> elements
) implements MessageTemplate {
    public static final String TYPE = "carousel";

    public record Element(
        String title, // Заголовок, максимум 80 символов
        String description, // Подзаголовок, максимум 80 символов
        String photoId, // ID изображения, которое надо прикрепить
        List<VkButton> buttons, // Один элемент карусели может содержать не больше 3-х кнопок
        Action action
    ) {
        private static final int MAX_BUTTONS = 3;

        public Element {
            if (title == null && photoId == null) {
                throw new ValidationCustomException("either photoId or title must be specified");
            }
            if (title != null && description == null) {
                throw new ValidationCustomException(ValidationCustomException.Error.builder()
                    .name("description")
                    .invalidValue(null)
                    .message("description is required when title is specified")
                    .build());
            }
            if (buttons.size() > MAX_BUTTONS) {
                throw new ValidationCustomException(ValidationCustomException.Error.builder()
                    .name("buttons")
                    .invalidValue(buttons.size())
                    .message("size must be <= " + MAX_BUTTONS)
                    .build());
            }
        }


        @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            property = "type",
            include = JsonTypeInfo.As.PROPERTY
        )
        @JsonSubTypes({
            @JsonSubTypes.Type(value = OpenPhotoAction.class, name = OpenPhotoAction.TYPE),
            @JsonSubTypes.Type(value = OpenLinkAction.class, name = OpenLinkAction.TYPE),
        })
        public sealed interface Action permits OpenPhotoAction, OpenLinkAction {
            String type();
        }

        public record OpenPhotoAction() implements Action {
            public static final String TYPE = "open_photo";

            @Override
            public String type() {
                return TYPE;
            }
        }

        public record OpenLinkAction(String link) implements Action {
            public static final String TYPE = "open_link";

            @Override
            public String type() {
                return TYPE;
            }
        }
    }

    @Override
    public String type() {
        return TYPE;
    }
}
