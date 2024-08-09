package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaRepository mpaRepository;

    public Collection<Mpa> getAll() {
        return mpaRepository.findAll();
    }

    public Mpa getById(int id) {
        return mpaRepository.getById(id).orElseThrow(() -> new NotFoundException("Рейтинг MPA не найден"));
    }
}
