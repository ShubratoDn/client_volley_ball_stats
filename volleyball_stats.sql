-- phpMyAdmin SQL Dump
-- version 5.0.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 16, 2025 at 06:54 PM
-- Server version: 10.4.17-MariaDB
-- PHP Version: 8.0.2

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `volleyball_stats`
--

-- --------------------------------------------------------

--
-- Table structure for table `matches`
--

CREATE TABLE `matches` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `location` varchar(200) DEFAULT NULL,
  `match_date` datetime(6) NOT NULL,
  `notes` varchar(1000) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `tournament_id` bigint(20) DEFAULT NULL,
  `home_team_id` bigint(20) DEFAULT NULL,
  `away_team_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `matches`
--

INSERT INTO `matches` (`id`, `created_at`, `location`, `match_date`, `notes`, `status`, `updated_at`, `created_by`, `tournament_id`, `home_team_id`, `away_team_id`) VALUES
(2, '2025-05-15 06:25:50.000000', 'sirajganj', '2025-05-15 06:00:00.000000', '', 'COMPLETED', '2025-05-15 08:37:24.000000', 1, 1, NULL, NULL),
(3, '2025-05-15 15:10:00.000000', 'Dhaka', '2025-05-16 06:00:00.000000', '', 'SCHEDULED', '2025-05-15 15:10:13.000000', 1, 1, NULL, NULL),
(4, '2025-05-16 10:57:12.000000', 'Dubai', '2025-05-17 06:00:00.000000', '', 'SCHEDULED', '2025-05-16 10:57:12.000000', 1, 2, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `match_teams`
--

CREATE TABLE `match_teams` (
  `id` bigint(20) NOT NULL,
  `is_home_team` bit(1) DEFAULT NULL,
  `is_winner` bit(1) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `set_scores` varchar(255) DEFAULT NULL,
  `match_id` bigint(20) NOT NULL,
  `team_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `match_teams`
--

INSERT INTO `match_teams` (`id`, `is_home_team`, `is_winner`, `score`, `set_scores`, `match_id`, `team_id`) VALUES
(8, NULL, NULL, NULL, NULL, 2, 5),
(9, NULL, NULL, NULL, NULL, 2, 1),
(11, NULL, NULL, NULL, NULL, 3, 1),
(12, NULL, NULL, NULL, NULL, 3, 4),
(14, NULL, NULL, NULL, NULL, 4, 1),
(15, NULL, NULL, NULL, NULL, 4, 3);

-- --------------------------------------------------------

--
-- Table structure for table `players`
--

CREATE TABLE `players` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `height` int(11) DEFAULT NULL,
  `jersey_number` int(11) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `nationality` varchar(50) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `position` varchar(50) DEFAULT NULL,
  `profile_image` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `weight` int(11) DEFAULT NULL,
  `created_by_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `players`
--

INSERT INTO `players` (`id`, `created_at`, `date_of_birth`, `height`, `jersey_number`, `name`, `nationality`, `notes`, `position`, `profile_image`, `updated_at`, `weight`, `created_by_id`) VALUES
(1, '2025-05-15 04:34:24.000000', '1996-12-31', 170, 1, 'Shubrato', '', '', 'Blocker', '', '2025-05-15 04:34:24.000000', 70, 1),
(2, '2025-05-15 04:34:55.000000', '2005-11-30', 177, 2, 'Madhu', '', '', 'Setter', '', '2025-05-15 04:34:55.000000', 80, 1),
(23, '2025-05-15 10:40:19.000000', '1995-03-10', 180, 9, 'John Smith', 'USA', 'Team captain', 'Setter', 'john.jpg', '2025-05-15 10:40:19.000000', 75, 1),
(24, '2025-05-15 10:40:19.000000', '1997-07-22', 175, 4, 'Carlos Diaz', 'Brazil', 'Rookie player', 'Defender', 'carlos.jpg', '2025-05-15 10:40:19.000000', 68, 1),
(25, '2025-05-15 10:40:19.000000', '1994-01-15', 190, 12, 'Ali Reza', 'Iran', NULL, 'Bloker', 'ali.jpg', '2025-05-15 10:40:19.000000', 82, 1),
(26, '2025-05-15 10:40:19.000000', '1998-10-05', 185, 6, 'Leon Tanaka', 'Japan', 'Fast setter', 'Setter', 'leon.jpg', '2025-05-15 10:40:19.000000', 70, 1),
(27, '2025-05-15 10:40:19.000000', '1993-05-30', 195, 11, 'Victor Hugo', 'Argentina', 'Veteran', 'Universal', 'victor.jpg', '2025-05-15 10:40:19.000000', 88, 1),
(28, '2025-05-15 10:40:19.000000', '2000-12-01', 178, 3, 'Paul Kim', 'South Korea', NULL, 'Defender', 'paul.jpg', '2025-05-15 10:40:19.000000', 66, 1),
(29, '2025-05-15 10:40:19.000000', '1996-06-14', 182, 8, 'George Ade', 'Nigeria', NULL, 'Bloker', 'george.jpg', '2025-05-15 10:40:19.000000', 74, 1),
(30, '2025-05-15 10:40:19.000000', '1999-09-19', 188, 10, 'Martin Müller', 'Germany', NULL, 'Universal', 'martin.jpg', '2025-05-15 10:40:19.000000', 80, 1),
(31, '2025-05-15 10:40:19.000000', '1992-11-23', 192, 7, 'Robert Kowalski', 'Poland', 'Strong attacker', 'Setter', 'robert.jpg', '2025-05-15 10:40:19.000000', 85, 1),
(32, '2025-05-15 10:40:19.000000', '1995-04-17', 170, 5, 'Hugo Leclerc', 'France', 'Defensive specialist', 'Defender', 'hugo.jpg', '2025-05-15 10:40:19.000000', 67, 1),
(33, '2025-05-15 10:41:20.000000', '1994-06-12', 186, 13, 'Adam Johnson', 'USA', 'Reliable blocker', 'Bloker', 'adam.jpg', '2025-05-15 10:41:20.000000', 79, 1),
(34, '2025-05-15 10:41:20.000000', '1993-03-25', 178, 2, 'Lucas Mendes', 'Brazil', NULL, 'Setter', 'lucas.jpg', '2025-05-15 10:41:20.000000', 70, 1),
(35, '2025-05-15 10:41:20.000000', '1997-08-19', 182, 5, 'Omar Faruk', 'Bangladesh', 'Quick reflexes', 'Defender', 'omar.jpg', '2025-05-15 10:41:20.000000', 68, 1),
(36, '2025-05-15 10:41:20.000000', '1995-09-02', 190, 14, 'Anton Petrov', 'Russia', NULL, 'Universal', 'anton.jpg', '2025-05-15 10:41:20.000000', 85, 1),
(37, '2025-05-15 10:41:20.000000', '1996-11-07', 176, 6, 'Marco Rossi', 'Italy', 'Left-handed', 'Setter', 'marco.jpg', '2025-05-15 10:41:20.000000', 72, 1),
(38, '2025-05-15 10:41:20.000000', '1992-02-14', 188, 10, 'Daniel Kim', 'South Korea', 'Strong arm', 'Bloker', 'daniel.jpg', '2025-05-15 10:41:20.000000', 83, 1),
(39, '2025-05-15 10:41:20.000000', '1998-05-22', 175, 7, 'Jean Dupont', 'France', NULL, 'Defender', 'jean.jpg', '2025-05-15 10:41:20.000000', 67, 1),
(40, '2025-05-15 10:41:20.000000', '1990-12-30', 195, 9, 'Carlos Ortega', 'Mexico', 'Aggressive attacker', 'Universal', 'carlos_o.jpg', '2025-05-15 10:41:20.000000', 90, 1),
(41, '2025-05-15 10:41:20.000000', '1991-01-11', 183, 3, 'Samir Shah', 'India', NULL, 'Setter', 'samir.jpg', '2025-05-15 10:41:20.000000', 76, 1),
(42, '2025-05-15 10:41:20.000000', '1999-04-18', 185, 12, 'Mohammed Ali', 'Egypt', NULL, 'Defender', 'ali_m.jpg', '2025-05-15 10:41:20.000000', 74, 1),
(43, '2025-05-15 10:41:20.000000', '1994-07-08', 180, 4, 'Ben Carter', 'Australia', NULL, 'Bloker', 'ben.jpg', '2025-05-15 10:41:20.000000', 77, 1),
(44, '2025-05-15 10:41:20.000000', '1995-10-27', 187, 11, 'Jamal Ahmed', 'UAE', 'Team motivator', 'Universal', 'jamal.jpg', '2025-05-15 10:41:20.000000', 81, 1),
(45, '2025-05-15 10:41:20.000000', '1996-03-15', 174, 1, 'Oscar Weber', 'Germany', NULL, 'Setter', 'oscar.jpg', '2025-05-15 10:41:20.000000', 69, 1),
(46, '2025-05-15 10:41:20.000000', '1998-06-01', 179, 8, 'Tariq Hassan', 'Pakistan', NULL, 'Defender', 'tariq.jpg', '2025-05-15 10:41:20.000000', 70, 1),
(47, '2025-05-15 10:41:20.000000', '1993-11-04', 191, 6, 'Ivan Novak', 'Croatia', NULL, 'Universal', 'ivan.jpg', '2025-05-15 10:41:20.000000', 84, 1),
(48, '2025-05-15 10:41:20.000000', '1997-01-29', 184, 10, 'George Brown', 'Canada', 'New recruit', 'Bloker', 'george.jpg', '2025-05-15 10:41:20.000000', 78, 1),
(49, '2025-05-15 10:41:20.000000', '1992-08-13', 186, 13, 'Leo Wang', 'China', NULL, 'Setter', 'leo.jpg', '2025-05-15 10:41:20.000000', 75, 1),
(50, '2025-05-15 10:41:20.000000', '1991-05-05', 177, 5, 'Andres Lopez', 'Colombia', NULL, 'Defender', 'andres.jpg', '2025-05-15 10:41:20.000000', 66, 1),
(51, '2025-05-15 10:41:20.000000', '1996-09-17', 193, 9, 'Mateusz Zielinski', 'Poland', NULL, 'Bloker', 'mateusz.jpg', '2025-05-15 10:41:20.000000', 87, 1),
(52, '2025-05-15 10:41:20.000000', '1995-12-21', 181, 7, 'Youssef El Fassi', 'Morocco', 'Versatile player', 'Universal', 'youssef.jpg', '2025-05-15 10:41:20.000000', 73, 1),
(53, '2025-05-15 10:51:48.000000', '1994-03-12', 182, 15, 'Alex Murphy', 'USA', NULL, 'Setter', 'alex.jpg', '2025-05-15 10:51:48.000000', 74, 1),
(54, '2025-05-15 10:51:48.000000', '1990-07-28', 193, 2, 'Rajiv Menon', 'India', 'Captain experience', 'Bloker', 'rajiv.jpg', '2025-05-15 10:51:48.000000', 88, 1),
(55, '2025-05-15 10:51:48.000000', '1996-09-13', 179, 5, 'Kenji Watanabe', 'Japan', NULL, 'Defender', 'kenji.jpg', '2025-05-15 10:51:48.000000', 70, 1),
(56, '2025-05-15 10:51:48.000000', '1993-04-17', 187, 6, 'Juan Herrera', 'Colombia', 'Aggressive hitter', 'Universal', 'juan.jpg', '2025-05-15 10:51:48.000000', 79, 1),
(57, '2025-05-15 10:51:48.000000', '1997-02-03', 178, 12, 'Yunus Emre', 'Turkey', NULL, 'Setter', 'yunus.jpg', '2025-05-15 10:51:48.000000', 69, 1),
(58, '2025-05-15 10:51:48.000000', '1995-11-19', 188, 8, 'Andre Silva', 'Brazil', NULL, 'Bloker', 'andre.jpg', '2025-05-15 10:51:48.000000', 83, 1),
(59, '2025-05-15 10:51:48.000000', '1999-06-27', 175, 3, 'Sofiane Zidane', 'Algeria', 'Good passer', 'Defender', 'sofiane.jpg', '2025-05-15 10:51:48.000000', 68, 1),
(60, '2025-05-15 10:51:48.000000', '1992-01-20', 191, 14, 'Diego Morales', 'Mexico', 'Former national team', 'Universal', 'diego.jpg', '2025-05-15 10:51:48.000000', 86, 1),
(61, '2025-05-15 10:51:48.000000', '1998-10-05', 180, 4, 'Dmitry Ivanov', 'Russia', NULL, 'Setter', 'dmitry.jpg', '2025-05-15 10:51:48.000000', 72, 1),
(62, '2025-05-15 10:51:48.000000', '1996-05-15', 184, 7, 'Liam O’Brien', 'Ireland', NULL, 'Defender', 'liam.jpg', '2025-05-15 10:51:48.000000', 71, 1),
(63, '2025-05-15 10:51:48.000000', '1993-08-08', 189, 10, 'Sanjay Desai', 'India', 'Solid blocker', 'Bloker', 'sanjay.jpg', '2025-05-15 10:51:48.000000', 84, 1),
(64, '2025-05-15 10:51:48.000000', '1994-12-18', 177, 1, 'Moussa Faye', 'Senegal', NULL, 'Defender', 'moussa.jpg', '2025-05-15 10:51:48.000000', 67, 1),
(65, '2025-05-15 10:51:48.000000', '1991-06-09', 186, 9, 'Theo Martin', 'UK', 'Reliable veteran', 'Universal', 'theo.jpg', '2025-05-15 10:51:48.000000', 79, 1),
(66, '2025-05-15 10:51:48.000000', '1995-03-11', 183, 11, 'Anil Kumar', 'India', NULL, 'Setter', 'anil.jpg', '2025-05-15 10:51:48.000000', 76, 1),
(67, '2025-05-15 10:51:48.000000', '1997-09-23', 190, 13, 'Daniel Sousa', 'Portugal', NULL, 'Bloker', 'daniel.jpg', '2025-05-15 10:51:48.000000', 82, 1),
(68, '2025-05-15 10:51:48.000000', '1998-01-14', 174, 6, 'Chin Ho Lee', 'South Korea', NULL, 'Defender', 'chin.jpg', '2025-05-15 10:51:48.000000', 66, 1),
(69, '2025-05-15 10:51:48.000000', '1992-10-01', 194, 15, 'Elias Berg', 'Sweden', NULL, 'Universal', 'elias.jpg', '2025-05-15 10:51:48.000000', 90, 1),
(70, '2025-05-15 10:51:48.000000', '1996-07-05', 181, 2, 'Fernando Ruiz', 'Spain', NULL, 'Setter', 'fernando.jpg', '2025-05-15 10:51:48.000000', 73, 1),
(71, '2025-05-15 10:51:48.000000', '1999-11-30', 176, 5, 'Zaid Hassan', 'Iraq', NULL, 'Defender', 'zaid.jpg', '2025-05-15 10:51:48.000000', 69, 1),
(72, '2025-05-15 10:51:48.000000', '1995-06-22', 188, 8, 'Nathan King', 'Australia', NULL, 'Bloker', 'nathan.jpg', '2025-05-15 10:51:48.000000', 80, 1),
(73, '2025-05-15 10:51:48.000000', '1995-06-22', 188, 8, 'TEST', 'Australia', NULL, 'Bloker', 'nathan.jpg', '2025-05-15 10:51:48.000000', 80, 1);

-- --------------------------------------------------------

--
-- Table structure for table `roles`
--

CREATE TABLE `roles` (
  `id` int(11) NOT NULL,
  `name` enum('ROLE_ADMIN','ROLE_MODERATOR','ROLE_USER') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `roles`
--

INSERT INTO `roles` (`id`, `name`) VALUES
(1, 'ROLE_USER'),
(2, 'ROLE_MODERATOR'),
(3, 'ROLE_ADMIN');

-- --------------------------------------------------------

--
-- Table structure for table `statistics`
--

CREATE TABLE `statistics` (
  `id` bigint(20) NOT NULL,
  `action_type` enum('ATTACK','RECEPTION','SERVE') NOT NULL,
  `color` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `endx` double DEFAULT NULL,
  `endy` double DEFAULT NULL,
  `notes` varchar(1000) DEFAULT NULL,
  `startx` double NOT NULL,
  `starty` double NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `match_id` bigint(20) DEFAULT NULL,
  `player_id` bigint(20) DEFAULT NULL,
  `team_id` bigint(20) DEFAULT NULL,
  `quality` int(11) DEFAULT NULL,
  `action_state` enum('ACE','ATTACK_ERROR','BREAK_POINT','FREEBALL','KILL','RECEPTION_ERROR','REGULAR_RECEPTION','SERVE_ERROR','SHOT','OUT_OF_SYSTEM_PASS') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `statistics`
--

INSERT INTO `statistics` (`id`, `action_type`, `color`, `created_at`, `endx`, `endy`, `notes`, `startx`, `starty`, `updated_at`, `created_by`, `match_id`, `player_id`, `team_id`, `quality`, `action_state`) VALUES
(8, 'ATTACK', '#ff0000', '2025-05-15 06:27:21.000000', 12.19845479704797, 41.328125, 'notes is here', 32.19845479704797, 41.328125, '2025-05-15 06:27:21.000000', 1, 2, 24, 1, 1, 'KILL'),
(9, 'ATTACK', '#ff0000', '2025-05-15 06:27:33.000000', 15.519488007380076, 88.128125, '', 35.519488007380076, 17.128125, '2025-05-15 06:27:33.000000', 1, 2, 1, 1, 1, 'SHOT'),
(10, 'ATTACK', '#ff0000', '2025-05-15 06:28:10.000000', 33.58221863468634, 37.728125, '', 33.58221863468634, 37.728125, '2025-05-15 06:28:10.000000', 1, 2, 61, 5, 1, 'KILL'),
(11, 'RECEPTION', '#4361ee', '2025-05-15 06:28:27.000000', NULL, NULL, '', 61.3497463099631, 27.928124999999998, '2025-05-15 06:28:27.000000', 1, 2, 29, 1, 1, NULL),
(12, 'RECEPTION', '#4361ee', '2025-05-15 06:28:41.000000', NULL, NULL, '', 29.24642527675277, 48.928125, '2025-05-15 06:28:41.000000', 1, 2, 1, 1, 1, NULL),
(13, 'ATTACK', '#ff0000', '2025-05-15 06:30:45.000000', 1.75565036900369, 5.328125, '', 1.75565036900369, 5.328125, '2025-05-15 06:30:45.000000', 1, 2, 1, 1, 1, 'KILL'),
(14, 'ATTACK', '#e10dfd', '2025-05-15 09:27:24.000000', 31.921381440109876, 35.223609924316406, '', 31.921381440109876, 35.223609924316406, '2025-05-15 09:27:24.000000', 1, 2, 23, 1, 1, 'SHOT'),
(15, 'ATTACK', '#8a68ca', '2025-05-15 09:28:56.000000', 0.5560677869733409, 97.84583358764648, '', 0.5560677869733409, 97.84583358764648, '2025-05-15 09:28:56.000000', 1, 2, 1, 1, 1, 'ATTACK_ERROR'),
(16, 'RECEPTION', '#4361ee', '2025-05-15 09:29:26.000000', NULL, NULL, '', 99.72580948439031, 97.2236099243164, '2025-05-15 09:29:26.000000', 1, 2, 27, 1, 1, NULL),
(17, 'SERVE', '#38b2ac', '2025-05-15 09:35:48.000000', NULL, NULL, '', 0.648318709482566, 97.64583358764648, '2025-05-15 09:35:48.000000', 1, 2, 57, 5, 4, NULL),
(18, 'ATTACK', '#ff0000', '2025-05-15 13:16:43.000000', 30.350553505535053, 45.0111083984375, '', 9.132841328413283, 37.4111083984375, '2025-05-15 13:16:43.000000', 1, 2, 30, 1, 1, NULL),
(19, 'ATTACK', '#ff0000', '2025-05-15 13:17:19.000000', 82.65682656826569, 32.8111083984375, '', 24.354243542435423, 35.4111083984375, '2025-05-15 13:17:19.000000', 1, 2, 55, 5, 1, NULL),
(20, 'RECEPTION', '#4361ee', '2025-05-15 13:17:45.000000', NULL, NULL, '', 31.642066420664207, 18.74444580078125, '2025-05-15 13:17:45.000000', 1, 2, 1, 1, 1, NULL),
(21, 'ATTACK', '#151313', '2025-05-15 13:29:39.000000', 98.43173431734317, 3.565625, '', 23.616236162361623, 16.565625, '2025-05-15 13:29:39.000000', 1, 2, 30, 1, 1, NULL),
(22, 'SERVE', '#38b2ac', '2025-05-15 13:30:03.000000', NULL, NULL, '', 35.70110701107011, 28.565625, '2025-05-15 13:30:03.000000', 1, 2, 55, 5, 1, NULL),
(23, 'ATTACK', '#ff0000', '2025-05-15 15:00:40.000000', 0, 0, '', 0, 0, '2025-05-15 15:00:40.000000', 1, 2, 55, 5, 1, NULL),
(24, 'ATTACK', '#ff0000', '2025-05-15 15:01:53.000000', 97.92051045510455, 95.0889423076923, '', 33.836869618696184, 44.62740384615385, '2025-05-15 15:01:53.000000', 1, 2, 30, 1, 1, NULL),
(25, 'ATTACK', '#ff0000', '2025-05-15 15:02:46.000000', 29.77782902829028, 41.14022592397836, '', 63.35716482164821, 93.44791823167067, '2025-05-15 15:02:46.000000', 1, 2, 29, 1, 1, NULL),
(26, 'RECEPTION', '#4361ee', '2025-05-15 15:03:13.000000', NULL, NULL, '', 81.43834563345634, 36.93509615384615, '2025-05-15 15:03:13.000000', 1, 2, 56, 5, 1, NULL),
(27, 'ATTACK', '#ff0000', '2025-05-15 15:11:47.000000', 96.56749692496925, 90.38301438551683, '', 18.092712177121772, 59.61378361628606, '2025-05-15 15:11:47.000000', 1, 3, 1, 1, 1, NULL),
(28, 'RECEPTION', '#4361ee', '2025-05-15 16:39:23.000000', NULL, NULL, '', 29.37932882585752, 24.697922926682693, '2025-05-15 16:39:23.000000', 1, 3, 1, 1, 1, 'REGULAR_RECEPTION'),
(29, 'SERVE', '#38b2ac', '2025-05-15 16:48:36.000000', NULL, NULL, '', 13.284342018469658, 29.415865384615387, '2025-05-15 16:48:36.000000', 1, 3, 47, 4, 1, 'FREEBALL'),
(30, 'ATTACK', '#ff0000', '2025-05-15 16:50:23.000000', 93.75927605540898, 84.28766338641826, '', 4.972996372031662, 7.056894155649037, '2025-05-15 16:50:23.000000', 1, 3, 1, 1, 1, 'SHOT'),
(31, 'ATTACK', '#ff0000', '2025-05-15 18:17:38.000000', 97.8338112543723, 6.888887845552885, '', 11.117944095700723, 40.11965707632211, '2025-05-15 18:17:38.000000', 1, 3, 23, 1, 1, 'ATTACK_ERROR'),
(32, 'RECEPTION', '#4361ee', '2025-05-15 18:18:02.000000', NULL, NULL, '', 85.65668948315461, 64.01708984375, '2025-05-15 18:18:02.000000', 1, 3, 48, 4, 1, 'REGULAR_RECEPTION'),
(33, 'ATTACK', '#ff0000', '2025-05-16 05:16:28.000000', 97.34180633432311, 77.65811861478366, '', 22.188054796807734, 65.65811861478366, '2025-05-16 05:16:28.000000', 1, 2, 56, 5, 1, 'SHOT'),
(34, 'ATTACK', '#ff0000', '2025-05-16 10:23:12.000000', 63.23287982635686, 89.58973106971155, NULL, 30.022547723035824, 90.82050030048077, '2025-05-16 10:23:12.000000', 1, 2, 1, 1, 1, 'KILL'),
(35, 'ATTACK', '#ff0000', '2025-05-16 10:23:12.000000', 73.07297822734087, 16.666654146634617, NULL, 35.92660676362623, 20.358961838942307, '2025-05-16 10:23:12.000000', 1, 2, 1, 1, 1, 'ATTACK_ERROR'),
(36, 'SERVE', '#38b2ac', '2025-05-16 10:23:12.000000', NULL, NULL, NULL, 96.81221561971479, 5.179480919471153, '2025-05-16 10:23:12.000000', 1, 2, 1, 1, 1, 'ACE'),
(37, 'ATTACK', '#ff0000', '2025-05-16 10:23:33.000000', 100.37925129007151, 32.76923076923077, NULL, 28.54653296288822, 73.6923076923077, '2025-05-16 10:23:33.000000', 1, 2, 55, 5, 1, 'KILL'),
(38, 'ATTACK', '#ff0000', '2025-05-16 10:27:24.000000', 98.65723406989929, 96.97434645432692, NULL, 2.3472709702682963, 5.282038762019231, '2025-05-16 10:27:24.000000', 1, 3, 1, 1, 1, 'SHOT'),
(39, 'ATTACK', '#ff0000', '2025-05-16 10:27:24.000000', 99.1492389899485, 3.230769230769231, NULL, 1.3632611301698956, 99.23076923076923, '2025-05-16 10:27:24.000000', 1, 3, 1, 1, 1, 'SHOT'),
(40, 'RECEPTION', '#4361ee', '2025-05-16 10:27:24.000000', NULL, NULL, NULL, 2.2242697402559966, 44.15384615384615, '2025-05-16 10:27:24.000000', 1, 3, 1, 1, 1, 'REGULAR_RECEPTION'),
(41, 'RECEPTION', '#4361ee', '2025-05-16 10:27:24.000000', NULL, NULL, NULL, 99.64124390999768, 44.15384615384615, '2025-05-16 10:27:24.000000', 1, 3, 1, 1, 1, 'REGULAR_RECEPTION'),
(42, 'SERVE', '#38b2ac', '2025-05-16 10:27:24.000000', NULL, NULL, NULL, 56.46781217568035, 45.07692307692307, '2025-05-16 10:27:24.000000', 1, 3, 1, 1, 1, 'ACE'),
(43, 'SERVE', '#38b2ac', '2025-05-16 10:27:24.000000', NULL, NULL, NULL, 40.23164981405674, 48.358961838942314, '2025-05-16 10:27:24.000000', 1, 3, 1, 1, 1, 'ACE'),
(44, 'ATTACK', '#ff0000', '2025-05-16 10:58:57.000000', 95.33620461326684, 90.23931415264424, NULL, 4.561296864189345, 7.470083383413462, '2025-05-16 10:58:57.000000', 1, 4, 1, 1, 1, 'KILL'),
(45, 'ATTACK', '#ff0000', '2025-05-16 10:58:57.000000', 96.54443419434195, 11.947115384615385, NULL, 5.646525215252153, 80.8701923076923, '2025-05-16 10:58:57.000000', 1, 4, 1, 1, 1, 'SHOT'),
(46, 'RECEPTION', '#4361ee', '2025-05-16 10:58:57.000000', NULL, NULL, NULL, 91.99338868388683, 5.793269230769231, '2025-05-16 10:58:57.000000', 1, 4, 1, 1, 1, 'RECEPTION_ERROR'),
(47, 'RECEPTION', '#4361ee', '2025-05-16 10:58:57.000000', NULL, NULL, NULL, 11.550584255842558, 66.71634615384615, '2025-05-16 10:58:57.000000', 1, 4, 1, 1, 1, 'REGULAR_RECEPTION'),
(48, 'SERVE', '#38b2ac', '2025-05-16 10:58:57.000000', NULL, NULL, NULL, 14.748616236162363, 26.716346153846153, '2025-05-16 10:58:57.000000', 1, 4, 1, 1, 1, 'SERVE_ERROR'),
(49, 'ATTACK', '#ff0000', '2025-05-16 11:08:37.000000', 64.41085397653194, 78.10096153846153, NULL, 2.742014341590613, 12.870192307692307, '2025-05-16 11:08:37.000000', 1, 4, 25, 1, 1, 'SHOT'),
(50, 'RECEPTION', '#4361ee', '2025-05-16 11:08:37.000000', NULL, NULL, NULL, 78.49847626965857, 37.69230769230769, '2025-05-16 11:08:37.000000', 1, 4, 25, 1, 1, 'REGULAR_RECEPTION'),
(51, 'ATTACK', '#ff0000', '2025-05-16 11:08:37.000000', 52.55323507018008, 5.076923076923077, NULL, 8.094304170571219, 81.38461538461539, '2025-05-16 11:08:37.000000', 1, 4, 45, 3, 1, 'SHOT'),
(52, 'SERVE', '#38b2ac', '2025-05-16 11:08:37.000000', NULL, NULL, NULL, 97.27292216274853, 64.76923076923077, '2025-05-16 11:08:37.000000', 1, 4, 40, 3, 1, 'SERVE_ERROR'),
(53, 'ATTACK', '#ff0000', '2025-05-16 15:49:05.000000', 74.05699182113892, 72.7008526141827, NULL, 23.749488746108163, 52.08546799879807, '2025-05-16 15:49:05.000000', 1, 4, 1, 1, 1, 'SHOT'),
(54, 'ATTACK', '#ff0000', '2025-05-16 15:49:05.000000', 78.11603241154482, 41.45298884465144, NULL, 25.840509656317266, 65.45298884465144, '2025-05-16 15:49:05.000000', 1, 4, 1, 1, 1, 'SHOT'),
(55, 'ATTACK', '#ff0000', '2025-05-16 15:49:05.000000', 64.33989465016721, 50.06837346003606, NULL, 21.04346168583756, 46.06837346003606, '2025-05-16 15:49:05.000000', 1, 4, 1, 1, 1, 'SHOT'),
(56, 'RECEPTION', '#4361ee', '2025-05-16 15:49:05.000000', NULL, NULL, NULL, 75.77900904131111, 77.14529653695912, '2025-05-16 15:49:05.000000', 1, 4, 1, 1, 1, 'REGULAR_RECEPTION'),
(57, 'RECEPTION', '#4361ee', '2025-05-16 15:49:05.000000', NULL, NULL, NULL, 19.444445695677658, 63.6068349984976, '2025-05-16 15:49:05.000000', 1, 4, 24, 1, 1, 'REGULAR_RECEPTION'),
(58, 'ATTACK', '#ff0000', '2025-05-16 15:50:26.000000', 72.9499807510282, 61.93162184495192, NULL, 20.18245307575146, 45.31623722956731, '2025-05-16 15:50:26.000000', 1, 4, 1, 1, 1, 'KILL'),
(59, 'SERVE', '#38b2ac', '2025-05-16 16:39:59.000000', NULL, NULL, NULL, 50.68675811880189, 50.61538461538462, '2025-05-16 16:39:59.000000', 1, 3, 1, 1, 1, 'ACE');

-- --------------------------------------------------------

--
-- Table structure for table `teams`
--

CREATE TABLE `teams` (
  `id` bigint(20) NOT NULL,
  `color_primary` varchar(20) DEFAULT NULL,
  `color_secondary` varchar(20) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `founded_year` int(11) DEFAULT NULL,
  `location` varchar(100) DEFAULT NULL,
  `logo` varchar(255) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `teams`
--

INSERT INTO `teams` (`id`, `color_primary`, `color_secondary`, `created_at`, `description`, `founded_year`, `location`, `logo`, `name`, `updated_at`, `created_by`) VALUES
(1, '#d10a0a', '#e600de', '2025-05-15 04:47:18.000000', '', 2001, 'sirajganj', '', 'Dynamo', '2025-05-15 04:47:18.000000', 1),
(2, 'Red', 'White', '2025-05-15 10:56:53.000000', 'A historic team with passionate fans.', 1990, 'New York, USA', 'ny_warriors.png', 'New York Warriors', '2025-05-15 10:56:53.000000', 1),
(3, 'Blue', 'Gold', '2025-05-15 10:56:53.000000', 'Known for speed and discipline.', 1995, 'Tokyo, Japan', 'tokyo_blitz.png', 'Tokyo Blitz', '2025-05-15 10:56:53.000000', 1),
(4, 'Green', 'Black', '2025-05-15 10:56:53.000000', 'An upcoming powerhouse.', 2001, 'Dublin, Ireland', 'dublin_dragons.png', 'Dublin Dragons', '2025-05-15 10:56:53.000000', 1),
(5, 'Orange', 'Navy', '2025-05-15 10:56:53.000000', 'Famous for their youth academy.', 1985, 'São Paulo, Brazil', 'sao_legends.png', 'São Paulo Legends', '2025-05-15 10:56:53.000000', 1);

-- --------------------------------------------------------

--
-- Table structure for table `team_players`
--

CREATE TABLE `team_players` (
  `team_id` bigint(20) NOT NULL,
  `player_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `team_players`
--

INSERT INTO `team_players` (`team_id`, `player_id`) VALUES
(1, 1),
(1, 24),
(1, 23),
(1, 24),
(1, 25),
(1, 26),
(1, 27),
(1, 28),
(1, 29),
(1, 30),
(2, 31),
(2, 32),
(2, 33),
(2, 34),
(2, 35),
(2, 36),
(2, 37),
(2, 38),
(3, 39),
(3, 40),
(3, 41),
(3, 42),
(3, 43),
(3, 44),
(3, 45),
(3, 46),
(4, 47),
(4, 48),
(4, 49),
(4, 50),
(4, 51),
(4, 52),
(4, 53),
(4, 54),
(5, 55),
(5, 56),
(5, 57),
(5, 58),
(5, 59),
(5, 60),
(5, 61),
(5, 62);

-- --------------------------------------------------------

--
-- Table structure for table `tournaments`
--

CREATE TABLE `tournaments` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `location` varchar(200) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `start_date` date DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `tournaments`
--

INSERT INTO `tournaments` (`id`, `created_at`, `description`, `end_date`, `location`, `name`, `start_date`, `updated_at`, `created_by`) VALUES
(1, '2025-05-15 05:15:52.000000', '', '2025-05-20', 'sirajganj', 'Test Tour', '2025-05-13', '2025-05-15 05:15:52.000000', 1),
(2, '2025-05-16 10:56:40.000000', '', '2025-05-31', 'UAE', 'ICC WORLD CUP', '2025-05-16', '2025-05-16 10:56:40.000000', 1);

-- --------------------------------------------------------

--
-- Table structure for table `tournament_teams`
--

CREATE TABLE `tournament_teams` (
  `tournament_id` bigint(20) NOT NULL,
  `team_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(120) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `username` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `created_at`, `email`, `password`, `updated_at`, `username`) VALUES
(1, '2025-05-15 04:31:48.000000', 'shubratodn44985@gmail.com', '$2a$10$AAH14E9PEVivXKESLOn0PO0fX9ZqOc3KxVaREnhOTIkyFjsTtWj2W', '2025-05-15 04:31:48.000000', 'shubratodn'),
(2, '2025-05-15 04:31:48.000000', 'sourav@gmail.com', '$2a$10$AAH14E9PEVivXKESLOn0PO0fX9ZqOc3KxVaREnhOTIkyFjsTtWj2W', '2025-05-15 04:31:48.000000', 'sourav'),
(3, '2025-05-15 04:31:48.000000', 'hridoy@gmail.com', '$2a$10$AAH14E9PEVivXKESLOn0PO0fX9ZqOc3KxVaREnhOTIkyFjsTtWj2W', '2025-05-15 04:31:48.000000', 'hridoy');

-- --------------------------------------------------------

--
-- Table structure for table `user_roles`
--

CREATE TABLE `user_roles` (
  `user_id` bigint(20) NOT NULL,
  `role_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `user_roles`
--

INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES
(1, 3),
(2, 2),
(3, 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `matches`
--
ALTER TABLE `matches`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK1j4j1o96y9gieoioaur9b3p4n` (`created_by`),
  ADD KEY `FKeeniokyjgo5k6rmhjujatn27i` (`tournament_id`),
  ADD KEY `FK8k68nekawp47js52dq8720voe` (`home_team_id`),
  ADD KEY `FK2e8erbfecb0tjtq9iudg36bxu` (`away_team_id`);

--
-- Indexes for table `match_teams`
--
ALTER TABLE `match_teams`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKkxhdo8b725bswuo7f9wrjiraw` (`match_id`),
  ADD KEY `FK7dmy26j3c15d5uoj606g42biv` (`team_id`);

--
-- Indexes for table `players`
--
ALTER TABLE `players`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK39sho2loob263c92wbjaqwd2h` (`created_by_id`);

--
-- Indexes for table `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `statistics`
--
ALTER TABLE `statistics`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKl1f93tru8exn741qic78brtx7` (`created_by`),
  ADD KEY `FKmnfa4pc3emi3wlu88kfmax3tx` (`match_id`),
  ADD KEY `FK5q4upat5wya0sl3jc072d8tg9` (`player_id`),
  ADD KEY `FKfkuydwsi31hic99idqnbra51u` (`team_id`);

--
-- Indexes for table `teams`
--
ALTER TABLE `teams`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKpkesgpdiuhrjgaafjx09g8dx6` (`created_by`);

--
-- Indexes for table `team_players`
--
ALTER TABLE `team_players`
  ADD KEY `FKddxneji5ow8j3171oe6mc2gu0` (`player_id`),
  ADD KEY `FK3bhsykltbdhsmmb61l2ml12h` (`team_id`);

--
-- Indexes for table `tournaments`
--
ALTER TABLE `tournaments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKg9th3w94s0c70bexogt0g3syw` (`created_by`);

--
-- Indexes for table `tournament_teams`
--
ALTER TABLE `tournament_teams`
  ADD PRIMARY KEY (`tournament_id`,`team_id`),
  ADD KEY `FKfdh1y15gds2l1e08j7qlhm9e2` (`team_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  ADD UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`);

--
-- Indexes for table `user_roles`
--
ALTER TABLE `user_roles`
  ADD PRIMARY KEY (`user_id`,`role_id`),
  ADD KEY `FKh8ciramu9cc9q3qcqiv4ue8a6` (`role_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `matches`
--
ALTER TABLE `matches`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `match_teams`
--
ALTER TABLE `match_teams`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `players`
--
ALTER TABLE `players`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=74;

--
-- AUTO_INCREMENT for table `roles`
--
ALTER TABLE `roles`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `statistics`
--
ALTER TABLE `statistics`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=60;

--
-- AUTO_INCREMENT for table `teams`
--
ALTER TABLE `teams`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `tournaments`
--
ALTER TABLE `tournaments`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `matches`
--
ALTER TABLE `matches`
  ADD CONSTRAINT `FK1j4j1o96y9gieoioaur9b3p4n` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FK2e8erbfecb0tjtq9iudg36bxu` FOREIGN KEY (`away_team_id`) REFERENCES `teams` (`id`),
  ADD CONSTRAINT `FK8k68nekawp47js52dq8720voe` FOREIGN KEY (`home_team_id`) REFERENCES `teams` (`id`),
  ADD CONSTRAINT `FKeeniokyjgo5k6rmhjujatn27i` FOREIGN KEY (`tournament_id`) REFERENCES `tournaments` (`id`);

--
-- Constraints for table `match_teams`
--
ALTER TABLE `match_teams`
  ADD CONSTRAINT `FK7dmy26j3c15d5uoj606g42biv` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`),
  ADD CONSTRAINT `FKkxhdo8b725bswuo7f9wrjiraw` FOREIGN KEY (`match_id`) REFERENCES `matches` (`id`);

--
-- Constraints for table `players`
--
ALTER TABLE `players`
  ADD CONSTRAINT `FK39sho2loob263c92wbjaqwd2h` FOREIGN KEY (`created_by_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `statistics`
--
ALTER TABLE `statistics`
  ADD CONSTRAINT `FK5q4upat5wya0sl3jc072d8tg9` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`),
  ADD CONSTRAINT `FKfkuydwsi31hic99idqnbra51u` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`),
  ADD CONSTRAINT `FKl1f93tru8exn741qic78brtx7` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKmnfa4pc3emi3wlu88kfmax3tx` FOREIGN KEY (`match_id`) REFERENCES `matches` (`id`);

--
-- Constraints for table `teams`
--
ALTER TABLE `teams`
  ADD CONSTRAINT `FKpkesgpdiuhrjgaafjx09g8dx6` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

--
-- Constraints for table `team_players`
--
ALTER TABLE `team_players`
  ADD CONSTRAINT `FK3bhsykltbdhsmmb61l2ml12h` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`),
  ADD CONSTRAINT `FKddxneji5ow8j3171oe6mc2gu0` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`);

--
-- Constraints for table `tournaments`
--
ALTER TABLE `tournaments`
  ADD CONSTRAINT `FKg9th3w94s0c70bexogt0g3syw` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

--
-- Constraints for table `tournament_teams`
--
ALTER TABLE `tournament_teams`
  ADD CONSTRAINT `FKfdh1y15gds2l1e08j7qlhm9e2` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`),
  ADD CONSTRAINT `FKkbbpiasv8aqbh6uwc1m0wlvw4` FOREIGN KEY (`tournament_id`) REFERENCES `tournaments` (`id`);

--
-- Constraints for table `user_roles`
--
ALTER TABLE `user_roles`
  ADD CONSTRAINT `FKh8ciramu9cc9q3qcqiv4ue8a6` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  ADD CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
