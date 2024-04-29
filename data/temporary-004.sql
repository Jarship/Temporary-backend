ALTER TABLE account
    ADD UNIQUE KEY `phone` (`phone`, `account_type`);

CREATE TABLE `assembly` (
    `assembly_id` int(11) NOT NULL AUTO_INCREMENT COMMENT `The Assembly ID`,
    `name` varchar(128) NOT NULL COMMENT `This must be unique`,
    `hidden` tinyint(1) NOT NULL DEFAULT '0',
    PRIMARY KEY (`assembly_id`),
    UNIQUE KEY `assembly_name` (`name`),
);

CREATE TABLE `assembly_user` (
    `relationship_id` int(11) NOT NULL AUTO_INCREMENT,
    `assembly_id` int(11) NOT NULL,
    `account_id` int(11) NOT NULL,
    `relationship` enum('NONE', 'IGNORE', 'FOLLOW', 'JOIN', 'ACCEPTED') NOT NULL DEFAULT 'NONE',
    PRIMARY KEY(`relationship_id`),
    FOREIGN KEY (`assembly_id`) references assembly(`assembly_id`),
    FOREIGN KEY (`account_id`) references account(`account_id`),
    UNIQUE KEY `assembly_user` (`assembly_id`, `account_id`)
);

-- Ran in Dev 04-28-24
