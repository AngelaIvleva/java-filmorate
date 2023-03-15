package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private long id;
    @NotBlank
    @Email(message = "invalid email")
    private String email;
    @NotBlank(message = "Login cannot be wrong or empty")
    private String login;
    private String name;
    @Past(message = "Date of birth cannot be in the future")
    private LocalDate birthday;
    private Set<Long> friendsList;

    public Set<Long> getFriendsList() {
        if (friendsList == null) {
            return new HashSet<>();
        }
        return friendsList;
    }
}
