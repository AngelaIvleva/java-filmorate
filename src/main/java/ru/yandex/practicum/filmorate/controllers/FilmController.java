package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate START_DATE = LocalDate.of(1895, 12, 28);
    private int id = 0;

    @GetMapping
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) { //добавить проверку на идентичность фильма с разными id?
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

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        try {
            validateFilm(film);
            if (films.containsKey(film.getId())) {
                films.replace(film.getId(), film);
                log.info("Film <<{}>> is updated", film.getName());
            } else {
                throw new ValidationException("There is no such film");
            }
        } catch (ValidationException ex) {
            log.error(ex.getMessage());
            throw new ValidationException(ex);
        }
        return film;
    }

    private int createId() {
        return ++id;
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
