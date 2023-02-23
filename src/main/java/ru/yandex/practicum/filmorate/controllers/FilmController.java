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
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate START_DATE = LocalDate.of(1895, 12, 28);
    private int id = 0;

    @GetMapping("/films")
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping(value = "/film")
    public Film createFilm(@Valid @RequestBody Film film) {
        try {
            film.setId(createId());
            validation(film);
            films.put(film.getId(), film);
            log.debug("Film <<{}>> is created", film.getName());
        } catch (ValidationException ex) {
            log.warn(ex.getMessage());
            throw new ValidationException(ex);
        }
        return film;
    }

    @PutMapping(value = "/film")
    public Film updateFilm(@Valid @RequestBody Film film) {
        try {
            validation(film);
            if (films.containsKey(film.getId())) {
                films.replace(film.getId(), film);
                log.debug("Film <<{}>> is updated", film.getName());
            } else {
                throw new ValidationException("There is no such movie");
            }
        } catch (ValidationException ex) {
            log.warn(ex.getMessage());
            throw new ValidationException(ex);
        }
        return film;
    }

    private int createId() {
        return id++;
    }

    public void validation(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(START_DATE)) {
            throw new ValidationException("date release cannot be earlier than 28.12.1895");
        }
    }
}
