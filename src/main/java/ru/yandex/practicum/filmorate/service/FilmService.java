package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
  private final FilmStorage filmStorage;

//  public Film addLike(Long filmId, long userId) {
//
//  }
//
//  public Film removeLike(Long filmId, Long userId) {
//
//  }
//
//  public List<Film> getTop10() {
//
//  }

}
