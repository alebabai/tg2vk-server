package com.github.alebabai.tg2vk.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class ImplicitAuthPayload {
    private final Integer vkId;
    private final String vkToken;

    @JsonCreator
    public ImplicitAuthPayload(@JsonProperty(value = "vkId", required = true) Integer vkId,
                               @JsonProperty(value = "vkToken", required = true) String vkToken) {
        this.vkId = vkId;
        this.vkToken = vkToken;
    }
}

