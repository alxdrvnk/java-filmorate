INSERT INTO DIRECTORS (id, name)
VALUES (1, 'Стивен Спилберг'),
       (2, 'Питер Джексон'),
       (3, 'Джеймс Кэмерон');

INSERT INTO users (email, login, name, birthday)
VALUES ('user1@email.mail', 'user1-login', 'user1-name', '1944-05-14'),
       ('user2@email.mail', 'user2-login', 'user2-name', '1954-01-24'),
       ('user3@email.mail', 'user3-login', 'user3-name', '1974-11-04'),
       ('user4@email.mail', 'user4-login', 'user4-name', '1991-12-23'),
       ('user5@email.mail', 'user5-login', 'user5-name', '1987-09-09'),
       ('user6@email.mail', 'user6-login', 'user6-name', '2001-07-19');

INSERT INTO FILMS (title, description, release_date, duration, rate, mpa_id)
VALUES ('Фильм-1', 'film1', '1987-02-01', 50, 0, 1);
INSERT INTO FILMS (title, description, release_date, duration, rate, mpa_id)
VALUES ('Фильм-2', 'film2', '1988-03-07', 150, 0, 2);
INSERT INTO FILMS (title, description, release_date, duration, rate, mpa_id)
VALUES ('Фильм-3', 'film3', '1989-04-09', 134, 0, 3);
INSERT INTO FILMS (title, description, release_date, duration, rate, mpa_id)
VALUES ('Фильм-4', 'film4', '1990-05-12', 12, 0, 4);
INSERT INTO FILMS (title, description, release_date, duration, rate, mpa_id)
VALUES ('Фильм-5', 'film5', '1991-06-21', 76, 0, 5);
INSERT INTO FILMS (title, description, release_date, duration, rate, mpa_id)
VALUES ('Фильм-6', 'film6', '1992-07-03', 103, 0, 1);
INSERT INTO FILMS (title, description, release_date, duration, rate, mpa_id)
VALUES ('Фильм-7', 'film7', '1993-07-03', 103, 0, 1);

INSERT INTO FILM_DIRECTORS (DIRECTOR_ID, FILM_ID)
VALUES (1, 1),
       (1, 3),
       (2, 1),
       (2, 2);

INSERT INTO film_genres (film_id, genre_id)
VALUES (1, 2),
       (1, 6),
       (2, 6),
       (2, 1),
       (3, 1);

INSERT INTO LIKES(USER_ID, FILM_ID)
VALUES (1, 1),
       (2, 1),
       (3, 1),
       (4, 1),
       (5, 1);
INSERT INTO LIKES(USER_ID, FILM_ID)
VALUES (1, 3);

INSERT INTO LIKES(USER_ID, FILM_ID)
VALUES (1, 2),
       (2, 2);