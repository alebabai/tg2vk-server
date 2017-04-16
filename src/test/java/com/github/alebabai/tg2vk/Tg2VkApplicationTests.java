package com.github.alebabai.tg2vk;

import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.repository.UserRestRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Tg2VkApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    public void contextLoads() {
        assertThat(context.getBeanDefinitionCount(), greaterThan(0));
    }

    @Test
    public void userRepositoriesBeansTest() {
        final String repositoryBeanName = "userRepository";
        final String restRepositoryBeanName = "userRestRepository";

        assertThat(context.isTypeMatch(repositoryBeanName, UserRepository.class), is(true));
        assertThat(context.isTypeMatch(restRepositoryBeanName, UserRestRepository.class), is(true));

        assertThat(context.containsBean(repositoryBeanName), is(true));
        assertThat(context.containsBean(restRepositoryBeanName), is(true));

        assertThat(context.containsBeanDefinition(repositoryBeanName), is(true));
        assertThat(context.containsBeanDefinition(restRepositoryBeanName), is(true));
    }

}
