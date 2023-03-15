package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class IsAfterCinemaBirthdayValidator implements ConstraintValidator<IsAfterCinemaBirthday, LocalDate> {
    private static final LocalDate cinemaBirthday = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext constraintValidatorContext) {
        return releaseDate == null || cinemaBirthday.isBefore(releaseDate);
    }
}
