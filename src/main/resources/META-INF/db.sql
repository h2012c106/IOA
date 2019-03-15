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
	`pwd` varchar(4) DEFAULT '1234', -- 考虑到一个大棚只有单一用户，暂时不用大棚密码
	`status` enum('error','on','close') DEFAULT 'on',
	`crop` varchar(16) NOT NULL,
	`longitude` decimal(10,7),
	`latitude` decimal(10,7),
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 每个传感器群的基本信息；这个表应当一开始就有一串已有的传感器，由管理员负责增改，对用户不可见/改
DROP TABLE IF EXISTS `sensor`;
CREATE TABLE `sensor` (
	`id` int NOT NULL,
	`pwd` varchar(4) NOT NULL, -- 用于验证与用户绑定时id是不是乱写的
	`type` varchar(16) NOT NULL, -- 传感器类型，如温度传感器等
	`unit` varchar(8) NOT NULL, -- 计量单位，如℃等
	`threshold_id` int DEFAULT -1, -- 传感器当前阈值设定
	`cluster_id` char(17) NOT NULL, -- 传感器所属传感器群id
	`inner_id` int NOT NULL, -- 传感器群内部id
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 每个传感器的多个阈值信息
DROP TABLE IF EXISTS `threshold`;
CREATE TABLE `threshold` (
	`id` int NOT NULL AUTO_INCREMENT,
	`sensor_id` int NOT NULL,
	`name` varchar(32) NOT NULL,
	`minimum` decimal(8,3) NOT NULL,
	`maximum` decimal(8,3) NOT NULL,
	`greenhouse_id` int NOT NULL, -- 方便大棚删除时一并删除
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 每个传感器的历史数据序列
DROP TABLE IF EXISTS `result`;
CREATE TABLE `result` (
	`id` int NOT NULL AUTO_INCREMENT,
	`sensor_id` int NOT NULL,
	`value` decimal(8,3) NOT NULL,
	`time` datetime NOT NULL,
	`minimum` decimal(8,3), -- 记录当时的阈值
	`maximum` decimal(8,3),
	`greenhouse_id` int NOT NULL, -- 记录当时所属大棚，给数据时方便筛选
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 大棚控制器，空调风扇等
DROP TABLE IF EXISTS `device`;
CREATE TABLE `device` (
	`id` int NOT NULL AUTO_INCREMENT,
	`cluster_id` char(17) NOT NULL,
	`name` varchar(32) NOT NULL,
	`nickname` varchar(8) NOT NULL,
	`status` enum('0','1','2') DEFAULT '0',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 用户-传感器连接表
DROP TABLE IF EXISTS `user_sensor`;
CREATE TABLE `user_sensor` (
	`user_id` int NOT NULL,
	`cluster_id` char(17) NOT NULL,
	PRIMARY KEY (`user_id`,`cluster_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 用户-大棚连接表
DROP TABLE IF EXISTS `user_greenhouse`;
CREATE TABLE `user_greenhouse` (
	`user_id` int NOT NULL,
	`greenhouse_id` int NOT NULL,
	PRIMARY KEY (`user_id`,`greenhouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 传感器-大棚连接表
DROP TABLE IF EXISTS `sensor_greenhouse`;
CREATE TABLE `sensor_greenhouse` (
	`cluster_id` char(17) NOT NULL,
	`greenhouse_id` int NOT NULL,
	`name` varchar(32) NOT NULL, -- 用户为传感器群的命名
	`status` enum('error','on','close') DEFAULT 'on',
	PRIMARY KEY (`cluster_id`,`greenhouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;