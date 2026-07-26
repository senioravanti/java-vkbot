package ru.senioravanti.vkbot.config;

import ru.senioravanti.vkbot.utils.YamlResourceBundle;

import java.util.ResourceBundle;

public class ResourceBundleConfig {
    public static final String NAME = "i18n.messages";

    private ResourceBundleConfig() {
    }

    public static ResourceBundle.Control newControl() {
        return new YamlResourceBundle.Control();
    }
}
