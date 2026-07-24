package ru.senioravanti.vkbot.models;

public record ConfigurationProperties(
    App app,
    Vk vk,
    Loggers loggers
) {
    public record App(
        String groupId,
        String artifactId,
        String version
    ) {
    }

    public record Vk(
        Long groupId,
        Api api
    ) {
        public record Api(
            String token,
            String version
        ) {
        }
    }

    public record Loggers(
        Integer maxExternalFrames,
        Integer maxCauseDepth
    ) {
    }
}
