CREATE TABLE `investdb`.`market_instruments` (
  `figi` VARCHAR(32) NOT NULL,
  `name` VARCHAR(128) NOT NULL,
  `currency` VARCHAR(8) NOT NULL,
  `type` VARCHAR(16) NOT NULL,
  `lot` INT NOT NULL,
  `ticker` VARCHAR(45) NOT NULL,
  `isin` VARCHAR(45) NOT NULL,
  `min_price_increment` DOUBLE NOT NULL,
  PRIMARY KEY (`figi`));
CREATE TABLE