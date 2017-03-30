package com.github.alebabai.tg2vk.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Persistable;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(exclude = {"user"})
@ToString(exclude = {"user"})
@Entity
@Table(
        name = "tg2vk_chat_settings",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_tg2vk_chat_settings_tg_vk_chat_id", columnNames = {"tg_chat_id", "vk_chat_id"}),
        }
)
public class ChatSettings implements Persistable<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tg2vk_chat_settings_id_seq")
    @SequenceGenerator(name = "tg2vk_chat_settings_id_seq", sequenceName = "tg2vk_chat_settings_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @NotNull(message = "tgChatId is required!")
    @Column(name = "tg_chat_id", nullable = false)
    private Integer tgChatId;

    @NotNull(message = "vkChatId is required!")
    @Column(name = "vk_chat_id", nullable = false)
    private Integer vkChatId;

    @Column(name = "answer_allowed", nullable = false)
    private boolean answerAllowed;

    @Column(name = "started", nullable = false)
    private boolean started;

    @NotNull(message = "ChatSettings can't be saved without specified reference to the User!")
    @RestResource(rel = "user", path = "user")
    @JsonIgnoreProperties(value = "chatsSettings")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public ChatSettings(Integer tgChatId, Integer vkChatId, User user) {
        this.tgChatId = tgChatId;
        this.vkChatId = vkChatId;
        this.user = user;
    }

    @JsonIgnore
    @Override
    public Integer getId() {
        return id;
    }

    @JsonIgnore
    @Override
    public boolean isNew() {
        return id == null;
    }

}
