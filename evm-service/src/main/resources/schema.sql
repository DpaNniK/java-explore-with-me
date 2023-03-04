DROP TABLE IF EXISTS EVENTS, USERS, CATEGORIES, LOCATIONS, REQUESTS, COMPILATIONS_EVENTS, COMPILATIONS;

CREATE TABLE USERS
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE CATEGORIES
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT UQ_CAT_NAME UNIQUE (name)
);

CREATE TABLE LOCATIONS
(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL
);

CREATE TABLE EVENTS
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation         VARCHAR(512) NOT NULL,
    category           BIGINT REFERENCES CATEGORIES (id),
    created_on         TIMESTAMP,
    description        TEXT         NOT NULL,
    event_date         TIMESTAMP,
    initiator          BIGINT REFERENCES USERS (id) ON DELETE CASCADE,
    location           BIGINT REFERENCES LOCATIONS (id) ON DELETE CASCADE,
    paid               BOOLEAN,
    participant_limit  INTEGER,
    published_on       TIMESTAMP,
    request_moderation BOOLEAN,
    state              VARCHAR(255) NOT NULL,
    title              VARCHAR(120) NOT NULL
);

CREATE TABLE REQUESTS
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    requester BIGINT REFERENCES USERS (id) ON DELETE CASCADE,
    event     BIGINT REFERENCES EVENTS (id) ON DELETE CASCADE,
    created   TIMESTAMP    NOT NULL,
    status    VARCHAR(255) NOT NULL
);

CREATE TABLE COMPILATIONS
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN,
    title  VARCHAR(512) NOT NULL
);

CREATE TABLE COMPILATIONS_EVENTS
(
    compilation_id BIGINT REFERENCES COMPILATIONS (id) ON DELETE CASCADE,
    event_id       BIGINT REFERENCES events (id) ON DELETE CASCADE,
    PRIMARY KEY (compilation_id, event_id)
);