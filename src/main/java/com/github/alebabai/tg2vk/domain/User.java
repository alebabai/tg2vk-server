package com.github.alebabai.tg2vk.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(of = {"id"})
@ToString(exclude = {"settings", "roles"})
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

    @Override
    public boolean isNew() {
        return id == null;
    }

}
