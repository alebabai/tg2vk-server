package com.github.alebabai.tg2vk.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Chat {
    private final Integer id;
    private final String title;
    private final ChatType type;
    private boolean hasPhoto;
    private String photoUrl;
    private String thumbUrl;
}
