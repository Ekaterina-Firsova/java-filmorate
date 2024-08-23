package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {

    @Autowired
    private final Storage<Director> storage;

    public List<DirectorDto> getAll() {
        log.debug("Inside getAll method.");
        return storage.findAll().stream().map(DirectorMapper::mapToDirectorDto).toList();
    }

    public DirectorDto getById(Long id) {
        log.debug("Inside getByID method to get a director with ID = {}.", id);
        return DirectorMapper.mapToDirectorDto(storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Director with ID = " + id + " doesn't exist.")));
    }

    public DirectorDto save(Director director) {
        return DirectorMapper.mapToDirectorDto(storage.save(director));
    }

    public DirectorDto update(Director newDirector) {
        return DirectorMapper.mapToDirectorDto(storage.update(newDirector));
    }

    public void delete(Long id) {
        storage.delete(id);
    }
}
