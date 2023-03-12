package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Long filmId, Long userId) {
        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
        if (user != null && film != null) {
            Set<Long> likes = film.getLikesList();
            likes.add(userId);
            film.setLikesList(likes);
            log.info("User ID {} added like to film ID {}", userId, filmId);
        } else {
            log.info("like have not been added");
            throw new NotFoundException("User ID " + userId + "or film ID " + filmId + "is not found");
        }
    }

    public void deleteLike(Long filmId, Long userId) {
        User user = userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
        if (user != null && film != null) {
            Set<Long> likes = film.getLikesList();
            likes.remove(userId);
            film.setLikesList(likes);
            log.info("User ID {} deleted like of film ID {}", userId, filmId);
        } else {
            log.info("like have not been deleted");
            throw new NotFoundException("User ID " + userId + "or film ID " + filmId + "is not found");
        }
    }

    public List<Film> getTop10Films(Integer count) {
        if (count == null) {
            log.info("'Count' is null");
            throw new IncorrectParameterException("'Count' is null");
        }
        if (count <= 0) {
            log.info("'Count' is negative value");
            throw new IncorrectParameterException("'Count' is negative value");
        }
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikesList().size(), f1.getLikesList().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void deleteFilmById(Long id) {
        filmStorage.deleteFilmById(id);
    }
}
