package ru.senioravanti.vkbot.models;

public record ConfigurationProperties(
    Vk vk,
    Loggers loggers
) {
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
