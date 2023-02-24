package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder

// dont work anno, delete set&getters for id?

public class Film {
    private int id;
    @NotBlank(message = "Name cannot be null or empty")
    private String name;
    @Size(max = 200, message = "Description must be max 200 characters")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Duration of the film must be positive")
    private int duration;
}
