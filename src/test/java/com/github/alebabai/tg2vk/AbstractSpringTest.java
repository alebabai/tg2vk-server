package com.github.alebabai.tg2vk;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        Tg2VkApplication.class
})
@TestPropertySource("classpath:test.properties")
public abstract class AbstractSpringTest {
}
