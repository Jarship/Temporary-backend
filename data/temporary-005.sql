CREATE TABLE `scope` (
    `scope_id` int(11) NOT NULL AUTO_INCREMENT COMMENT `Scope ID`,
    `title` varchar(128) NOT NULL COMMENT `The display name of the scope`,
    `population` int(11) NOT NULL DEFAULT 0,
    `parent_id` int(11) NULL DEFAULT NULL,
    PRIMARY KEY (`scope_id`),
    FOREIGN KEY (`parent_id`) references scope(`scope_id`)
);

CREATE TABLE `structure` (
    `structure_id` int(11) NOT NULL AUTO_INCREMENT COMMENT `Structure ID`,
    `title` varchar(128) NOT NULL COMMENT `The display name of the structure`,
    PRIMARY KEY (`structure_id`)
);

CREATE TABLE `front` (
    `front_id` int(11) NOT NULL AUTO_INCREMENT COMMENT `Front ID`,
    `title` varchar(128) NOT NULL COMMENT `The Front Display Name`,
    `is_active` tinyint(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (`front_id`)
);

CREATE TABLE `structure_scope` (
    `scope_id` int(11) NOT NULL,
    `structure_id` int(11) NOT NULL,
    FOREIGN KEY (`scope_id`) references scope(`scope_id`),
    FOREIGN KEY (`structure_id`) references structure(`structure_id`),
    UNIQUE KEY `structure_scope` (`scope_id`, `structure_id`)
);

CREATE TABLE `front_scope` (
    `scope_id` int(11) NOT NULL,
    `front_id` int(11) NOT NULL,
    FOREIGN KEY (`scope_id`) references scope(`scope_id`),
    FOREIGN KEY (`front_id`) references front(`front_id`),
    UNIQUE KEY `front_scope` (`scope_id`, `front_id`)
);

CREATE TABLE `front_structure` (
    `structure_id` int(11) NOT NULL,
    `front_id` int(11) NOT NULL,
    FOREIGN KEY (`structure_id`) references structure(`structure_id`),
    FOREIGN KEY (`front_id`) references front(`front_id`),
    UNIQUE KEY `front_structure` (`structure_id`, `front_id`)
);

CREATE TABLE `scope_assembly` (
    `assembly_id` int(11) NOT NULL,
    `scope_id` int(11) NOT NULL,
    FOREIGN KEY (`assembly_id`) references assembly(`assembly_id`),
    FOREIGN KEY (`scope_id`) references scope(`scope_id`),
    UNIQUE KEY `front_assembly` (`assembly_id`, `scope_id`)
);



-- Ran in Dev 04-29-24