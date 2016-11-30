package com.github.alebabai.tg2vk.domain;

import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(
        name = "chat_settings",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_tg_vk_id", columnNames = {"tg_chat_id", "vk_chat_id"}),
        }
)
public class ChatSettings implements Persistable<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "tg_chat_id", nullable = false)
    private Integer tgChatId;

    @Column(name = "vk_chat_id", nullable = false)
    private Integer vkChatId;

    @Column(name = "answer_allowed", nullable = false)
    private boolean answerAllowed;

    @Column(name = "started", nullable = false)
    private boolean started;

    public ChatSettings() {
        super();
    }

    @Override
    public Integer getId() {
        return id;
    }

    public ChatSettings setId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getTgChatId() {
        return tgChatId;
    }

    public ChatSettings setTgChatId(Integer tgChatId) {
        this.tgChatId = tgChatId;
        return this;
    }

    public Integer getVkChatId() {
        return vkChatId;
    }

    public ChatSettings setVkChatId(Integer vkChatId) {
        this.vkChatId = vkChatId;
        return this;
    }

    public Boolean isAnswerAllowed() {
        return answerAllowed;
    }

    public ChatSettings answerAllowed(boolean answerAllowed) {
        this.answerAllowed = answerAllowed;
        return this;
    }

    public boolean isStarted() {
        return started;
    }

    public ChatSettings started(boolean started) {
        this.started = started;
        return this;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatSettings)) return false;
        ChatSettings that = (ChatSettings) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ChatSettings{" +
                "id=" + id +
                ", tgChatId=" + tgChatId +
                ", vkChatId=" + vkChatId +
                ", answerAllowed=" + answerAllowed +
                ", started=" + started +
                '}';
    }
}
