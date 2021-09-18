CREATE TABLE ticket (
    id SERIAL PRIMARY KEY,
    session_id INT NOT NULL REFERENCES session(id),
    row INT NOT NULL,
    cell INT NOT NULL,
    UNIQUE (row, cell),
    account_id INT NOT NULL REFERENCES account(id)
);