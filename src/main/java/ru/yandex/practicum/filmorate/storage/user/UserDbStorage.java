package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> getUsers() {
        String sql = "SELECT * FROM USERS";
        return jdbcTemplate.query(sql, this::makeUser);
    }

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO USERS (EMAIL, LOGIN, USER_NAME, BIRTHDAY) " +
                "VALUES ( ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"USER_ID"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId((int) Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        validateUser(user.getId());
        String sql = "UPDATE USERS SET EMAIL = ?, LOGIN = ?, USER_NAME = ?, BIRTHDAY = ? WHERE USER_ID = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        log.info("User id " + user.getId() + " is upd.");
        return user;
    }

    @Override
    public User getUserById(int id) {
        validateUser(id);
        String sql = "SELECT * FROM USERS WHERE USER_ID = ?";
        return jdbcTemplate.queryForObject(sql, this::makeUser, id);
    }

    @Override
    public void deleteUserById(int id) {
        validateUser(id);
        String sql = "DELETE FROM USERS WHERE USER_ID = ?";
        jdbcTemplate.update(sql, id);
        log.info("User ID {} is deleted", id);

    }

    @Override
    public void addFriend(int userId, int friendId) {
        validateUser(userId);
        validateUser(friendId);
        String sqlForWrite = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID, STATUS) " +
                "VALUES (?, ?, ?)";
        String sqlForUpdate = "UPDATE FRIENDS SET STATUS = ? " +
                "WHERE USER_ID = ? AND FRIEND_ID = ?";
        String sqlCheckFriendship = "SELECT * FROM FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlCheckFriendship, userId, friendId);

        if (userRows.first()) {
            jdbcTemplate.update(sqlForUpdate, FriendshipStatus.FOLLOW.toString(), userId, friendId);
        } else {
            jdbcTemplate.update(sqlForWrite, userId, friendId, FriendshipStatus.UNFOLLOW.toString());
        }
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        validateUser(userId);
        validateUser(friendId);
        String sqlQuery = "DELETE FROM FRIENDS WHERE (USER_ID = ? AND FRIEND_ID =?) OR (USER_ID =? AND FRIEND_ID = ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId, friendId, userId);
    }

    @Override
    public List<User> getFriendList(int id) {
        validateUser(id);
        String sql = "SELECT * FROM USERS WHERE USER_ID IN (SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?)";
        return jdbcTemplate.query(sql, this::makeUser, id);
    }

    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        validateUser(userId);
        validateUser(friendId);
        String sql = "SELECT U.USER_ID, U.EMAIL, U.LOGIN, U.USER_NAME, U.BIRTHDAY " +
                "FROM FRIENDS AS F " +
                "LEFT JOIN FRIENDS AS FF ON FF.FRIEND_ID = F.FRIEND_ID AND FF.USER_ID = ?" +
                "LEFT JOIN USERS AS U ON U.USER_ID = F.FRIEND_ID " +
                "WHERE F.USER_ID = ? ";

        return jdbcTemplate.query(sql, this::makeUser, userId, friendId);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getInt("USER_ID"))
                .email(rs.getString("EMAIL"))
                .login(rs.getString("LOGIN"))
                .name(rs.getString("USER_NAME"))
                .birthday(rs.getDate("BIRTHDAY").toLocalDate())
                .build();
    }

    private void validateUser(int userId) {
        String checkUser = "SELECT * FROM USERS WHERE USER_ID = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(checkUser, userId);

        if (!userRows.next()) {
            log.warn("User id {} is not found", userId);
            throw new NotFoundException("User id " + userId + " is not found");
        }
    }
}
