ALTER TABLE account
    ADD COLUMN confirmation_code varchar(24) DEFAULT NULL;

-- Ran in Dev 12-7-23