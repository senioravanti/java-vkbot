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

import static ru.senioravanti.vkbot.models.vk.button.VkButtonPayload.Code;

@RequiredArgsConstructor
public class TemplateCommandHandler implements CommandHandler.Callback {
    private static final Logger LOGGER = LogManager.getLogger(TemplateCommandHandler.class);

    private final ResourceBundle.Control control;
    private final MessageService messageService;

    @Override
    public EnumMap<Code, CallbackHandler> callbackHandlers() {
        return new EnumMap<>(Map.of(
            Code.TEMPLATE, this::onStart,
            Code.TEMPLATE_SELECT_TYPE, this::onSelectType,
            Code.TEMPLATE_SELECT_NUMBER, this::onSelectNumber
        ));
    }

    private void onStart(VkEventMessage.Event evt, Session session) {
        session.setCommand(new TemplateCommand());
        var b = ResourceBundle.getBundle(ResourceBundleConfig.NAME, session.getUser().locale(), control);
        var prefix = "template.type";
        var typeButtons = Arrays.stream(TemplateCommand.Type.values())
            .map(it -> new VkButtonActionCallback(b.getString(prefix + "." + it.code()), new VkButtonPayload(Code.TEMPLATE_SELECT_TYPE, it.code())))
            .map(VkButton::new)
            .map(List::of)
            .toList();
        var grid = new ArrayList<>(typeButtons);
        grid.add(List.of(KeyboardUtils.newCancelButton(b)));
        messageService.sendOrEditMessage(session, r -> r
            .text(b.getString(prefix + ".title"))
            .keyboard(MessageKeyboard.newInline(grid))
        );
    }

    public void onSelectType(VkEventMessage.Event evt, Session session) {
        var cmd = (TemplateCommand) session.getCommand();
        var type = TemplateCommand.Type.of(evt.payload().id());
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
                    row.add(new VkButton(new VkButtonActionCallback(label, new VkButtonPayload(Code.TEMPLATE_SELECT_NUMBER, label))));
                }
                grid.add(row);
            }
            requestBuilder.text(b.getString(prefix + ".number"));
        } else {
            cmd.setStep(TemplateCommand.Step.TITLE);
            requestBuilder.text(b.getString(prefix + ".subject"));
        }
        grid.add(List.of(KeyboardUtils.newCancelButton(b)));
        requestBuilder.keyboard(MessageKeyboard.newInline(grid));
        messageService.sendOrEditMessage(session, requestBuilder.build());
    }

    public void onSelectNumber(VkEventMessage.Event evt, Session session) {
    }

    public void onWriteSubject(VkEventMessage.New evt, Session session) {
    }

    public void onWriteTopic(VkEventMessage.New evt, Session session) {
    }
}
