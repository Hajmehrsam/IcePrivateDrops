package com.hajmehrsam.iceprivatedrops.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class ColorUtil {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .hexColors()
            .character('&')
            .hexCharacter('#')
            .build();

    private ColorUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Component toComponent(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }
        return SERIALIZER.deserialize(input);
    }
}