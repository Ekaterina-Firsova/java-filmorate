package ru.yandex.practicum.filmorate.storage.rowmappers;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.experimental.UtilityClass;

/**
 * Utility class to assist with mapping rows from a {@link ResultSet} to Java objects.
 *
 * @see UserRowMapper
 * @see FilmRowMapper
 */
@UtilityClass
public class RowMapperHelper {

  /**
   * Extracts a list of Strings from a specified column in the {@link ResultSet}.
   *
   * @param rs         the {@link ResultSet} data.
   * @param columnName the name of the column to extract data from.
   * @return a list of strings, or an empty list when the column does not exist or contains null
   * values.
   * @throws SQLException if a database access error occurs.
   */
  public List<String> extractListString(ResultSet rs, String columnName) throws SQLException {
    final Object[] arrayObj = getArrayFromResultSet(rs, columnName);
    if (arrayObj == null) {
      return Collections.emptyList();
    }
    return Arrays.stream(arrayObj)
        .filter(Objects::nonNull)
        .map(Object::toString)
        .toList();
  }

  /**
   * Extracts a list of Long from a specified column in the {@link ResultSet}.
   *
   * @param rs         the {@link ResultSet} data.
   * @param columnName the name of the column to extract data from.
   * @return a list of Long, or an empty list when the column does not exist or contains null
   * values.
   * @throws SQLException if a database access error occurs.
   */
  public List<Long> extractListLong(ResultSet rs, String columnName) throws SQLException {
    final Object[] arrayObj = getArrayFromResultSet(rs, columnName);
    if (arrayObj == null) {
      return Collections.emptyList();
    }
    return Arrays.stream(arrayObj)
        .filter(Objects::nonNull)
        .map(m -> Long.parseLong(m.toString()))
        .toList();
  }

  private Object[] getArrayFromResultSet(ResultSet rs, String columnName) throws SQLException {
    if (!hasColumn(rs, columnName)) {
      return null;
    }
    return (Object[]) rs.getArray(columnName).getArray();
  }

  private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
    ResultSetMetaData rsMetaData = rs.getMetaData();
    int columnCount = rsMetaData.getColumnCount();
    for (int i = 1; i <= columnCount; i++) {
      if (columnName.equalsIgnoreCase(rsMetaData.getColumnName(i))) {
        return true;
      }
    }
    return false;
  }
}
