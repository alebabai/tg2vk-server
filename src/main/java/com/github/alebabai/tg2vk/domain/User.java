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
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id", "roles"})
@Entity
@Table(
        name = "tg2vk_user",
        uniqueConstraints = @UniqueConstraint(name = "uk_tg2vk_user_user_settings_id", columnNames = {"user_settings_id"})
)
public class User implements Persistable<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tg2vk_user_id_seq")
    @SequenceGenerator(name = "tg2vk_user_id_seq", sequenceName = "tg2vk_user_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @NotNull(message = "tgId is required!")
    @Column(name = "tg_id", unique = true, nullable = false)
    private Integer tgId;

    @NotNull(message = "vkId is required!")
    @Column(name = "vk_id", unique = true, nullable = false)
    private Integer vkId;

    @NotNull(message = "vkToken is required!")
    @Column(name = "vk_token", unique = true, nullable = false)
    private String vkToken;

    @NotNull(message = "Can't save user without settings!")
    @RestResource(rel = "settings", path = "settings")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_settings_id", nullable = false)
    private UserSettings settings;

    @RestResource(rel = "chatsSettings", path = "chat-settings")
    @JsonIgnoreProperties(value = "user")
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<ChatSettings> chatsSettings = new HashSet<>();

    @JsonIgnore
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(
            name = "tg2vk_user_role",
            joinColumns = {
                    @JoinColumn(name = "user_id", nullable = false)
            }
    )
    @Column(name = "role_id", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Set<Role> roles = new HashSet<>();

    public User(Integer tgId, Integer vkId, String vkToken, UserSettings settings) {
        this.tgId = tgId;
        this.vkId = vkId;
        this.vkToken = vkToken;
        this.settings = settings;
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
