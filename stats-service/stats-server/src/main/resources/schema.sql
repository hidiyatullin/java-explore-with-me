CREATE TABLE IF NOT EXISTS STATS
(
    ID        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    APP       VARCHAR(100)                            NOT NULL,
    URI       VARCHAR(1000)                           NOT NULL,
    IP        VARCHAR(100)                            NOT NULL,
    TIMESTAMP TIMESTAMP                               NOT NULL,
    CONSTRAINT PK_STATS PRIMARY KEY (ID)
);