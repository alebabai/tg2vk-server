package com.github.alebabai.tg2vk.domain;

import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tg2vk_user")
public class User implements Persistable<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "tg_id", unique = true, nullable = false)
    private Integer tgId;

    @Column(name = "vk_id", unique = true, nullable = false)
    private Integer vkId;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_settings_id", nullable = false)
    private UserSettings settings;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_chat_settings",
            joinColumns = {
                    @JoinColumn(name = "user_id", nullable = false)
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "chat_settings_id", nullable = false)
            }
    )
    private Set<ChatSettings> chatsSettings;

    public User() {
        this.settings = new UserSettings();
        this.chatsSettings = new HashSet<>();
    }

    @Override
    public Integer getId() {
        return id;
    }

    public User setId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getTgId() {
        return tgId;
    }

    public User setTgId(Integer tgId) {
        this.tgId = tgId;
        return this;
    }

    public Integer getVkId() {
        return vkId;
    }

    public User setVkId(Integer vkId) {
        this.vkId = vkId;
        return this;
    }

    public UserSettings getSettings() {
        return settings;
    }

    public User setSettings(UserSettings settings) {
        this.settings = settings;
        return this;
    }

    public Set<ChatSettings> getChatsSettings() {
        return chatsSettings;
    }

    public User setChatsSettings(Set<ChatSettings> chatsSettings) {
        this.chatsSettings = chatsSettings;
        return this;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", tgId=" + tgId +
                ", vkId=" + vkId +
                ", settings=" + settings +
                '}';
    }
}
