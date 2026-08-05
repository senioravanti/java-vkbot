package ru.senioravanti.vkbot.handlers;

import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

import ru.senioravanti.vkbot.clients.VkApiClient;
import ru.senioravanti.vkbot.config.ResourceBundleConfig;
import ru.senioravanti.vkbot.exception.CustomException;
import ru.senioravanti.vkbot.models.PasswordEncoder;
import ru.senioravanti.vkbot.models.commands.PasswordCommand;
import ru.senioravanti.vkbot.models.commands.Session;
import ru.senioravanti.vkbot.models.vk.button.VkButton;
import ru.senioravanti.vkbot.models.vk.button.VkButtonPayload;
import ru.senioravanti.vkbot.models.vk.button.actions.VkButtonActionCallback;
import ru.senioravanti.vkbot.models.vk.events.VkEventMessage;
import ru.senioravanti.vkbot.models.vk.method.messages.MessageAttachment;
import ru.senioravanti.vkbot.models.vk.method.messages.MessageKeyboard;
import ru.senioravanti.vkbot.models.vk.method.messages.MessageRequest;
import ru.senioravanti.vkbot.services.MessageService;
import ru.senioravanti.vkbot.utils.KeyboardUtils;
import ru.senioravanti.vkbot.utils.MarkdownVkFormatter;
import ru.senioravanti.vkbot.utils.PasswordGenerator;

import static ru.senioravanti.vkbot.models.vk.button.VkButtonPayload.Type;
import static ru.senioravanti.vkbot.models.vk.method.messages.MessageRequest.MAX_MESSAGE_LENGTH;

@RequiredArgsConstructor
public class PasswordCommandHandler implements
    CommandHandler.Callback,
    CommandHandler.Message<PasswordCommand> {
    private static final String PREFIX = "password";
    private static final int[] STANDARD_LENGTHS = {
        6, 8, 12, 24,
        32, 48, 72, 128
    };
    private static final int[] STANDARD_COUNTS = {
        1, 2, 4, 6,
        8, 24, 36, 128,
    };

    private final ResourceBundle.Control control;
    private final VkApiClient vkApiClient;
    private final MessageService messageService;
    private final ScheduledExecutorService scheduledExecutorService;


    @Override
    public EnumMap<Type, CallbackHandler> callbackHandlers() {
        return new EnumMap<>(Map.of(
            Type.PASSWORD, this::onStart,
            Type.PASSWORD_SELECT_COUNT, this::onSelectCount,
            Type.PASSWORD_SELECT_LENGTH, this::onSelectLength,
            Type.PASSWORD_SELECT_ENCODER, this::onSelectEncoder
        ));
    }

    @Override
    public Class<PasswordCommand> commandType() { return PasswordCommand.class; }

    @Override
    public void onMessage(Session session, VkEventMessage.New evt) {
        var cmd = (PasswordCommand) Objects.requireNonNull(session.getCommand());
        switch (cmd.getStep()) {
            case LENGTH -> onWriteLength(session, evt);
            case COUNT -> onWriteCount(session, evt);
            case ENCODER -> onWriteEncoder(session, evt);
        }
    }

    private void onStart(Session session, VkEventMessage.Event evt) {
        session.setCommand(new PasswordCommand());
        var b = ResourceBundle.getBundle(ResourceBundleConfig.NAME, session.getUser().locale(), control);
        var grid = KeyboardUtils.newNumKeyboard(Type.PASSWORD_SELECT_LENGTH, STANDARD_LENGTHS);
        grid.add(KeyboardUtils.newCancelButtonRow(b));
        messageService.sendOrEditMessage(session, r -> r
            .text(b.getString(PREFIX + ".length"))
            .keyboard(MessageKeyboard.fromGrid(grid))
        );
    }

    private void onLength(Session session, short length) {
        var cmd = (PasswordCommand) Objects.requireNonNull(session.getCommand());
        cmd.setLength(length);
        cmd.setStep(PasswordCommand.Step.COUNT);
        var b = ResourceBundle.getBundle(ResourceBundleConfig.NAME, session.getUser().locale(), control);
        var grid = KeyboardUtils.newNumKeyboard(Type.PASSWORD_SELECT_COUNT, STANDARD_COUNTS);
        grid.add(KeyboardUtils.newCancelButtonRow(b));
        messageService.sendOrEditMessage(session, r -> r
            .text(b.getString(PREFIX + ".count"))
            .keyboard(MessageKeyboard.fromGrid(grid))
        );
    }

    private void onSelectLength(Session session, VkEventMessage.Event evt) {
        short length = Short.parseShort(Objects.requireNonNull(evt.payload().text()));
        onLength(session, length);
    }

    private void onWriteLength(Session session, VkEventMessage.New evt) {
        try {
            short length = Short.parseShort(evt.message().text().trim());
            if (length < 1) throw new NumberFormatException();
            onLength(session, length);
        } catch (NumberFormatException ex) {
            var b = ResourceBundle.getBundle(ResourceBundleConfig.NAME, session.getUser().locale(), control);
            messageService.sendMessage(session, r -> r
                .text(MessageFormat.format(b.getString("error.number-format.integer"), 1, Short.MAX_VALUE))
            );
        }
    }

    private void onCount(Session session, int count) {
        var cmd = (PasswordCommand) Objects.requireNonNull(session.getCommand());
        cmd.setCount(count);
        cmd.setStep(PasswordCommand.Step.ENCODER);
        var b = ResourceBundle.getBundle(ResourceBundleConfig.NAME, session.getUser().locale(), control);
        messageService.sendOrEditMessage(session, r -> r
            .text(b.getString(PREFIX + ".encoder"))
            .keyboard(KeyboardUtils.newPasswordEncoderKeyboard(b))
        );
    }

    private void onSelectCount(Session session, VkEventMessage.Event evt) {
        int count = Integer.parseInt(Objects.requireNonNull(evt.payload().text()));
        onCount(session, count);
    }

    private void onWriteCount(Session session, VkEventMessage.New evt) {
        try {
            int count = Integer.parseInt(evt.message().text().trim());
            if (count < 1) throw new NumberFormatException();
            onCount(session, count);
        } catch (NumberFormatException ex) {
            var b = ResourceBundle.getBundle(ResourceBundleConfig.NAME, session.getUser().locale(), control);
            messageService.sendMessage(session, r -> r
                .text(MessageFormat.format(b.getString("error.number-format.integer"), 1, Integer.MAX_VALUE))
            );
        }
    }

    private void onEncoder(Session session, PasswordEncoder passwordEncoder) {
        var cmd = (PasswordCommand) Objects.requireNonNull(session.getCommand());
        var b = ResourceBundle.getBundle(ResourceBundleConfig.NAME, session.getUser().locale(), control);
        var formatted = MarkdownVkFormatter.parse(b.getString(PREFIX + ".result"));
        var delimiter = "\n\n";
        var passwords = IntStream
            .range(0, cmd.getCount())
            .mapToObj(_ -> PasswordGenerator.generate(passwordEncoder, cmd.getLength()))
            .collect(joining(delimiter));
        var message = formatted.text() + delimiter + passwords;
        var userId = session.getUser().id();
        var messageRequestBuilder = MessageRequest.builder()
            .userId(userId)
            .formatData(formatted.formatData());
        if (message.length() > MAX_MESSAGE_LENGTH) {
            message = formatted.text();
            try (var is = new ByteArrayInputStream(passwords.getBytes(StandardCharsets.UTF_8))) {
                var attachment = vkApiClient.uploadDocument(userId, is);
                messageRequestBuilder.attachments(List.of(
                    new MessageAttachment(MessageAttachment.Type.DOCUMENT, attachment.id(), attachment.ownerId())
                ));
            } catch (IOException ex) {
                throw new IllegalStateException("failed to upload passwords as file", ex);
            }
        }
        session.cancelTask();
        session.schedule(scheduledExecutorService, () -> messageService.sendMainMenu(session), Duration.ofMinutes(1));
        messageService.sendOrEditMessage(session, messageRequestBuilder
            .text(message)
            .keyboard(MessageKeyboard.fromButton(KeyboardUtils.newCancelButton(b.getString("button.main-menu"))))
            .build());
    }

    private void onSelectEncoder(Session session, VkEventMessage.Event evt) {
        var passwordEncoder = PasswordEncoder
            .fromString(Objects.requireNonNull(evt.payload().text()))
            .orElseThrow(() -> new CustomException("unknown", Map.of("encoder", evt.payload().text())));
        onEncoder(session, passwordEncoder);
    }

    public void onWriteEncoder(Session session, VkEventMessage.New evt) {
        var passwordEncoder = PasswordEncoder.fromString(evt.message().text().trim());
        if (passwordEncoder.isPresent()) {
            onEncoder(session, passwordEncoder.get());
            return;
        }
        var b = ResourceBundle.getBundle(ResourceBundleConfig.NAME, session.getUser().locale(), control);
        messageService.sendOrEditMessage(session, r -> r
            .text(MessageFormat.format(b.getString("error.illegal-argument.enum"), Stream.of(PasswordEncoder.values()).map(PasswordEncoder::code).collect(joining(", "))))
            .keyboard(KeyboardUtils.newPasswordEncoderKeyboard(b))
        );
    }
}
