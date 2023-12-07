CREATE TABLE `account` (
    `account_id` int(11) NOT NULL AUTO_INCREMENT COMMENT `Our account ID`,
    `email` varchar(128) NOT NULL COMMENT `This must be unique per account_type`,
    `password` varchar(256) DEFAULT NULL COMMENT `Null indicates an account that does not log in`,
    `password_salt` varchar(64) DEFAULT NULL,
    `password_reset_token` varchar(128) DEFAULT NULL,
    `account_enabled` tinyint(1) NOT NULL DEFAULT '0',
    `account_type` enum('ADMIN', 'USER') NOT NULL COMMENT `Defines the primary role of this account`,
    `username` varchar(128) NOT NULL Comment `This must be unique`
    `external_credential` varchar(128) UNIQUE KEY DEFAULT NULL,
    PRIMARY KEY (`account_id`),
    UNIQUE KEY `email` (`email`, `account_type`)
    UNIQUE KEY `password_reset_token` (`password_reset_token`)
    KEY `username` (`username`)
    );

-- Saved for future
--CREATE TABLE `account_relationships` (
--    `relationship_id` int(11) NOT NULL AUTO_INCREMENT,
--    `primary_id` int(11) NOT NULL,
--    `secondary_id` int(11) NOT NULL,
--    `relationship` enum('BLOCK','FOLLOW') NOT NULL DEFAULT 'FOLLOW',
--    PRIMARY_KEY (`relationship_id`),
--    FOREIGN KEY (`primary_id`) references account(`account_id`)
--    FOREIGN KEY (`secondary_id`) references account(`account_id`)
--);

-- Ran in Dev 12-7-23