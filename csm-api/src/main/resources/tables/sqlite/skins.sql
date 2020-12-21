CREATE TABLE IF NOT EXISTS skins(
    id IDENTITY PRIMARY KEY,
    uuid VARCHAR(38),
    name VARCHAR(16),
    default_value TEXT NOT NULL,
    default_signature TEXT NOT NULL,
    custom_value TEXT,
    custom_signature TEXT
);