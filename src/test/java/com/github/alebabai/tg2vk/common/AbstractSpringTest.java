package com.github.alebabai.tg2vk.common;

import com.github.alebabai.tg2vk.Tg2VkApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        Tg2VkApplication.class
})
public abstract class AbstractSpringTest {
}
