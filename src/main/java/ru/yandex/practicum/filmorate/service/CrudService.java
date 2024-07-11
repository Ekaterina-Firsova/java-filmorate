package ru.yandex.practicum.filmorate.service;

import java.util.Collection;

public interface CrudService<T> {

  T save(T t);

  T update(T t);

  Collection<T> getAll();

  T getById(Long id);

}
