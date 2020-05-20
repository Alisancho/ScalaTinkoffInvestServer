DROP TABLE IF EXISTS tinkoff_tools_tbl;
DROP TABLE IF EXISTS task_monitoring_tbl;

CREATE TABLE `investdb`.`tinkoff_tools_tbl`
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

CREATE TABLE `investdb`.`task_monitoring_tbl`
(
    `figi`           VARCHAR(45)    NOT NULL,
    `name`           VARCHAR(128)   NOT NULL,
    `ticker`         VARCHAR(45)    NOT NULL,
    `currency`       VARCHAR(8)     NOT NULL,
    `purchase_price` DECIMAL(10, 2) NOT NULL,
    `purchase_lot`   INT UNSIGNED   NOT NULL,
    `sale_price`     DECIMAL(10, 2) NOT NULL,
    `sale_lot`       INT UNSIGNED   NOT NULL,
    `percent`        DOUBLE         NOT NULL,
    `task_operation` VARCHAR(45)    NOT NULL,
    `task_type`      VARCHAR(45)    NOT NULL,
    `task_status`    VARCHAR(45)    NOT NULL,

    PRIMARY KEY (`figi`)
);
