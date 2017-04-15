package com.github.alebabai.tg2vk.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "tg2vk_user_settings")
public class UserSettings implements Persistable<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tg2vk_user_settings_id_seq")
    @SequenceGenerator(name = "tg2vk_user_settings_id_seq", sequenceName = "tg2vk_user_settings_id_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "started")
    private boolean started;

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
