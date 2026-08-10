CREATE PROCEDURE findByEmail(IN input_Email VARCHAR)
BEGIN
    SELECT *
    FROM users
    WHERE email = input_Email;
END;