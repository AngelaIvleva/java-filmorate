Filmorate project.
![ER diagram](https://github.com/AngelaIvleva/java-filmorate/blob/f1573a0748f15551253a3e72b3511c34b2ea5930/filmorate.png)
1. Create User
```SQL
INSERT INTO USERS (EMAIL, LOGIN, USER_NAME, BIRTHDAY)
VALUES ( ?, ?, ?, ?);
```
2. Update User
```SQL
UPDATE USERS 
SET EMAIL = ?,
    LOGIN = ?,
    USER_NAME = ?,
    BIRTHDAY = ?
WHERE USER_ID = ?;
```
3. Find user by id
```SQL
SELECT *
FROM USERS
WHERE USER_ID = ?;
```
4. Get film's genres
```SQL
SELECT G.GENRE_ID, G.GENRE_NAME
FROM GENRE AS G
JOIN FILM_GENRE AS FG ON G.GENRE_ID = FG.GENRE_ID
WHERE FG.FILM_ID =?;
```
5. Add like to film
```SQL
INSERT INTO FILM_LIKES (FILM_ID, USER_ID)
VALUES (?, ?);
```
6. Get Top Films
```SQL
SELECT F.FILM_ID,
       F.FILM_NAME,
       F.DESCRIPTION,
       F.RELEASE_DATE,
       F.DURATION,
       F.MPA_ID,
       M.MPA_ID,
       M.MPA_NAME
FROM FILMS AS F
JOIN FILM_LIKES L ON F.FILM_ID = L.FILM_ID
JOIN MPA AS M ON F.MPA_ID = M.MPA_ID
GROUP BY F.FILM_NAME
ORDER BY COUNT(L.USER_ID)
DESC LIMIT ?;
```