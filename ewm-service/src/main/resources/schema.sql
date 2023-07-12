DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS category CASCADE;
DROP TABLE IF EXISTS location CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS compilation CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS category (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (id),
    CONSTRAINT UQ_CATEGORY_NAME UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS location (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL,
    CONSTRAINT pk_location PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation VARCHAR(2000) NOT NULL,
    category_id BIGINT NOT NULL,
    confirmed_requests BIGINT NOT NULL,
    created_on TIMESTAMP NOT NULL,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    initiator_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    paid BOOLEAN NOT NULL,
    participant_limit BIGINT NOT NULL,
    published_on TIMESTAMP,
    request_moderation BOOLEAN NOT NULL,
    state VARCHAR(50),
    title VARCHAR(120) NOT NULL,
    views BIGINT,
    CONSTRAINT pk_events PRIMARY KEY (id),
    CONSTRAINT fk_event_category FOREIGN KEY (category_id) REFERENCES category (id),
    CONSTRAINT fk_event_users FOREIGN KEY (initiator_id) REFERENCES users (id),
    CONSTRAINT fk_event_location FOREIGN KEY (location_id) REFERENCES location (id)
);

CREATE TABLE IF NOT EXISTS compilation (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id BIGINT,
    pinned BOOLEAN,
    title VARCHAR(50) NOT NULL,
    CONSTRAINT pk_compilation PRIMARY KEY (id),
    CONSTRAINT fk_compilation_event FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE TABLE IF NOT EXISTS compilation_events (
    event_id BIGINT,
    compilation_id BIGINT,
    CONSTRAINT fk_comp_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_event_comp FOREIGN KEY (compilation_id) REFERENCES compilation (id)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created TIMESTAMP NOT NULL,
    event_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT pk_requests PRIMARY KEY (id),
    CONSTRAINT fk_request_events FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_request_users FOREIGN KEY (requester_id) REFERENCES users (id)
);