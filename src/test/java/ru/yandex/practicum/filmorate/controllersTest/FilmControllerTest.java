package ru.yandex.practicum.filmorate.controllersTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    FilmController filmController;

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));
    }

    @Test
    void shouldCreateFilm() {
        Film film = Film.builder()
                .id(0)
                .name("Harry Potter and the sorcerer's stone")
                .description("The story of a boy who learns on his 11th birthday that he is the orphaned " +
                        "son of two powerful wizards and possesses unique magical powers of his own.")
                .releaseDate(LocalDate.of(2001, 11, 16))
                .duration(152)
                .build();

        assertEquals("Harry Potter and the sorcerer's stone", filmController.createFilm(film).getName());
    }

    @Test
    void shouldAssignCorrectIdWhenCreateNewFilm() {
        Film film = Film.builder()
                .id(33)
                .name("Harry Potter and the sorcerer's stone")
                .description("The story of a boy who learns on his 11th birthday that he is the orphaned " +
                        "son of two powerful wizards and possesses unique magical powers of his own.")
                .releaseDate(LocalDate.of(2001, 11, 16))
                .duration(152)
                .build();
        assertEquals(1, filmController.createFilm(film).getId());
    }

    @Test
    void shouldNotCreateFilmWhenNameIsEmpty() {
        Film film = Film.builder()
                .id(0)
                .name(" ")
                .description("The story of a boy who learns on his 11th birthday that he is the orphaned " +
                        "son of two powerful wizards and possesses unique magical powers of his own.")
                .releaseDate(LocalDate.of(2001, 11, 16))
                .duration(152)
                .build();
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldCreateFilmWhenReleaseDateOnBirthOfCinema() {
        Film film = Film.builder()
                .id(0)
                .name("Harry Potter and the sorcerer's stone")
                .description("The story of a boy who learns on his 11th birthday that he is the orphaned " +
                        "son of two powerful wizards and possesses unique magical powers of his own.")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(152)
                .build();
        assertEquals(LocalDate.of(1895, 12, 28),
                filmController.createFilm(film).getReleaseDate());
    }

    @Test
    void shouldNotCreateFilmWhenReleaseDateBeforeBirthOfCinema() {
        Film film = Film.builder()
                .id(0)
                .name("Harry Potter and the sorcerer's stone")
                .description("The story of a boy who learns on his 11th birthday that he is the orphaned " +
                        "son of two powerful wizards and possesses unique magical powers of his own.")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(152)
                .build();
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldNotCreateFilmWhenDescriptionLengthIsMore200() {
        Film film = Film.builder()
                .id(0)
                .name("Harry Potter and the sorcerer's stone")
                .description("Based on the first of J.K. Rowling's popular children's novels about Harry Potter. " +
                        "The story of a boy who learns on his 11th birthday that he is the orphaned " +
                        "son of two powerful wizards and possesses unique magical powers of his own." +
                        "Invited to attend Hogwarts School of Witchcraft and Wizardry, " +
                        "Harry embarks on the adventure of a lifetime. " +
                        "At Hogwarts, he finds the home and the family he has never had.")
                .releaseDate(LocalDate.of(2001, 11, 16))
                .duration(152)
                .build();
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));

    }

    @Test
    void shouldNotCreateFilmWhenDurationIsNegative() {
        Film film = Film.builder()
                .id(0)
                .name("Harry Potter and the sorcerer's stone")
                .description("The story of a boy who learns on his 11th birthday that he is the orphaned " +
                        "son of two powerful wizards and possesses unique magical powers of his own.")
                .releaseDate(LocalDate.of(2001, 11, 16))
                .duration(-1)
                .build();
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void shouldUpdateFilm() {
        Film film = Film.builder()
                .id(0)
                .name("Harry Potter")
                .description("The story of a boy who learns on his 11th birthday that he is the orphaned " +
                        "son of two powerful wizards and possesses unique magical powers of his own.")
                .releaseDate(LocalDate.of(2001, 11, 16))
                .duration(152)
                .build();
        assertEquals("Harry Potter", filmController.createFilm(film).getName());
        film.setName("Harry Potter and the sorcerer's stone");
        assertEquals("Harry Potter and the sorcerer's stone", filmController.updateFilm(film).getName());
    }

    @Test
    void shouldNotUpdateFilmWhenFilmIsNotFound() {
        Film film = Film.builder()
                .id(0)
                .name("Harry Potter and the sorcerer's stone")
                .description("The story of a boy who learns on his 11th birthday that he is the orphaned " +
                        "son of two powerful wizards and possesses unique magical powers of his own.")
                .releaseDate(LocalDate.of(2001, 11, 16))
                .duration(152)
                .build();
        assertThrows(NotFoundException.class, () -> filmController.updateFilm(film));
    }

}
