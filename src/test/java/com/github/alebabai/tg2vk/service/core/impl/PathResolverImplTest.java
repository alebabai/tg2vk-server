package com.github.alebabai.tg2vk.service.core.impl;

import com.github.alebabai.tg2vk.service.core.PathResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
        "tg2vk.server.scheme=http",
        "tg2vk.server.name=tg2vk.server.com",
        "tg2vk.server.port=8080"
})
public class PathResolverImplTest {

    @Autowired
    private PathResolver pathResolver;

    @Test
    public void getClientUrlTest() {
        final String clientUrl = pathResolver.getClientUrl();

        assertThat(clientUrl, is("https://tg2vk.client.com"));
    }

    @Test
    public void getServerUrlTest() {
        final String serverUrl = pathResolver.getServerUrl();

        assertThat(serverUrl, is("http://tg2vk.server.com:8080"));
    }

    @Test
    public void resolveServerUrlTest() {
        assertThat(pathResolver.resolveServerUrl("/api/users"), is("http://tg2vk.server.com:8080/api/users"));
        assertThat(pathResolver.resolveServerUrl("////api/users"), is("http://tg2vk.server.com:8080/api/users"));
        assertThat(pathResolver.resolveServerUrl("api/users"), is("http://tg2vk.server.com:8080/api/users"));
        assertThat(pathResolver.resolveServerUrl(" api/users"), is("http://tg2vk.server.com:8080/%20api/users"));
    }

    @Test
    public void resolveClientUrlTest() {
        assertThat(pathResolver.resolveClientUrl("/settings"), is("https://tg2vk.client.com/settings"));
        assertThat(pathResolver.resolveClientUrl("////settings"), is("https://tg2vk.client.com/settings"));
        assertThat(pathResolver.resolveClientUrl("settings"), is("https://tg2vk.client.com/settings"));
        assertThat(pathResolver.resolveClientUrl(" settings"), is("https://tg2vk.client.com/%20settings"));
    }
}
