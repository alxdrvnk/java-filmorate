Insert INTO genre (name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');

INSERT INTO mpa (name)
VALUES ('G'),
      ('PG'),
      ('PG-13'),
      ('R'),
      ('NC-17');

MERGE INTO event_types (id, name)
VALUES (1, 'LIKE'),
       (2, 'REVIEW'),
       (3, 'FRIEND');

MERGE INTO operations (id, name)
VALUES (1, 'REMOVE'),
       (2, 'REVIEW'),
       (3, 'FRIEND');
