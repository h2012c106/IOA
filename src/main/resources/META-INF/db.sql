CREATE DATABASE IF NOT EXISTS `IOA`;
USE `IOA`;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
											`id` int NOT NULL AUTO_INCREMENT,
											`name` varchar(16) NOT NULL,
											`pwd` char(32) NOT NULL,
											`user_type` enum('farmer','admin') not null,
											PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `greenhouse`;
CREATE TABLE `greenhouse` (
														`id` int NOT NULL AUTO_INCREMENT,
														`name` varchar(32) NOT NULL,
														`status` enum('error','on','close') DEFAULT 'on',
														`crop` varchar(16) NOT NULL,
														`longitude` decimal(10,7),
														`latitude` decimal(10,7),
														PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `cluster`;
CREATE TABLE `cluster` (
												 `id` char(17) NOT NULL,
												 `pwd` char(4) NOT NULL,
												 `status` enum('error','on','close') DEFAULT 'on',
												 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `sensor`;
CREATE TABLE `sensor` (
												`id` int NOT NULL AUTO_INCREMENT,
												`type` varchar(16) NOT NULL, -- 传感器类型，如温度传感器等
												`unit` varchar(8) NOT NULL, -- 计量单位，如℃等
												PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 每个传感器的多个阈值信息
DROP TABLE IF EXISTS `threshold`;
CREATE TABLE `threshold` (
													 `id` int NOT NULL AUTO_INCREMENT,
													 `name` varchar(32) NOT NULL,
													 `minimum` decimal(32,16) NOT NULL,
													 `maximum` decimal(32,16) NOT NULL,
													 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 每个传感器的历史数据序列
DROP TABLE IF EXISTS `result`;
CREATE TABLE `result` (
												`id` int NOT NULL AUTO_INCREMENT,
												`value` decimal(32,16) NOT NULL,
												`time` datetime NOT NULL,
												`minimum` decimal(32,16), -- 记录当时的阈值
												`maximum` decimal(32,16),
												PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 大棚控制器，空调风扇等
DROP TABLE IF EXISTS `device`;
CREATE TABLE `device` (
												`id` int NOT NULL AUTO_INCREMENT,
												`name` varchar(32) NOT NULL,
												`status` enum('0','1','2') DEFAULT '0',
												PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 用户-大棚连接表
DROP TABLE IF EXISTS `user_greenhouse`;
CREATE TABLE `user_greenhouse` (
																 `user_id` int NOT NULL,
																 `greenhouse_id` int NOT NULL,
																 PRIMARY KEY (`user_id`,`greenhouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 传感器群-大棚连接表
DROP TABLE IF EXISTS `greenhouse_cluster`;
CREATE TABLE `greenhouse_cluster` (
																		`cluster_id` char(17) NOT NULL,
																		`greenhouse_id` int NOT NULL,
																		`name` varchar(32) NOT NULL, -- 用户为传感器群的命名
																		PRIMARY KEY (`cluster_id`,`greenhouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 传感器群-设备连接表
DROP TABLE IF EXISTS `cluster_device`;
CREATE TABLE `cluster_device` (
																`cluster_id` char(17) NOT NULL,
																`device_id` int NOT NULL,
																`nickname` varchar(32) NOT NULL, -- 设备在传感器群报文中的代号
																PRIMARY KEY (`cluster_id`,`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 传感器群-传感器连接表
DROP TABLE IF EXISTS `cluster_sensor`;
CREATE TABLE `cluster_sensor` (
																`cluster_id` char(17) NOT NULL,
																`sensor_id` int NOT NULL,
																`inner_id` int NOT NULL, -- 传感器在传感器群报文中的序号
																PRIMARY KEY (`cluster_id`,`sensor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 传感器选择的阈值表（一对一）
DROP TABLE IF EXISTS `select`;
CREATE TABLE `select` (
												`sensor_id` int NOT NULL,
												`threshold_id` int NOT NULL,
												PRIMARY KEY (`sensor_id`,`threshold_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 传感器-阈值连接表（一对多）
DROP TABLE IF EXISTS `sensor_threshold`;
CREATE TABLE `sensor_threshold` (
																	`sensor_id` int NOT NULL,
																	`threshold_id` int NOT NULL,
																	PRIMARY KEY (`sensor_id`,`threshold_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 传感器-结果连接表
DROP TABLE IF EXISTS `sensor_result`;
CREATE TABLE `sensor_result` (
															 `sensor_id` int NOT NULL,
															 `result_id` int NOT NULL,
															 PRIMARY KEY (`sensor_id`,`result_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 大棚-结果连接表，方便查询大棚的结果（一个传感器可能先后被装在多个大棚上）
DROP TABLE IF EXISTS `greenhouse_result`;
CREATE TABLE `greenhouse_result` (
																	 `greenhouse_id` int NOT NULL,
																	 `result_id` int NOT NULL,
																	 PRIMARY KEY (`greenhouse_id`,`result_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;