package ru.senioravanti.vkbot.models.commands;

// https://thedailybyte.substack.com/p/refactoring-fsms-sealed-classes-vs
public sealed interface Command permits
    TemplateCommand,
    PasswordCommand {
}
