INSERT INTO users (email, login, name, birthday)
VALUES ('user1@email.mail', 'user1-login', 'user1-name', '1944-05-14'),
       ('user2@email.mail', 'user2-login', 'user2-name', '1954-01-24'),
       ('user3@email.mail', 'user3-login', 'user3-name', '1974-11-04');

INSERT INTO films (title, description, release_date, duration, rate, mpa_id)
VALUES ('SW', 'SW description', '1977-05-25', 121, 0, 2),
       ('Indiana Jones and the Raiders of the Lost Ark', 'Indiana Jones description', '1981-06-12', 115, 0, 4),
       ('The Shawshank Redemption', 'The Shawshank Redemption description', '1994-09-10', 142, 0, 4);

INSERT INTO film_genres (film_id, genre_id)
VALUES (3, 2),
       (1, 6),
       (2, 6),
       (2, 1);