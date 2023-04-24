package ru.yandex.practicum.filmorate.controllersTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.controllers.GenreController;
import ru.yandex.practicum.filmorate.controllers.MpaController;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmControllerTest {
    private final FilmController filmController;
    private final UserController userController;
    private final GenreController genreController;
    private final MpaController mpaController;

    Film film = Film.builder()
            .name("Harry Potter and the sorcerer's stone")
            .description("The story of a boy who learns on his 11th birthday that he is the orphaned " +
                    "son of two powerful wizards and possesses unique magical powers of his own.")
            .releaseDate(LocalDate.of(2001, 11, 16))
            .duration(152)
            .mpa(new Mpa(2, "PG"))
            .genres(new ArrayList<>(List.of(new Genre(1, "Комедия"))))
            .build();

    User user = User.builder()
            .email("columbus1958@gmail.com")
            .login("columbus1958")
            .name("Christopher Joseph Columbus")
            .birthday(LocalDate.of(1958, 9, 10))
            .build();

    @Test
    public void shouldCreateFilm() {
        assertThat(filmController.createFilm(film).getId())
                .isEqualTo(1);
    }

    @Test
    public void shouldAssignCorrectIdWhenCreateNewFilm() {
        Film film1 = Film.builder()
                .id(33)
                .name("Harry Potter and the sorcerer's stone")
                .description("The story of a boy who learns on his 11th birthday that he is the orphaned " +
                        "son of two powerful wizards and possesses unique magical powers of his own.")
                .releaseDate(LocalDate.of(2001, 11, 16))
                .duration(152)
                .mpa(new Mpa(2, "PG"))
                .genres(new ArrayList<>(List.of(new Genre(1, "Комедия"))))
                .build();
        assertThat(filmController.createFilm(film1).getId())
                .isEqualTo(1);
    }

    @Test
    public void shouldCreateFilmWhenReleaseDateOnBirthOfCinema() {
        Film film1 = Film.builder()
                .name("Harry Potter and the sorcerer's stone")
                .description("The story of a boy who learns on his 11th birthday that he is the orphaned " +
                        "son of two powerful wizards and possesses unique magical powers of his own.")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(152)
                .mpa(new Mpa(2, "PG"))
                .genres(new ArrayList<>(List.of(new Genre(1, "Комедия"))))
                .build();
        assertThat(filmController.createFilm(film1).getReleaseDate())
                .isEqualTo(LocalDate.of(1895, 12, 28));
    }

    @Test
    public void shouldUpdateFilm() {
        assertThat(filmController.createFilm(film).getName())
                .isEqualTo("Harry Potter and the sorcerer's stone");
        film.setName("Harry Potter");
        assertThat(filmController.updateFilm(film).getName())
                .isEqualTo("Harry Potter");
    }

    @Test
    public void shouldNotUpdateFilmWhenFilmIsNotFound() {
        assertThrows(NotFoundException.class, () -> filmController.updateFilm(film));
    }

    @Test
    public void shouldGetAllFilms() {
        Film film2 = Film.builder()
                .name("Harry Potter and the sorcerer's stone")
                .description("The story of a boy who learns on his 11th birthday that he is the orphaned " +
                        "son of two powerful wizards and possesses unique magical powers of his own.")
                .releaseDate(LocalDate.of(2001, 11, 16))
                .duration(152)
                .mpa(new Mpa(2, "PG"))
                .genres(new ArrayList<>(List.of(new Genre(1, "Комедия"))))
                .build();
        filmController.createFilm(film);
        filmController.createFilm(film2);

        assertThat(filmController.getAllFilms().size())
                .isEqualTo(2);
    }

    @Test
    public void shouldGetFilmById() {
        assertThat(filmController.createFilm(film).getId())
                .isEqualTo(1);
    }

    @Test
    public void shouldDeleteFilm() {
        assertThat(filmController.createFilm(film).getId())
                .isEqualTo(1);

        filmController.deleteFilmById(film.getId());

        assertThat(filmController.getAllFilms().size())
                .isEqualTo(0);
    }

    @Test
    public void shouldGetAllFilmsGenre() {
        assertEquals(1, filmController.createFilm(film).getId());
        Film film2 = Film.builder()
                .name("Harry Potter 2")
                .description("The story of a boy")
                .releaseDate(LocalDate.of(2002, 11, 16))
                .duration(158)
                .mpa(new Mpa(3, "PG-13"))
                .genres(new ArrayList<>(List.of(new Genre(4, "Триллер"))))
                .build();
        assertEquals(2, filmController.createFilm(film2).getId());
        assertEquals(1, filmController.getFilmById(1).getGenres().size());
        assertEquals(1, filmController.getFilmById(2).getGenres().size());
    }

    @Test
    public void shouldAddLikeToFilm() { //DEBUG
        filmController.createFilm(film);
        userController.createUser(user);
        filmController.addLike(1, 1);
        assertThat(filmController.getPopularFilms(filmController.getAllFilms().size()).size())
                .isEqualTo(1);
    }

    @Test
    public void shouldReturnFilmGenres() {
        List<Genre> genres = genreController.getAll();

        assertThat(genres.size())
                .isEqualTo(6);

        assertThat(genres.get(0))
                .extracting(Genre::getId, Genre::getName)
                .containsExactly(1, "Комедия");
    }

    @Test
    public void shouldGetGenreById() {
        filmController.createFilm(film);
        Genre genre = genreController.getById(1);
        assertThat(genre.getName()).isEqualTo("Комедия");
    }

    @Test
    public void shouldGetRatings() {
        List<Mpa> mpa = mpaController.getAll();

        assertThat(mpa.size())
                .isEqualTo(5);

        assertThat(mpa.get(0))
                .extracting(Mpa::getId, Mpa::getName)
                .containsExactly(1, "G");
    }

    @Test
    public void shouldGetRatingById() {
        filmController.createFilm(film);
        Mpa mpa = mpaController.getById(1);
        assertThat(mpa.getName()).isEqualTo("G");
    }
}
