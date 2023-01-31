INSERT INTO users (email, login, name, birthday)
VALUES ('user1@email.mail', 'user1-login', 'user1-name', '1944-05-14'),
       ('user2@email.mail', 'user2-login', 'user2-name', '1954-01-24'),
       ('user3@email.mail', 'user3-login', 'user3-name', '1974-11-04');

INSERT INTO films (title, description, release_date, duration, rate, mpa_id)
VALUES ('SW', 'SW description', '1977-05-25', 121, 0, 2),
       ('Indiana Jones and the Raiders of the Lost Ark', 'Indiana Jones description', '1981-06-12', 115, 0, 4),
       ('The Shawshank Redemption', 'The Shawshank Redemption description', '1994-09-10', 142, 0, 1);

INSERT INTO reviews(user_id, film_id, content, is_positive, useful)
VALUES (1, 1, 'test content 1', true, 0),
       (1, 2, 'test content 2', false, 0),
       (2, 1, 'test content 3', true, 0);

INSERT INTO film_genres (film_id, genre_id)
VALUES (3, 2),
       (1, 6),
       (2, 6),
       (2, 1);

INSERT INTO DIRECTORS (id, name)
VALUES (1, 'Стивен Спилберг'),
       (2, 'Питер Джексон'),
       (3, 'Джеймс Кэмерон');

INSERT INTO LIKES(USER_ID, FILM_ID)
VALUES ( 1, 1 ),
       ( 2, 1 ),
       ( 3, 1 );

INSERT INTO LIKES(USER_ID, FILM_ID)
VALUES ( 1, 2 ),
       ( 2, 2 );