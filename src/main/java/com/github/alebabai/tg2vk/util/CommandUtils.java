package com.github.alebabai.tg2vk.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public final class CommandUtils {

    private CommandUtils() {
        super();
    }

    public static void parseCommand(String input, BiConsumer<String, List<String>> callback) {
        final List<String> tokens = Arrays.asList(input.split(StringUtils.SPACE));
        if (tokens.size() > 1) {
            callback.accept(tokens.get(0).substring(1), tokens.subList(1, tokens.size()));
        } else {
            callback.accept(tokens.get(0).substring(1), Collections.emptyList());
        }
    }
}
