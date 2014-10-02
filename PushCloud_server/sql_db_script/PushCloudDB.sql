
-- phpMyAdmin SQL Dump
-- version 2.11.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Sep 07, 2014 at 08:02 AM
-- Server version: 5.1.57
-- PHP Version: 5.2.17

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `a1920512_gcm`
--

-- --------------------------------------------------------

--
-- Table structure for table `gcm_sponsors`
--

CREATE TABLE `gcm_sponsors` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(150) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `sponsor_image` varchar(150) COLLATE latin1_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci AUTO_INCREMENT=1 ;

--
-- Dumping data for table `gcm_sponsors`
--


-- --------------------------------------------------------

--
-- Table structure for table `gcm_subscriptions`
--

CREATE TABLE `gcm_subscriptions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gcm_regid` varchar(200) COLLATE latin1_general_ci NOT NULL,
  `sponsor_name` varchar(120) COLLATE latin1_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci AUTO_INCREMENT=1 ;

--
-- Dumping data for table `gcm_subscriptions`
--


-- --------------------------------------------------------

--
-- Table structure for table `gcm_users`
--

CREATE TABLE `gcm_users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gcm_regid` text,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Dumping data for table `gcm_users`
--

