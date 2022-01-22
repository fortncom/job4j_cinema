CREATE TABLE account (
    id SERIAL PRIMARY KEY,
    username VARCHAR NOT NULL,
    email VARCHAR NOT NULL UNIQUE,
    phone VARCHAR NOT NULL UNIQUE
);

CREATE TABLE hall (
id SERIAL PRIMARY KEY,
places integer[]
);

CREATE TABLE session (
    id SERIAL PRIMARY KEY,
    time timestamp,
    price int
);

CREATE TABLE ticket (
    id SERIAL PRIMARY KEY,
    session_id INT NOT NULL REFERENCES session(id),
    row INT NOT NULL,
    cell INT NOT NULL,
    UNIQUE (row, cell),
    account_id INT NOT NULL REFERENCES account(id)
);

INSERT INTO session(time, price) values ('2023-05-15 17:30', 450);

INSERT INTO hall(places) values ('{5, 4, 3, 2}');

INSERT INTO account(username, email, phone) values('fortncom', 'root@local', 89173333333);

INSERT INTO ticket(session_id, row, cell, account_id) values(1, 1, 1, 1);
