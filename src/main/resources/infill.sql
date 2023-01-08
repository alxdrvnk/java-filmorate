INSERT INTO genre (name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик')
ON CONFLICT DO NOTHING;

INSERT INTO mpa (name)
VALUES ('G'),
      ('PG'),
      ('PG-13'),
      ('R'),
      ('NC-17')
ON CONFLICT DO NOTHING;