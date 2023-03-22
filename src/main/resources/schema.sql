CREATE TABLE role (
    id INT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR,
    PRIMARY KEY (id)
);

CREATE TABLE token (
    id INT GENERATED ALWAYS AS IDENTITY,
    token VARCHAR,
    user_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT (fk_token_user) FOREIGN KEY (user_id) REFERENCES user(id)
);


CREATE TABLE IF NOT EXISTS user (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    username VARCHAR,
    password VARCHAR,
    role_id INT,
    PRIMARY KEY (id),
    CONSTRAINT (fk_user_role) FOREIGN KEY (role_id) REFERENCES role(id)
);