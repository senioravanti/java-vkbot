package ru.senioravanti.vkbot.models;

public record ConfigurationProperties(
    App app
) {
    public record App(
        String groupId,
        String artifactId,
        String version
    ) {
    }
}
