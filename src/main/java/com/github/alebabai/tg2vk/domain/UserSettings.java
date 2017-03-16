package com.github.alebabai.tg2vk.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(of = {"id"})
@Entity
@Table(name = "tg2vk_user_settings")
public class UserSettings implements Persistable<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tg2vk_user_settings_id_seq")
    @SequenceGenerator(name = "tg2vk_user_settings_id_seq", sequenceName = "tg2vk_user_settings_id_seq")
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "started")
    private boolean started;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }

}
