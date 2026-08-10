DROP PROCEDURE IF EXISTS findByEmail;

CREATE PROCEDURE findByEmail(
    IN input_Email VARCHAR(255)
)
BEGIN
    SELECT *
    FROM users
    WHERE email = input_Email;
END;