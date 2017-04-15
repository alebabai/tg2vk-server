package com.github.alebabai.tg2vk.util;

import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.User;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Map;

import static com.github.alebabai.tg2vk.util.Tg2vkMapperUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class Tg2vkMapperUtilsTest {

    @Test
    public void createPrivateMessageContextForOnlineUserTest() {
        final User user = mock(User.class);
        final String firstName = "Ivan";
        final String lastName = "Govnov";
        when(user.getFirstName()).thenReturn(firstName);
        when(user.getLastName()).thenReturn(lastName);
        when(user.isOnline()).thenReturn(true);

        final Message message = mock(Message.class);
        final String body = "some-text";
        when(message.getBody()).thenReturn(body);

        assertThat(createPrivateMessageContext(user, message), hasEntry("user", String.join(StringUtils.SPACE, firstName, lastName)));
        assertThat(createPrivateMessageContext(user, message), hasEntry("status", "online"));
        assertThat(createPrivateMessageContext(user, message), hasEntry("body", body));
    }

    @Test
    public void createPrivateMessageContextForOfflineUserTest() {
        final User user = mock(User.class);
        final String firstName = "Ivan";
        final String lastName = "Govnov";
        when(user.getFirstName()).thenReturn(firstName);
        when(user.getLastName()).thenReturn(lastName);
        when(user.isOnline()).thenReturn(false);

        final Message message = mock(Message.class);
        final String body = "some-text";
        when(message.getBody()).thenReturn(body);

        assertThat(createPrivateMessageContext(user, message), hasEntry("user", String.join(StringUtils.SPACE, firstName, lastName)));
        assertThat(createPrivateMessageContext(user, message), hasEntry("status", "offline"));
        assertThat(createPrivateMessageContext(user, message), hasEntry("body", body));

    }

    @Test
    public void createGroupMessageContextTest() {
        final User user = mock(User.class);
        final String firstName = "Ivan";
        final String lastName = "Govnov";
        when(user.getFirstName()).thenReturn(firstName);
        when(user.getLastName()).thenReturn(lastName);
        when(user.isOnline()).thenReturn(true);

        final Message message = mock(Message.class);
        final String body = "some-text";
        when(message.getBody()).thenReturn(body);
        when(message.getTitle()).thenReturn("Linden honey");
        when(message.getChatActive()).thenReturn(Arrays.asList(1, 2, 3));

        final Map<String, Object> context = createGroupMessageContext(user, message);
        assertThat(context, hasEntry("user", String.join(StringUtils.SPACE, firstName, lastName)));
        assertThat(context, hasEntry("status", "online"));
        assertThat(context, hasEntry("body", body));
        assertThat(context, hasEntry("chat", "#Linden_honey"));
        assertThat(context, hasEntry("online_count", 3));
    }

    @Test
    public void createValidHashTagTest() {
        assertThat(createValidHashTag("Uncle Junior"), is("#Uncle_Junior"));
        assertThat(createValidHashTag("Uncle Junior#!@$%"), is("#Uncle_Junior_"));
        assertThat(createValidHashTag("Uncle Junior123"), is("#Uncle_Junior123"));
        assertThat(createValidHashTag("   uncle Junior   $#@$ Java"), is("#_uncle_Junior_Java"));
    }

    @Test
    public void getVkChatIdFromChatTest() {
        final Message message = spy(new Message());
        final int id = 10;
        when(message.getChatId()).thenReturn(id);

        assertThat(getVkChatId(message), is(id));
    }

    @Test
    public void getVkChatIdFromUserTest() {
        final Message message = spy(new Message());
        final int id = 10;
        when(message.getUserId()).thenReturn(id);

        assertThat(getVkChatId(message), is(id));
    }

    @Test
    public void getVkChatIdNegativeTest() {
        final Message message = spy(new Message());

        assertThat(getVkChatId(message), nullValue());
    }
}
