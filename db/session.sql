CREATE TABLE session (
    id SERIAL PRIMARY KEY,
    time timestamp,
    price int
);


INSERT INTO session(time, price) values ('2023-05-15 17:30', 450);