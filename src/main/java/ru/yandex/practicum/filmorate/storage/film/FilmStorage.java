package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Collection<Film> getAllFilms();

    Film getFilmById(int id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilmById(int id);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Film> getPopularFilms(Integer count);

}
