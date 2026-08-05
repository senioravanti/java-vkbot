package ru.senioravanti.vkbot.handlers;

import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import ru.senioravanti.vkbot.config.ResourceBundleConfig;
import ru.senioravanti.vkbot.models.commands.Session;
import ru.senioravanti.vkbot.models.commands.TemplateCommand;
import ru.senioravanti.vkbot.models.vk.button.VkButton;
import ru.senioravanti.vkbot.models.vk.button.actions.VkButtonActionCallback;
import ru.senioravanti.vkbot.models.vk.events.VkEventMessage;
import ru.senioravanti.vkbot.models.vk.method.messages.MessageKeyboard;
import ru.senioravanti.vkbot.models.vk.method.messages.MessageRequest;
import ru.senioravanti.vkbot.models.vk.button.VkButtonPayload;
import ru.senioravanti.vkbot.services.MessageService;
import ru.senioravanti.vkbot.utils.KeyboardUtils;

import static ru.senioravanti.vkbot.models.vk.button.VkButtonPayload.Type;

@RequiredArgsConstructor
public class TemplateCommandHandler implements CommandHandler.Callback {
    private static final Logger LOGGER = LogManager.getLogger(TemplateCommandHandler.class);
    private static final String PREFIX = "template";

    private final ResourceBundle.Control control;
    private final MessageService messageService;

    @Override
    public EnumMap<Type, CallbackHandler> callbackHandlers() {
        return new EnumMap<>(Map.of(
            Type.TEMPLATE, this::onStart,
            Type.TEMPLATE_SELECT_TYPE, this::onSelectType,
            Type.TEMPLATE_SELECT_NUMBER, this::onSelectNumber
        ));
    }

    private void onStart(Session session, VkEventMessage.Event evt) {
        session.setCommand(new TemplateCommand());
        var b = ResourceBundle.getBundle(ResourceBundleConfig.NAME, session.getUser().locale(), control);
        var prefix = PREFIX + ".type";
        var typeButtons = Arrays.stream(TemplateCommand.Type.values())
            .map(it -> new VkButtonActionCallback(b.getString(prefix + "." + it.code()), new VkButtonPayload(Type.TEMPLATE_SELECT_TYPE, it.code())))
            .map(VkButton::new)
            .map(List::of)
            .toList();
        var grid = new ArrayList<>(typeButtons);
        grid.add(List.of(KeyboardUtils.newCancelButton(b)));
        messageService.sendOrEditMessage(session, r -> r
            .text(b.getString(prefix + ".title"))
            .keyboard(MessageKeyboard.fromGrid(grid))
        );
    }

    public void onSelectType(Session session, VkEventMessage.Event evt) {
        var cmd = (TemplateCommand) session.getCommand();
        var type = TemplateCommand.Type.of(evt.payload().text());
        cmd.setType(type);
        var b = ResourceBundle.getBundle(ResourceBundleConfig.NAME, session.getUser().locale(), control);
        var prefix = "template";
        var grid = new ArrayList<List<VkButton>>();
        var requestBuilder = MessageRequest.builder();
        if (type.numerable()) {
            cmd.setStep(TemplateCommand.Step.NUMBER);
            for (int i = 0; i < 3; i++) {
                var row = new ArrayList<VkButton>();
                for (int j = 0; j < 3; j++) {
                    var label = String.valueOf(i * 3 + j + 1);
                    row.add(new VkButton(new VkButtonActionCallback(label, new VkButtonPayload(Type.TEMPLATE_SELECT_NUMBER, label))));
                }
                grid.add(row);
            }
            requestBuilder.text(b.getString(prefix + ".number"));
        } else {
            cmd.setStep(TemplateCommand.Step.TITLE);
            requestBuilder.text(b.getString(prefix + ".subject"));
        }
        grid.add(List.of(KeyboardUtils.newCancelButton(b)));
        requestBuilder.keyboard(MessageKeyboard.fromGrid(grid));
        messageService.sendOrEditMessage(session, requestBuilder.build());
    }

    public void onSelectNumber(Session session, VkEventMessage.Event evt) {
    }

    public void onWriteSubject(Session session, VkEventMessage.New evt) {
    }

    public void onWriteTopic(Session session, VkEventMessage.New evt) {
    }
}
