-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Oct 19, 2021 at 08:17 AM
-- Server version: 5.7.31
-- PHP Version: 7.3.21

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `telebrown`
--
CREATE DATABASE IF NOT EXISTS `telebrown` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `telebrown`;

-- --------------------------------------------------------

--
-- Table structure for table `messagehistory`
--

DROP TABLE IF EXISTS `messagehistory`;
CREATE TABLE IF NOT EXISTS `messagehistory` (
  `messageID` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `senderID` bigint(20) UNSIGNED NOT NULL,
  `recipientID` bigint(20) UNSIGNED NOT NULL,
  `message` varchar(1200) NOT NULL,
  `TIMESTAMP` bigint(20) NOT NULL,
  `STATUS` smallint(6) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`messageID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
