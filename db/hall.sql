CREATE TABLE hall (
id SERIAL PRIMARY KEY,
places integer[]
);

INSERT INTO hall(places) values ('{5, 4, 3, 2}');
