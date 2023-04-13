package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

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
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT * FROM FILMS AS F " +
                "JOIN MPA AS M ON F.MPA_ID = M.MPA_ID";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    @Override
    public Film getFilmById(int id) {
        validateFilm(id);
        String sql =
                "SELECT FILM_ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, M.MPA_ID, M.MPA_NAME FROM FILMS " +
                        "JOIN MPA M ON M.MPA_ID = FILMS.MPA_ID " +
                        "WHERE FILM_ID = ?";
        return jdbcTemplate.queryForObject(sql, this::makeFilm, id);
    }

    @Override
    public Film createFilm(Film film) {
        String sql = "INSERT INTO FILMS(FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        addGenres(film);
        film.setGenres(getFilmGenres(film.getId()));
        log.info("Film " + film.getName() + " was created");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validateFilm(film.getId());
        String sql = "UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, " +
                "MPA_ID = ? WHERE FILM_ID = ?";

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        removeGenres(film.getId());
        addGenres(film);
        film.setGenres(getFilmGenres(film.getId()));
        log.info("Film id " + film.getId() + " was updated.");
        return film;
    }

    @Override
    public void deleteFilmById(int id) {
        validateFilm(id);
        removeGenres(id);
        String sql = "DELETE FROM FILMS WHERE FILM_ID =?;";
        jdbcTemplate.update(sql, id);

        log.info("Film id " + id + " was deleted.");
    }

    @Override
    public void addLike(int filmId, int userId) {
        validateFilm(filmId);
        validateUser(userId);
        log.info("User id " + userId + " liked film id " + filmId);
        String sql = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        validateFilm(filmId);
        validateUser(userId);
        String sql = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        if (count == null) {
            log.info("'Count' is null");
            throw new IncorrectParameterException("'Count' is null");
        }
        if (count <= 0) {
            log.info("'Count' is negative value");
            throw new IncorrectParameterException("'Count' is negative value");
        }
        String sql = "SELECT F.FILM_ID, F.FILM_NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION," +
                " F.MPA_ID, M.MPA_ID, M.MPA_NAME " +
                "FROM FILMS AS F " +
                "LEFT JOIN FILM_LIKES L ON F.FILM_ID = L.FILM_ID " +
                "LEFT JOIN MPA AS M ON F.MPA_ID = M.MPA_ID " +
                "GROUP BY F.FILM_NAME " +
                "ORDER BY COUNT(L.USER_ID) " +
                "DESC LIMIT ?";

        return jdbcTemplate.query(sql, this::makeFilm, count);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getInt("FILM_ID"))
                .name(rs.getString("FILM_NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .mpa(new Mpa(rs.getInt("MPA_ID"), rs.getString("MPA_NAME")))
                .build();
        film.setGenres(getFilmGenres(film.getId()));
        return film;
    }

    private List<Genre> getFilmGenres(int id) {
        String sql = "SELECT G.GENRE_ID, G.GENRE_NAME " +
                "FROM GENRE AS G " +
                "JOIN FILM_GENRE AS FG ON G.GENRE_ID = FG.GENRE_ID " +
                "WHERE FG.FILM_ID =?";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) ->
                new Genre(rs.getInt("GENRE_ID"), rs.getString("GENRE_NAME")), id);
        return genres;
    }

    private void removeGenres(int id) {
        String sql = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, id);
    }

    private void addGenres(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                String sql = "MERGE INTO FILM_GENRE (FILM_ID, GENRE_ID) KEY (FILM_ID,GENRE_ID) VALUES (?, ?)";
                jdbcTemplate.update(sql, film.getId(), genre.getId());
            }
        }
    }

    private void validateFilm(int filmId) {
        String checkFilm = "SELECT * FROM FILMS WHERE FILM_ID = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(checkFilm, filmId);

        if (!filmRows.next()) {
            log.warn("Film id {} is not found", filmId);
            throw new NotFoundException("Film ID " + filmId + " is not found");
        }
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
