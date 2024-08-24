package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
public class ReviewDbStorage extends BaseRepository<Review> implements ReviewStorage {

    /**
     * Особенность CHANGE_USEFUL_MARK - если при добавлении лайка граничное значение равно 0,
     * то вычитаем/добавляем значение 2, чтоб перескочить 0, в противном случае вычитаем/добавляем 1
     * */

    private static final String ADD_LIKE = "MERGE INTO review_likes(review_id, user_id, is_useful) VALUES(?, ?, ?)";
    private static final String REMOVE_LIKE = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
    private static final String CHANGE_USEFUL_MARK = """
            UPDATE reviews
            SET useful = CASE
                            WHEN useful + %d = 0 THEN useful + %d
                            ELSE useful + %d
                         END
            WHERE review_id = ?
            """;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Review> mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public Review save(Review review) {
        log.info("Отзыв для сохранения в БД: {}", review);
        String query = "INSERT INTO reviews(content, is_positive, user_id, film_id) VALUES(?, ?, ?, ?)";
        Long id = insert(
                query,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId());
        review.setReviewId(id);
        log.info("Сохраненный отзыв: {}", review);
        return review;
    }

    @Override
    public Review update(Review review) {
        String query = "UPDATE reviews SET content = ?, is_positive = ?, useful = ? WHERE review_id = ?";
        update(
                query,
                review.getContent(),
                review.getIsPositive(),
                review.getUseful(),
                review.getReviewId()
        );
        removeLikes(review.getReviewId());
        return review;
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM reviews WHERE review_id = ?";
        delete(query, id);
    }

    @Override
    public Optional<Review> findById(Long id) {
        log.info("Поиск отзыва с id {}", id);
        String query = "SELECT * FROM reviews WHERE review_id = ?";
        return findOne(query, id);
    }

    @Override
    public Collection<Review> findAll() {
        log.info("Поиск всех существующих отзывов");
        String query = "SELECT * FROM reviews";
        return findMany(query);
    }

    @Override
    public Collection<Review> findAllByFilmId(Long filmId, Integer count) {
        log.info("Поиск отзывов по фильму: {} в количестве: {}", filmId, count);
        String queryByFilmId = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        String queryAll = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ? ";
        return filmId == null ? findMany(queryAll, count) : findMany(queryByFilmId, filmId, count);
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        insertCompositePk(ADD_LIKE, reviewId, userId, Boolean.TRUE);
        update(String.format(CHANGE_USEFUL_MARK, 1, 2, 1), reviewId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        insertCompositePk(ADD_LIKE, reviewId, userId, Boolean.FALSE);
        update(String.format(CHANGE_USEFUL_MARK, -1, -2, -1), reviewId);
    }

    @Override
    public void removeLikes(Long reviewId) {
        String query = "DELETE FROM review_likes WHERE review_id = ?";
        delete(query, reviewId);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        delete(REMOVE_LIKE, reviewId, userId);
        update(String.format(CHANGE_USEFUL_MARK, -1, -1, -1), reviewId);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        delete(REMOVE_LIKE, reviewId, userId);
        update(String.format(CHANGE_USEFUL_MARK, 1, 1, 1), reviewId);
    }

    @Override
    public boolean isExist(Long id) {
        String query = "SELECT EXISTS(SELECT 1 FROM reviews WHERE review_id = ?)";
        return checkExistence(query, id);
    }

    @Override
    public boolean isLikeAndUsefulExist(Long reviewId, Long userId, Boolean isUseful) {
        String query = """
                SELECT EXISTS(SELECT 1 FROM review_likes
                WHERE review_id = ?
                AND user_id = ?
                AND is_useful = ?)
                """;
        return checkExistence(query, reviewId, userId, isUseful);
    }

    @Override
    public boolean isLikeExist(Long reviewId, Long userId) {
        String query = """
                SELECT EXISTS(SELECT 1 FROM review_likes
                WHERE review_id = ?
                AND user_id = ?)
                """;
        return checkExistence(query, reviewId, userId);
    }
}
