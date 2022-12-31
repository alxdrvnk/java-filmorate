CREATE TABLE "user" (
  "id" integer PRIMARY KEY,
  "email" varchar,
  "login" varchar,
  "name" varchar,
  "birthday" date
);

CREATE TABLE "film" (
  "id" integer PRIMARY KEY,
  "title" varchar,
  "description" varchar,
  "release_date" date,
  "duration" integer,
  "mpa_id" integer
);

CREATE TABLE "genre" (
  "id" integer PRIMARY KEY,
  "name" varchar
);

CREATE TABLE "filmgenres" (
  "film_id" integer,
  "genre_id" integer
);

CREATE TABLE "mpa" (
  "mpa_id" integer,
  "name" varchar
);

CREATE TABLE "friendslist" (
  "user_id" integer,
  "friend_jd" integer
);

CREATE TABLE "likes" (
  "user_id" integer,
  "film_id" integer
);

ALTER TABLE "filmgenres" ADD FOREIGN KEY ("genre_id") REFERENCES "genre" ("id");

ALTER TABLE "filmgenres" ADD FOREIGN KEY ("film_id") REFERENCES "film" ("id");

ALTER TABLE "film" ADD FOREIGN KEY ("mpa_id") REFERENCES "mpa" ("mpa_id");

ALTER TABLE "friendslist" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id");

ALTER TABLE "friendslist" ADD FOREIGN KEY ("friend_jd") REFERENCES "user" ("id");

ALTER TABLE "likes" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id");

ALTER TABLE "likes" ADD FOREIGN KEY ("film_id") REFERENCES "film" ("id");
