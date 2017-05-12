package com.github.alebabai.tg2vk.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class CodeAuthPayload {
    private final String code;

    @JsonCreator
    public CodeAuthPayload(@JsonProperty(value = "code", required = true) String code) {
        this.code = code;
    }
}
