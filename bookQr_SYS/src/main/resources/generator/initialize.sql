-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        5.6.19 - MySQL Community Server (GPL)
-- 服务器操作系统:                      Win64
-- HeidiSQL 版本:                  9.1.0.4867
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- 导出 ssm 的数据库结构
DROP DATABASE IF EXISTS `ssm`;
CREATE DATABASE IF NOT EXISTS `ssm` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `ssm`;


-- 导出  表 ssm.book 结构
DROP TABLE IF EXISTS `book`;
CREATE TABLE IF NOT EXISTS `book` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `author` varchar(50) DEFAULT NULL COMMENT '作者',
  `code` varchar(255) DEFAULT NULL COMMENT '产品码',
  `cover` varchar(255) DEFAULT NULL COMMENT '封面图片路径',
  `isbn` varchar(255) DEFAULT NULL COMMENT 'ISBN',
  `name` varchar(255) DEFAULT NULL COMMENT '书名',
  `press` varchar(255) DEFAULT NULL COMMENT '出版社',
  `remark` varchar(512) DEFAULT NULL COMMENT '简介',
  `logo` varchar(512) DEFAULT NULL COMMENT 'logo存储地址',
  `widthAndHeight` int(11) DEFAULT NULL COMMENT '宽度和高度',
  `onwer` int(11) NOT NULL COMMENT '拥有者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='书实体';

-- 正在导出表  ssm.book 的数据：~0 rows (大约)
DELETE FROM `book`;
/*!40000 ALTER TABLE `book` DISABLE KEYS */;
/*!40000 ALTER TABLE `book` ENABLE KEYS */;


-- 导出  表 ssm.book_qr 结构
DROP TABLE IF EXISTS `book_qr`;
CREATE TABLE IF NOT EXISTS `book_qr` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '二维码ID',
  `bookId` int(11) NOT NULL DEFAULT '0',
  `page` int(11) NOT NULL DEFAULT '0' COMMENT '页码',
  `index` int(11) NOT NULL DEFAULT '0' COMMENT '序号排序',
  `url` varchar(512) DEFAULT '0' COMMENT '二维码存储路径',
  `name` varchar(512) DEFAULT '0',
  `num` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '资源数量',
  `onwer` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='图书与二维码';

-- 正在导出表  ssm.book_qr 的数据：~0 rows (大约)
DELETE FROM `book_qr`;
/*!40000 ALTER TABLE `book_qr` DISABLE KEYS */;
/*!40000 ALTER TABLE `book_qr` ENABLE KEYS */;


-- 导出  表 ssm.mail 结构
DROP TABLE IF EXISTS `mail`;
CREATE TABLE IF NOT EXISTS `mail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(128) NOT NULL DEFAULT '0',
  `time` datetime DEFAULT NULL COMMENT '最后操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='邮箱';

-- 正在导出表  ssm.mail 的数据：~1 rows (大约)
DELETE FROM `mail`;
/*!40000 ALTER TABLE `mail` DISABLE KEYS */;
INSERT INTO `mail` (`id`, `email`, `time`) VALUES
	(1, 'aaa', '2015-09-30 18:01:37');
/*!40000 ALTER TABLE `mail` ENABLE KEYS */;


-- 导出  表 ssm.mail_book_count 结构
DROP TABLE IF EXISTS `mail_book_count`;
CREATE TABLE IF NOT EXISTS `mail_book_count` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mailId` int(11) NOT NULL DEFAULT '0',
  `bookId` int(11) NOT NULL DEFAULT '0',
  `sum` int(11) NOT NULL DEFAULT '0' COMMENT '合计',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- 正在导出表  ssm.mail_book_count 的数据：~1 rows (大约)
DELETE FROM `mail_book_count`;
/*!40000 ALTER TABLE `mail_book_count` DISABLE KEYS */;
INSERT INTO `mail_book_count` (`id`, `mailId`, `bookId`, `sum`) VALUES
	(1, 1, 1, 10);
/*!40000 ALTER TABLE `mail_book_count` ENABLE KEYS */;


-- 导出  表 ssm.resources 结构
DROP TABLE IF EXISTS `resources`;
CREATE TABLE IF NOT EXISTS `resources` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `qrId` int(11) NOT NULL,
  `suffix` varchar(50) DEFAULT NULL COMMENT '资源后缀名',
  `text` text COMMENT 'txt文件内容',
  `url` varchar(256) DEFAULT NULL COMMENT '存储资源的地址',
  `size` int(11) DEFAULT NULL COMMENT '文件大小',
  `name` varchar(128) DEFAULT NULL COMMENT '文件名',
  `fileType` int(11) DEFAULT NULL COMMENT '文件类型 1文本， 2图片，3视频，4音频，5其他',
  `sorting` int(11) DEFAULT NULL COMMENT '排序',
  `onwer` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='二维码与资源';

-- 正在导出表  ssm.resources 的数据：~0 rows (大约)
DELETE FROM `resources`;
/*!40000 ALTER TABLE `resources` DISABLE KEYS */;
/*!40000 ALTER TABLE `resources` ENABLE KEYS */;


-- 导出  表 ssm.user 结构
DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱 即账号',
  `password` varchar(128) DEFAULT NULL,
  `loginTime` datetime DEFAULT NULL COMMENT '最后登录时间',
  `loginNum` int(11) DEFAULT NULL COMMENT '登陆次数',
  `phone` int(11) DEFAULT NULL COMMENT '电话',
  `userName` int(11) DEFAULT NULL COMMENT '用户名称',
  `role` int(11) DEFAULT NULL COMMENT '0：管理员；1：编辑员；2：总编',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=utf8 CHECKSUM=1;

-- 正在导出表  ssm.user 的数据：~50 rows (大约)
DELETE FROM `user`;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` (`id`, `email`, `password`, `loginTime`, `loginNum`, `phone`, `userName`, `role`) VALUES
	(1, 'Wang：0', '123456', NULL, NULL, NULL, NULL, NULL),
	(2, 'Wang：1', '123456', NULL, NULL, NULL, NULL, NULL),
	(3, 'Wang：2', '123456', NULL, NULL, NULL, NULL, NULL),
	(4, 'Wang：3', '123456', NULL, NULL, NULL, NULL, NULL),
	(5, 'Wang：4', '123456', NULL, NULL, NULL, NULL, NULL),
	(6, 'Wang：5', '123456', NULL, NULL, NULL, NULL, NULL),
	(7, 'Wang：6', '123456', NULL, NULL, NULL, NULL, NULL),
	(8, 'Wang：7', '123456', NULL, NULL, NULL, NULL, NULL),
	(9, 'Wang：8', '123456', NULL, NULL, NULL, NULL, NULL),
	(10, 'Wang：9', '123456', NULL, NULL, NULL, NULL, NULL),
	(11, 'Wang：10', '123456', NULL, NULL, NULL, NULL, NULL),
	(12, 'Wang：11', '123456', NULL, NULL, NULL, NULL, NULL),
	(13, 'Wang：12', '123456', NULL, NULL, NULL, NULL, NULL),
	(14, 'Wang：13', '123456', NULL, NULL, NULL, NULL, NULL),
	(15, 'Wang：14', '123456', NULL, NULL, NULL, NULL, NULL),
	(16, 'Wang：15', '123456', NULL, NULL, NULL, NULL, NULL),
	(17, 'Wang：16', '123456', NULL, NULL, NULL, NULL, NULL),
	(18, 'Wang：17', '123456', NULL, NULL, NULL, NULL, NULL),
	(19, 'Wang：18', '123456', NULL, NULL, NULL, NULL, NULL),
	(20, 'Wang：19', '123456', NULL, NULL, NULL, NULL, NULL),
	(21, 'Wang：20', '123456', NULL, NULL, NULL, NULL, NULL),
	(22, 'Wang：21', '123456', NULL, NULL, NULL, NULL, NULL),
	(23, 'Wang：22', '123456', NULL, NULL, NULL, NULL, NULL),
	(24, 'Wang：23', '123456', NULL, NULL, NULL, NULL, NULL),
	(25, 'Wang：24', '123456', NULL, NULL, NULL, NULL, NULL),
	(26, 'Wang：25', '123456', NULL, NULL, NULL, NULL, NULL),
	(27, 'Wang：26', '123456', NULL, NULL, NULL, NULL, NULL),
	(28, 'Wang：27', '123456', NULL, NULL, NULL, NULL, NULL),
	(29, 'Wang：28', '123456', NULL, NULL, NULL, NULL, NULL),
	(30, 'Wang：29', '123456', NULL, NULL, NULL, NULL, NULL),
	(31, 'Wang：30', '123456', NULL, NULL, NULL, NULL, NULL),
	(32, 'Wang：31', '123456', NULL, NULL, NULL, NULL, NULL),
	(33, 'Wang：32', '123456', NULL, NULL, NULL, NULL, NULL),
	(34, 'Wang：33', '123456', NULL, NULL, NULL, NULL, NULL),
	(35, 'Wang：34', '123456', NULL, NULL, NULL, NULL, NULL),
	(36, 'Wang：35', '123456', NULL, NULL, NULL, NULL, NULL),
	(37, 'Wang：36', '123456', NULL, NULL, NULL, NULL, NULL),
	(38, 'Wang：37', '123456', NULL, NULL, NULL, NULL, NULL),
	(39, 'Wang：38', '123456', NULL, NULL, NULL, NULL, NULL),
	(40, 'Wang：39', '123456', NULL, NULL, NULL, NULL, NULL),
	(41, 'Wang：40', '123456', NULL, NULL, NULL, NULL, NULL),
	(42, 'Wang：41', '123456', NULL, NULL, NULL, NULL, NULL),
	(43, 'Wang：42', '123456', NULL, NULL, NULL, NULL, NULL),
	(44, 'Wang：43', '123456', NULL, NULL, NULL, NULL, NULL),
	(45, 'Wang：44', '123456', NULL, NULL, NULL, NULL, NULL),
	(46, 'Wang：45', '123456', NULL, NULL, NULL, NULL, NULL),
	(47, 'Wang：46', '123456', NULL, NULL, NULL, NULL, NULL),
	(48, 'Wang：47', '123456', NULL, NULL, NULL, NULL, NULL),
	(49, 'Wang：48', '123456', NULL, NULL, NULL, NULL, NULL),
	(50, 'Wang：49', '123456', NULL, NULL, NULL, NULL, NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;


-- 导出  视图 ssm.v_mail 结构
DROP VIEW IF EXISTS `v_mail`;
-- 创建临时表以解决视图依赖性错误
CREATE TABLE `v_mail` (
	`email` VARCHAR(128) NOT NULL COLLATE 'utf8_general_ci',
	`time` DATETIME NULL COMMENT '最后操作时间',
	`bookId` INT(11) NULL,
	`sum` INT(11) NULL COMMENT '合计'
) ENGINE=MyISAM;


-- 导出  视图 ssm.v_mail 结构
DROP VIEW IF EXISTS `v_mail`;
-- 移除临时表并创建最终视图结构
DROP TABLE IF EXISTS `v_mail`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` VIEW `v_mail` AS SELECT m.email,m.time,b.bookId,b.`sum` from mail m left join mail_book_count b on m.id=b.mailId ;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
