package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.IsAfterCinemaBirthday;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class Film {
    private int id;
    @NotEmpty(message = "Name cannot be null or empty")
    private String name;
    @Size(max = 200, message = "Description must be max 200 characters")
    private String description;
    @IsAfterCinemaBirthday
    private LocalDate releaseDate;
    @Positive(message = "Duration of the film must be positive")
    private int duration;
    private Set<Integer> likesList;

    private List<Genre> genres;

    private Mpa mpa;
}
