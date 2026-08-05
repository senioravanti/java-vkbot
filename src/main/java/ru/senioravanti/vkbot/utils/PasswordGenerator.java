package ru.senioravanti.vkbot.utils;

import ru.senioravanti.vkbot.models.PasswordEncoder;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final SecureRandom RND = new SecureRandom();

    public static String generate(PasswordEncoder encoder, int length) {
        var b = new byte[encoder.bytes(length)];
        RND.nextBytes(b);
        return encoder.encode(b)
            .substring(0, length);
    }
}
