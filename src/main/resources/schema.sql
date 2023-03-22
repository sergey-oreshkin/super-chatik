CREATE TABLE role (
    id INT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    username VARCHAR,
    password VARCHAR,
    role_id INT,
    PRIMARY KEY (id),
    CONSTRAINT (fk_role) FOREIGN KEY (role_id) REFERENCES role(id)
);