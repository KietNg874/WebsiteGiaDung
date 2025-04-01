INSERT INTO role (id, name, description)
VALUES (1, 'ADMIN', 'Admin')
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);

INSERT INTO role (id, name, description)
VALUES (2, 'USER', 'User')
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);