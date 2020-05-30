CREATE TABLE `investdb`.`analytics_tbl`
(
    `idanalytics`    VARCHAR(45) NOT NULL,
    `type_analytics` VARCHAR(45) NOT NULL,
    `figi`           VARCHAR(45) NOT NULL,
    `datatask`       DATETIME(4) NOT NULL,
    `trend`          VARCHAR(8)  NOT NULL,
    PRIMARY KEY (`idanalytics`)
);
