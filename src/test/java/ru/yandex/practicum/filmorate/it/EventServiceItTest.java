package ru.yandex.practicum.filmorate.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.dao.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.rowmappers.EventRowMapper;
import ru.yandex.practicum.filmorate.storage.rowmappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.rowmappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.rowmappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.storage.rowmappers.UserRowMapper;

@JdbcTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmService.class, FilmDbStorage.class, FilmRowMapper.class,
    UserService.class, UserDbStorage.class, UserRowMapper.class,
    EventService.class, EventDbStorage.class, EventRowMapper.class,
    GenreDbStorage.class, GenreRowMapper.class, MpaRatingDbStorage.class, MpaRatingRowMapper.class})
@Transactional
public class EventServiceItTest {

  private static final Long ID_START = 1L;
  private final EventService eventService;
  private final UserService userService;
  private final FilmService filmService;
//  private final ReviewService reviewService;

  private final JdbcTemplate jdbc;

  @BeforeEach
  public void resetSequences() {
//    jdbc.execute("ALTER TABLE review ALTER COLUMN id RESTART WITH " + ID_START);
  }

//  TODO add review

  /**
   * <ol>
   *   <li>GIVEN: <ul>
   *     <li>user(id = 1) - test object,</li>
   *     <li>user(id = 2) - to friend/unfriend,</li>
   *     <li>film(id = 1) - to like/unlike,</li>
   *     <li>review -post,update,delete</li>
   *   </ul></li>
   *
   *   <li>WHEN: <ul>
   *     <li>addFriend(user2)</li>
   *     <li>removeFriend(user2)</li>
   *     <li>addLike(film)</li>
   *     <li>removeLike(film)</li>
   *     <li>postReview()</li>
   *     <li>updateReview()</li>
   *     <li>deleteReview()</li>
   *   </ul></li>
   *
   *   <li>AND: <ul>
   *     <li>findUserEvents(user1)</li>
   *     </ul></li>
   *
   *   <li>THEN: <ul>
   *     <li>List<Event> length = 7</li>
   *     <li>{other assertion}</li>
   *   </ul></li>
   * </ol>
   */
  @Test
  @DisplayName("Create events and retrieve Event Feed for certain user.")
  public void createAndRetrieveUserEventFeed() {
    final Long userId = 1L;
    final Long friendId = 2L;
    final Long filmId = 1L;
//     Review review = TestDataBuilder.buildReview(userId,filmId);

    userService.addFriend(userId, friendId); //0
    userService.removeFriend(userId, friendId); //1
    filmService.addLike(filmId, userId); //2
    filmService.removeLike(filmId, userId); //3
//    review = reviewService.postReview(review); //4
//    reviewService.updateReview(review); //5
//    reviewService.removeReview(review); //6

    final List<EventDto> actualtFeed = eventService.getFeed(userId);

    assertThat(actualtFeed)
        .isNotEmpty()
//        .hasSize(7);
        .hasSize(4);

    assertThat(actualtFeed)
        .allSatisfy(event -> assertThat(event.getTimestamp())
            .isGreaterThan(1670590017281L));

    assertThat(actualtFeed)
        .allSatisfy(event -> assertThat(event.getUserId())
            .isEqualTo(userId));

    assertThat(actualtFeed.get(0))
        .extracting(EventDto::getEventType,
            EventDto::getOperation,
            EventDto::getEntityId)
        .containsExactly(EventType.FRIEND, Operation.ADD, friendId);

    assertThat(actualtFeed.get(1))
        .extracting(EventDto::getEventType,
            EventDto::getOperation,
            EventDto::getEntityId)
        .containsExactly(EventType.FRIEND, Operation.REMOVE, friendId);

    assertThat(actualtFeed.get(2))
        .extracting(EventDto::getEventType,
            EventDto::getOperation,
            EventDto::getEntityId)
        .containsExactly(EventType.LIKE, Operation.ADD, filmId);

    assertThat(actualtFeed.get(3))
        .extracting(EventDto::getEventType,
            EventDto::getOperation,
            EventDto::getEntityId)
        .containsExactly(EventType.LIKE, Operation.REMOVE, filmId);

//    assertThat(actualtFeed.get(4))
//        .extracting(EventDto::getEventType,
//            EventDto::getOperation,
//            EventDto::getEntityId)
//        .containsExactly(EventType.REVIEW, Operation.ADD, review.getId());
//
//    assertThat(actualtFeed.get(5))
//        .extracting(EventDto::getEventType,
//            EventDto::getOperation,
//            EventDto::getEntityId)
//        .containsExactly(EventType.REVIEW, Operation.UPDATE,review.getId());
//
//    assertThat(actualtFeed.get(6))
//        .extracting(EventDto::getEventType,
//            EventDto::getOperation,
//            EventDto::getEntityId)
//        .containsExactly(EventType.REVIEW, Operation.REMOVE,review.getId());
  }

  @Test
  @DisplayName("Retrieve empty Event Feed for certain user.")
  public void retrieveEmptyUserEventFeed() {
    final Long userId = 1L;

    final List<EventDto> actualFeed = eventService.getFeed(userId);

    assertThat(actualFeed)
        .isEmpty();
  }

}
