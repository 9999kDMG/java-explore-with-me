CREATE TABLE IF NOT EXISTS USERS
(
    USER_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    EMAIL   VARCHAR(200) UNIQUE NOT NULL,
    NAME    VARCHAR(100)        NOT NULL
);

CREATE TABLE IF NOT EXISTS CATEGORIES
(
    CATEGORY_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME        VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS EVENTS
(
    EVENT_ID           INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    ANNOTATION         VARCHAR(2000)               NOT NULL,
    CATEGORY_ID        INTEGER                     NOT NULL,
    CREATED_ON         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    DESCRIPTION        VARCHAR(7000)               NOT NULL,
    EVENT_DATE         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    INITIATOR_ID       INTEGER                     NOT NULL,
    LAT                FLOAT                       NOT NULL,
    LON                FLOAT                       NOT NULL,
    PAID               BOOLEAN                     NOT NULL,
    PARTICIPANT_LIMIT  INTEGER                     NOT NULL,
    PUBLISHED_ON       TIMESTAMP WITHOUT TIME ZONE,
    REQUEST_MODERATION BOOLEAN                     NOT NULL,
    STATE              VARCHAR(100)                NOT NULL,
    TITLE              VARCHAR(120)                NOT NULL,
    CONFIRMED_REQUESTS INTEGER                     NOT NULL,
    FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORIES (CATEGORY_ID) ON DELETE RESTRICT,
    FOREIGN KEY (INITIATOR_ID) REFERENCES USERS (USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REQUESTS
(
    REQUEST_ID   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    EVENT_ID     INTEGER      NOT NULL,
    REQUESTER_ID INTEGER      NOT NULL,
    STATUS       VARCHAR(100) NOT NULL,
    CREATED      TIMESTAMP    NOT NULL,
    FOREIGN KEY (EVENT_ID) REFERENCES EVENTS (EVENT_ID) ON DELETE CASCADE,
    FOREIGN KEY (REQUESTER_ID) REFERENCES USERS (USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS COMPILATIONS
(
    COMPILATION_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    PINNED         BOOLEAN      NOT NULL,
    TITLE          VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS EVENT_COMPILATIONS
(
    EVENT_ID       INTEGER NOT NULL,
    COMPILATION_ID INTEGER NOT NULL,
    FOREIGN KEY (EVENT_ID) REFERENCES EVENTS (EVENT_ID) ON DELETE CASCADE,
    FOREIGN KEY (COMPILATION_ID) REFERENCES COMPILATIONS (COMPILATION_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS COMMENTS
(
    COMMENT_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    TEXT       VARCHAR(1000) NOT NULL,
    EVENT_ID   INTEGER       NOT NULL,
    USER_ID    INTEGER       NOT NULL,
    CREATED    TIMESTAMP     NOT NULL,
    STATUS     VARCHAR(100)  NOT NULL,
    FOREIGN KEY (EVENT_ID) REFERENCES EVENTS (EVENT_ID) ON DELETE CASCADE,
    FOREIGN KEY (USER_ID) REFERENCES USERS (USER_ID) ON DELETE CASCADE
)