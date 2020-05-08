CREATE TABLE `investdb`.`figi_monitoring_tbl`
(
    `figi` VARCHAR(45) NOT NULL,
    `name` VARCHAR(128),
    PRIMARY KEY (`figi`)
);


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
