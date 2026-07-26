package ru.senioravanti.vkbot.utils;

import java.util.Set;

public class MiscellaneousUtils {
    private static final Set<Class<?>> WRAPPER_TYPES = Set.of(
        Boolean.class,
        Character.class,
        Byte.class,
        Short.class,
        Integer.class,
        Long.class,
        Float.class,
        Double.class,
        Void.class
    );

    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || WRAPPER_TYPES.contains(clazz);
    }
}
