package com.github.alebabai.tg2vk.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Chat {
    private final Integer id;
    private final String title;
}
