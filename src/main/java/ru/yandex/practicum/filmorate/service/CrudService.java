package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import ru.yandex.practicum.filmorate.model.User;

public interface CrudService<T> {

  T save (T t);

  T update(T t);

  Collection<T> getAll();

//  T getByID(Long id);



}
