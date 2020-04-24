CREATE TABLE IF NOT EXISTS skins(
    id INT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(38),
    name VARCHAR(16),
    default_value VARCHAR(512) NOT NULL,
    default_signature VARCHAR(512) NOT NULL,
    custom_value VARCHAR(1024),
    custom_signature VARCHAR(1024)
);