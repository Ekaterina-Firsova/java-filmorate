package ru.yandex.practicum.filmorate.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.builder.TestDataBuilder;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewRequest;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.dao.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.rowmappers.EventRowMapper;
import ru.yandex.practicum.filmorate.storage.rowmappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.rowmappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.rowmappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.storage.rowmappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.storage.rowmappers.UserRowMapper;

@JdbcTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmService.class, FilmDbStorage.class, FilmRowMapper.class,
    UserService.class, UserDbStorage.class, UserRowMapper.class,
    EventService.class, EventDbStorage.class, EventRowMapper.class,
    GenreDbStorage.class, GenreRowMapper.class, MpaRatingDbStorage.class, MpaRatingRowMapper.class,
    ReviewService.class, ReviewDbStorage.class, ReviewRowMapper.class})
@Transactional
public class EventServiceItTest {

  private static final Long ID_START = 1L;
  private final EventService eventService;
  private final UserService userService;
  private final FilmService filmService;
  private final ReviewService reviewService;

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
   *   <li>AND:<ul>
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
    ReviewRequest review = TestDataBuilder.buildReview(userId,filmId);
    ReviewRequest reviewToUpdate = TestDataBuilder.buildUpdateReview(userId,filmId);

    userService.addFriend(userId, friendId); //0
    userService.removeFriend(userId, friendId); //1
    filmService.addLike(filmId, userId); //2
    filmService.removeLike(filmId, userId); //3
    final Long reviewId = reviewService.saveReview(review).getReviewId(); //4
    reviewToUpdate.setReviewId(reviewId);
    ReviewDto reviewUpdated = reviewService.updateReview(reviewToUpdate); //5
    reviewService.removeReview(reviewId); //6

    final List<EventDto> actualFeed = eventService.getFeed(userId);

    assertThat(actualFeed)
        .isNotEmpty()
        .hasSize(7);


    assertThat(actualFeed)
        .allSatisfy(event -> assertThat(event.getTimestamp())
            .isGreaterThan(1670590017281L));

    assertThat(actualFeed)
        .allSatisfy(event -> assertThat(event.getUserId())
            .isEqualTo(userId));

    assertThat(actualFeed.get(0))
        .extracting(EventDto::getEventType,
            EventDto::getOperation,
            EventDto::getEntityId)
        .containsExactly(EventType.FRIEND, Operation.ADD, friendId);

    assertThat(actualFeed.get(1))
        .extracting(EventDto::getEventType,
            EventDto::getOperation,
            EventDto::getEntityId)
        .containsExactly(EventType.FRIEND, Operation.REMOVE, friendId);

    assertThat(actualFeed.get(2))
        .extracting(EventDto::getEventType,
            EventDto::getOperation,
            EventDto::getEntityId)
        .containsExactly(EventType.LIKE, Operation.ADD, filmId);

    assertThat(actualFeed.get(3))
        .extracting(EventDto::getEventType,
            EventDto::getOperation,
            EventDto::getEntityId)
        .containsExactly(EventType.LIKE, Operation.REMOVE, filmId);

    assertThat(actualFeed.get(4))
        .extracting(EventDto::getEventType,
            EventDto::getOperation,
            EventDto::getEntityId)
        .containsExactly(EventType.REVIEW, Operation.ADD, reviewId);

    assertThat(actualFeed.get(5))
        .extracting(EventDto::getEventType,
            EventDto::getOperation,
            EventDto::getEntityId)
        .containsExactly(EventType.REVIEW, Operation.UPDATE,reviewId);

    assertThat(actualFeed.get(6))
        .extracting(EventDto::getEventType,
            EventDto::getOperation,
            EventDto::getEntityId)
        .containsExactly(EventType.REVIEW, Operation.REMOVE,reviewId);
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
