CREATE TABLE IF NOT EXISTS STATS
(
    STATS_ID     INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    APP          VARCHAR(30) NOT NULL,
    URI          VARCHAR(20) NOT NULL,
    IP           VARCHAR(15) NOT NULL,
    REQUEST_TIME TIMESTAMP   NOT NULL
);