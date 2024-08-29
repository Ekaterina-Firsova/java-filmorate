package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.validator.DateAfter;

/**
 * Data Transfer Object representing a Film.
 *
 * @see Film
 */
@Data
@Builder
public class FilmDto {

    static final String MIN_DATE = "1895-12-28";
    static final int MAX_DESCRIPTION_SIZE = 200;

    private Long id;

    @NotBlank(message = "Name should not be empty.")
    private String name;

    @Size(max = MAX_DESCRIPTION_SIZE, message = "Description should not exceed "
            + MAX_DESCRIPTION_SIZE + " characters.")
    private String description;

    @NotNull(message = "ReleaseDate should not be null.")
    @DateAfter(after = MIN_DATE, message = "Release date should not be before " + MIN_DATE)
    private LocalDate releaseDate;

    @Positive(message = "Duration must be a positive number.")
    @NotNull(message = "Duration should not be null.")
    private Long duration;

    @NotNull(message = "MPA rate should not be null.")
    private MpaRating mpa;

    private final Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));

    private final Set<Long> likes = new HashSet<>();
    private final Set<Director> directors = new HashSet<>();
}
