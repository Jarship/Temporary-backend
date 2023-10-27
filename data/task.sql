CREATE DATABASE temporary_app;

CREATE TABLE `task` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `title` varchar(128) NOT NULL,
    `description` varchar(256) NOT NULL,
    `status` enum('INCOMPLETE', 'DONE') NOT NULL DEFAULT 'INCOMPLETE',
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT NOW() ON UPDATE NOW(),
    PRIMARY KEY(`id`)
);