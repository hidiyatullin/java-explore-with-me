CREATE TABLE IF NOT EXISTS STATS
(
    ID        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    APP       VARCHAR(100)                            ,
    URI       VARCHAR(1000)                           ,
    IP        VARCHAR(100)                            ,
    TIMESTAMP TIMESTAMP                               ,
    CONSTRAINT PK_STATS PRIMARY KEY (ID)
);