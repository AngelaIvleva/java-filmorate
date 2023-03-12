package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final LocalDate START_DATE = LocalDate.of(1895, 12, 28);

    private final Map<Long, Film> films = new HashMap<>();

    private long id = 0;

    private long createId() {
        return ++id;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Long id) {
        if (films.get(id) != null) {
            return films.get(id);
        } else {
            throw new NotFoundException("Film ID " + id + "is not found");
        }
    }

    @Override
    public Film createFilm(Film film) {
        try {
            validateFilm(film);
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
            validateFilm(film);
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
    public void deleteFilmById(Long id) {
        if (films.containsKey(id)) {
            films.remove(id);
            log.info("Film ID <<{}>> is deleted", id);
        } else {
            log.info("Film ID <<{}>> is not found", id);
        }
    }

    public void validateFilm(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(START_DATE)) {
            throw new ValidationException("date release cannot be earlier than 28.12.1895");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Name cannot be null or empty");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Description must be max 200 characters");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Duration of the film must be positive");
        }
    }
}
