CREATE TABLE IF NOT EXISTS skins(
    id IDENTITY PRIMARY KEY,
    uuid VARCHAR(38),
    name VARCHAR(16),
    default_value VARCHAR(2048) NOT NULL,
    default_signature VARCHAR(2048) NOT NULL,
    custom_value VARCHAR(2048),
    custom_signature VARCHAR(2048)
);