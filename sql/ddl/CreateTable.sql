CREATE TABLE `investdb`.`task_monitoring`
(
    `figi`        VARCHAR(45)    NOT NULL,
    `name`        VARCHAR(128)   NOT NULL,
    `currency`    VARCHAR(8)     NOT NULL,
    `status`      VARCHAR(16)    NOT NULL,
    `operation`   VARCHAR(8)     NOT NULL,
    `price_buy`   DECIMAL(15, 2) NOT NULL,
    `lots_buy`    INT            NOT NULL,
    `price_sell`  DECIMAL(15, 2) NOT NULL,
    `lots_sell`   INT            NOT NULL,
    `data_create` TIMESTAMP(4)   NOT NULL,
    `data_update` TIMESTAMP(4)   NOT NULL,
    PRIMARY KEY (`figi`)
);


CREATE TABLE `investdb`.`tinkoff_tools`
(
    `figi`             VARCHAR(45)  NOT NULL,
    `name`             VARCHAR(128) NOT NULL,
    `currency`         VARCHAR(8)   NOT NULL,
    `ticker`           VARCHAR(45)  NOT NULL,
    `isin`             VARCHAR(45),
    `instruments_type` VARCHAR(45),
    `lot`              INT,
    PRIMARY KEY (`figi`)
);
