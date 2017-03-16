package com.github.alebabai.tg2vk.domain;

import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "tg2vk_user")
public class User implements Persistable<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tg2vk_user_id_seq")
    @SequenceGenerator(name = "tg2vk_user_id_seq", sequenceName = "tg2vk_user_id_seq")
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "tg_id", unique = true, nullable = false)
    private Integer tgId;

    @Column(name = "vk_id", unique = true, nullable = false)
    private Integer vkId;

    @Column(name = "vk_token", unique = true, nullable = false)
    private String vkToken;

    @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_settings_id", nullable = false)
    private UserSettings settings;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "tg2vk_user_chat_settings",
            joinColumns = {
                    @JoinColumn(name = "user_id", nullable = false)
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "chat_settings_id", nullable = false)
            }
    )
    private List<ChatSettings> chatsSettings;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.LAZY)
    @CollectionTable(
            name = "tg2vk_user_role",
            joinColumns = {
                    @JoinColumn(name = "user_id", nullable = false)
            }
    )
    @Column(name = "role_id")
    @Enumerated(EnumType.ORDINAL)
    private List<Role> roles;

    public User() {
        this.settings = new UserSettings();
        this.chatsSettings = new ArrayList<>();
        this.roles = new ArrayList<>();
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

    public List<ChatSettings> getChatsSettings() {
        return chatsSettings;
    }

    public User setChatsSettings(List<ChatSettings> chatsSettings) {
        this.chatsSettings = chatsSettings;
        return this;
    }

    public String getVkToken() {
        return vkToken;
    }

    public User setVkToken(String vkToken) {
        this.vkToken = vkToken;
        return this;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public User setRoles(List<Role> roles) {
        this.roles = roles;
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
