package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate START_DATE = LocalDate.of(1895, 12, 28);

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getTop10Films(@RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        return filmService.getTop10Films(count);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long filmId,
                        @PathVariable Long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Long filmId,
                           @PathVariable Long userId) {
        filmService.deleteLike(filmId, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteFilmById(@PathVariable Long id) {
        filmService.deleteFilmById(id);
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
