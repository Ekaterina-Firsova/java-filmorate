package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService service;

    @GetMapping
    public List<DirectorDto> getAllDirectors() {
        log.info("Received GET /directors");
        return service.getAll();
    }

    @GetMapping("/{id}")
    public DirectorDto getDirectorsById(@PathVariable("id") @NotNull @Positive final Long id) {
        log.info("Received GET /directors/{}", id);
        return service.getById(id);
    }

    @PostMapping
    public DirectorDto save(@Validated @RequestBody final Director director) {
        log.info("Received POST /directors - {}", director);
        return service.save(director);
    }

    @PutMapping
    public DirectorDto update(@Validated @RequestBody final Director director) {
        log.info("Received PUT /directors - {}", director);
        return service.update(director);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") @NotNull @Positive final Long id) {
        log.info("Received DELETE /directors/{}", id);
        service.delete(id);
    }
}
