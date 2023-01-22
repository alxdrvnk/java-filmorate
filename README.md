# java-filmorate

# ER-diagram
![FilmorateDb](https://github.com/alxdrvnk/java-filmorate/blob/dev/Filmorate.png)


# Основные запросы

#### Получение списка друзей для User:
```
SELECT * FROM users 
WHERE id IN(SELECT fl.friend_id FROM friend_list AS fl 
            WHERE fl.user_id = user_id
            UNION 
            SELECT fl.user_id FROM friend_list AS fl  
            WHERE fl.friend_id = user_id AND fl.state = true);
```

#### Получение списка общих друзей для User1 и User2:
```
SELECT * FROM users
WHERE id IN(
	SELECT fu.friends_id FROM (
		SELECT fl.friend_id AS friends_id FROM friend_list AS fl 
		WHERE fl.user_id = user1_id 
		UNION  
		SELECT fl.user_id FROM friend_list AS fl 
		WHERE fl.friend_id = user1_id AND fl.state = true) AS fu
	INNER JOIN (
		SELECT fl.friend_id AS friends_id FROM friend_list AS fl 
		WHERE fl.user_id = user2_id
		UNION  
		SELECT fl.user_id FROM friend_list AS fl 
		WHERE fl.friend_id = user2_id AND fl.state = true) AS fou
	ON fou.friends_id = fu.friends_id);
```
#### Получение списка из _count_ самых популярных фильмов:
```
SELECT flm.*, mpa.name AS mpa_name, COALESCE(fl.film_likes,0) AS likes 
FROM films AS flm 
INNER JOIN mpa ON mpa.id = flm.mpa_id 
LEFT JOIN (SELECT lk.film_id, COUNT(user_id) AS film_likes 
           FROM likes AS lk 
           GROUP BY lk.film_id) AS fl ON fl.film_id = flm.id
ORDER BY likes DESC
LIMIT count 
```