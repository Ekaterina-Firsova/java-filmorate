package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

/**
 * Utility class for mapping between {@link User} entities and {@link UserDto}. This class provides
 * static methods to convert between different representations of user data:
 * <ul>
 *   <li> {@link #mapToUser(UserDto)}: maps a {@link UserDto} to a {@link User} entity.</li>
 *   <li>{@link #mapToUserDto(User)}: maps a {@link User} to {@link UserDto}.</li>
 * </ul>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

  public static User mapToUser(final UserDto userDto) {
    return User.builder()
        .login(userDto.getLogin())
        .name(userDto.getName())
        .email(userDto.getEmail())
        .birthday(userDto.getBirthday())
        .id(userDto.getId())
        .build();
  }

  public static UserDto mapToUserDto(final User user) {
    final UserDto userDto = UserDto.builder()
        .id(user.getId())
        .email(user.getEmail())
        .login(user.getLogin())
        .name(user.getName())
        .birthday(user.getBirthday())
        .build();
    user.getFriends().forEach(id -> userDto.getFriends().add(id));
    return userDto;
  }

}
