CREATE TABLE tg2vk_user_settings (
    id SERIAL,
    started BOOLEAN DEFAULT FALSE,
    CONSTRAINT pk_tg2vk_user_settings_id PRIMARY KEY (id)
);

CREATE TABLE tg2vk_user (
    id SERIAL,
    tg_id INTEGER NOT NULL,
    vk_id INTEGER NOT NULL,
    vk_token VARCHAR(100) NOT NULL,
    user_settings_id INTEGER NOT NULL,
    CONSTRAINT pk_tg2vk_user_id PRIMARY KEY (id),
    CONSTRAINT uk_tg2vk_user_tg_id UNIQUE (tg_id),
    CONSTRAINT uk_tg2vk_user_vk_id UNIQUE (vk_id),
    CONSTRAINT uk_tg2vk_user_vk_token UNIQUE (vk_token),
    CONSTRAINT uk_tg2vk_user_user_settings_id UNIQUE (user_settings_id),
    CONSTRAINT fk_tg2vk_user_user_settings_id FOREIGN KEY (user_settings_id) REFERENCES tg2vk_user_settings (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE tg2vk_chat_settings (
    id SERIAL,
    tg_chat_id INTEGER NOT NULL,
    vk_chat_id INTEGER NOT NULL,
    answer_allowed BOOLEAN DEFAULT FALSE,
    started BOOLEAN DEFAULT FALSE,
    user_id INTEGER NOT NULL,
    CONSTRAINT pk_tg2vk_chat_settings_id PRIMARY KEY (id),
    CONSTRAINT uk_tg2vk_chat_settings_tg_vk_chat_id UNIQUE (tg_chat_id, vk_chat_id),
    CONSTRAINT fk_tg2vk_chat_settings_user_id FOREIGN KEY (user_id) REFERENCES tg2vk_user (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE tg2vk_role (
    id SERIAL,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT pk_tg2vk_role_id PRIMARY KEY (id)
);

CREATE TABLE tg2vk_user_role (
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    CONSTRAINT pk_tg2vk_user_role PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_tg2vk_user_role_user_id FOREIGN KEY (user_id) REFERENCES tg2vk_user (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_tg2vk_user_role_role_id FOREIGN KEY (role_id) REFERENCES tg2vk_role (id) ON DELETE CASCADE ON UPDATE CASCADE
);

