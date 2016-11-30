package com.github.alebabai.tg2vk.domain;

import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user_settings")
public class UserSettings implements Persistable<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "started", nullable = false)
    private boolean started;

    @Override
    public Integer getId() {
        return id;
    }

    public UserSettings setId(Integer id) {
        this.id = id;
        return this;
    }

    public boolean isStarted() {
        return started;
    }

    public UserSettings started(Boolean started) {
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
        if (!(o instanceof UserSettings)) return false;
        UserSettings that = (UserSettings) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "id=" + id +
                ", started=" + started +
                '}';
    }
}
