package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private long id;
    @NotEmpty(message = "Name cannot be null or empty")
    private String name;
    @Size(max = 200, message = "Description must be max 200 characters")
    private String description;
    @PastOrPresent
    private LocalDate releaseDate;
    @Positive(message = "Duration of the film must be positive")
    private int duration;
    private Set<Long> likesList;

    public Set<Long> getLikesList() {
        if (likesList == null) {
            return new HashSet<>();
        }
        return likesList;
    }
}
