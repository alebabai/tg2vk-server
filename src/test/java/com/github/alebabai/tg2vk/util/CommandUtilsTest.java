package com.github.alebabai.tg2vk.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(JUnit4.class)
public class CommandUtilsTest {

    @Test
    public void parseCommandWithArgsTest() {
        final String input = "/link some-chat";
        CommandUtils.parseCommand(input, (command, args) -> {
            assertThat(command, is("link"));
            assertThat(args, hasItem("some-chat"));
        });
    }

    @Test
    public void parseCommandWithoutArgsTest() {
        final String input = "/link";
        CommandUtils.parseCommand(input, (command, args) -> {
            assertThat(command, is("link"));
            assertThat(args, empty());
        });
    }
}
