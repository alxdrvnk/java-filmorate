SET REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE users RESTART IDENTITY;
TRUNCATE TABLE films RESTART IDENTITY;
TRUNCATE TABLE friend_list RESTART IDENTITY;
TRUNCATE TABLE likes RESTART IDENTITY;
TRUNCATE TABLE film_genres RESTART IDENTITY;
TRUNCATE TABLE events RESTART IDENTITY;
TRUNCATE TABLE reviews RESTART IDENTITY;
TRUNCATE TABLE review_likes RESTART IDENTITY;
SET REFERENTIAL_INTEGRITY TRUE;

