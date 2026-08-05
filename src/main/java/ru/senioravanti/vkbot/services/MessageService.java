package ru.senioravanti.vkbot.services;

import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Stream;

import ru.senioravanti.vkbot.clients.VkApiClient;
import ru.senioravanti.vkbot.config.ResourceBundleConfig;
import ru.senioravanti.vkbot.models.commands.Session;
import ru.senioravanti.vkbot.models.vk.button.VkButton;
import ru.senioravanti.vkbot.models.vk.button.VkButtonPayload;
import ru.senioravanti.vkbot.models.vk.button.actions.VkButtonActionCallback;
import ru.senioravanti.vkbot.models.vk.method.messages.MessageEdit;
import ru.senioravanti.vkbot.models.vk.method.messages.MessageKeyboard;
import ru.senioravanti.vkbot.models.vk.method.messages.MessageRequest;
import ru.senioravanti.vkbot.models.vk.method.messages.MessageSend;
import ru.senioravanti.vkbot.models.vk.method.messages.MessagesDelete;
import ru.senioravanti.vkbot.utils.MarkdownVkFormatter;

@RequiredArgsConstructor
public class MessageService {
    private final ResourceBundle.Control control;
    private final VkApiClient vkApiClient;

    public void deleteMessages(Long peerId, Long... messageIds) {
        if (peerId == null) return;
        var request = MessagesDelete.Request.of(peerId, messageIds);
        vkApiClient.call(new MessagesDelete(request));
    }

    public void sendMessage(Session session, MessageSend.Request request) {
        var messageId = vkApiClient.call(new MessageSend(request));
        session.setLastBotMessageId(messageId);
    }


    public void sendMessage(Session session, Consumer<MessageRequest.MessageRequestBuilder> messageRequestBuilderConsumer) {
        var messageRequestBuilder = MessageRequest.builder()
            .userId(session.getUser().id());
        messageRequestBuilderConsumer.accept(messageRequestBuilder);
        sendMessage(session, MessageSend.Request.builder()
            .message(messageRequestBuilder.build())
            .build());
    }

    public void sendOrEditMessage(Session session, MessageSend.Request sendRequest) {
        var lastBotMessageId = session.getLastBotMessageId();
        var lastUserMessageId = session.getLastUserMessageId();
        if (lastBotMessageId == null || (lastUserMessageId != null && lastUserMessageId > lastBotMessageId)) {
            sendMessage(session, sendRequest);
            return;
        }
        var editRequest = MessageEdit.Request.builder()
            .messageId(lastBotMessageId)
            .message(sendRequest.message())
            .build();
        vkApiClient.call(new MessageEdit(editRequest));
    }

    public void sendOrEditMessage(Session session, MessageRequest messageRequest) {
        sendOrEditMessage(session, MessageSend.Request.builder()
            .message(messageRequest)
            .build());
    }

    public void sendOrEditMessage(Session session, Consumer<MessageRequest.MessageRequestBuilder> messageRequestBuilderConsumer) {
        var messageRequestBuilder = MessageRequest.builder()
            .userId(session.getUser().id());
        messageRequestBuilderConsumer.accept(messageRequestBuilder);
        sendOrEditMessage(session, messageRequestBuilder.build());
    }

    public void sendMainMenu(Session session, boolean forceSend) {
        session.setCommand(null);
        session.cancelTask();
        var b = ResourceBundle.getBundle(ResourceBundleConfig.NAME, session.getUser().locale(), control);
        var greeting = switch (LocalTime.now().getHour()) {
            case 0, 1, 2, 3, 4, 5 -> b.getString("greeting.night");
            case 6, 7, 8, 9, 10, 11 -> b.getString("greeting.morning");
            case 12, 13, 14, 15, 16, 17 -> b.getString("greeting.day");
            default -> b.getString("greeting.evening");
        };
        var grid = Stream.of(
                new VkButtonActionCallback(b.getString("images.title"), new VkButtonPayload(VkButtonPayload.Type.IMAGES)),
                new VkButtonActionCallback(b.getString("template.title"), new VkButtonPayload(VkButtonPayload.Type.TEMPLATE)),
                new VkButtonActionCallback(b.getString("references.title"), new VkButtonPayload(VkButtonPayload.Type.REFERENCES)),
                new VkButtonActionCallback(b.getString("password.title"), new VkButtonPayload(VkButtonPayload.Type.PASSWORD))
            )
            .map(VkButton::new)
            .map(List::of)
            .toList();
        var formatted = MarkdownVkFormatter.parse(MessageFormat.format(b.getString("greeting.template"), greeting, session.getUser().firstName(), session.getUser().screenName()));
        Consumer<MessageRequest.MessageRequestBuilder> consumer = r -> r
            .text(formatted.text())
            .formatData(formatted.formatData())
            .keyboard(MessageKeyboard.fromGrid(grid));
        if (forceSend) {
            sendMessage(session, consumer);
            return;
        }
        sendOrEditMessage(session, consumer);
    }

    public void sendMainMenu(Session session) {
        sendMainMenu(session, false);
    }

    public void sendCommandPlaceholder(Session session) {
        var b = ResourceBundle.getBundle(ResourceBundleConfig.NAME, session.getUser().locale(), control);
        sendOrEditMessage(session, r -> r
            .text(b.getString("error.unknown-command"))
            .keyboard(MessageKeyboard.fromGrid(List.of(
                List.of(new VkButton(new VkButtonActionCallback(b.getString("button.back"), new VkButtonPayload(VkButtonPayload.Type.BUTTON_CANCEL))))
            )))
        );
    }
}
