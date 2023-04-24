package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    private int id = 0;

    private int createId() {
        return ++id;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(int id) {
        if (films.get(id) != null) {
            return films.get(id);
        } else {
            throw new NotFoundException("Film ID " + id + "is not found");
        }
    }

    @Override
    public Film createFilm(Film film) {
        try {
            film.setId(createId());
            films.put(film.getId(), film);
            log.info("Film <<{}>> is created", film.getName());
        } catch (ValidationException ex) {
            log.error(ex.getMessage());
            throw new ValidationException(ex);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        try {
            if (films.containsKey(film.getId())) {
                films.replace(film.getId(), film);
                log.info("Film <<{}>> is updated", film.getName());
            } else {
                throw new NotFoundException("There is no such film");
            }
        } catch (NotFoundException ex) {
            log.error(ex.getMessage());
            throw new NotFoundException(ex.getMessage());
        }
        return film;
    }

    @Override
    public void deleteFilmById(int id) {
        if (films.containsKey(id)) {
            films.remove(id);
            log.info("Film ID <<{}>> is deleted", id);
        } else {
            log.info("Film ID <<{}>> is not found", id);
        }
    }

    @Override
    public void addLike(int filmId, int userId) {
        if (films.containsKey(id)) {
            films.get(filmId).getLikesList().add(userId);
            log.info("User ID {} added like to film ID {}", userId, filmId);
        } else {
            log.info("Film ID <<{}>> is not found", id);
        }
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        if (films.containsKey(filmId)) {
            films.get(filmId).getLikesList().remove(userId);
            log.info("User ID {} deleted like of film ID {}", userId, filmId);
        } else {
            log.info("Film ID <<{}>> is not found", id);
        }
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

        return films.values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikesList().size(), f1.getLikesList().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
