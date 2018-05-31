DROP DATABASE IF EXISTS cbg;
CREATE DATABASE cbg CHARACTER SET utf8;
USE cbg;

CREATE TABLE cbg_equip (
	`game_ordersn` VARCHAR(50) NOT NULL,
	`equip_name` VARCHAR(50) NOT NULL,
	`server_id` INT(11),
	`grouth_rate` VARCHAR(50),
	`born_xue` VARCHAR(50),
	`born_gong` VARCHAR(50),
	`born_fa` VARCHAR(50),
	`born_su` VARCHAR(50),
	`born_chan` VARCHAR(50),
	`detail_url` VARCHAR(200),
	PRIMARY KEY (game_ordersn)
) engine=InnoDb  default charset=utf8;
