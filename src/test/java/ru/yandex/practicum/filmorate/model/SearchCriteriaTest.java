package ru.yandex.practicum.filmorate.model;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SearchCriteriaTest {

  @ParameterizedTest(name = "{0}")
  @DisplayName("Testing valid criteria conversion")
  @MethodSource("provideValidCriteriaStrings")
  public void testFromStringValidValues(final SearchCriteria searchCriteriaExpected,
      String toConvert) {
    Assertions.assertEquals(searchCriteriaExpected, SearchCriteria.fromString(toConvert));
  }

  @Test
  @DisplayName("Testing invalid criteria conversion")
  public void testFromStringInvalidValue() {
    final IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class,
        () -> SearchCriteria.fromString("INVALID"));
    Assertions.assertEquals("Invalid search criteria: INVALID", thrown.getMessage());
  }

  @Test
  @DisplayName("Validate tableColumn property is correctly set")
  public void testTableColumnProperty() {
    Assertions.assertEquals("f.NAME", SearchCriteria.TITLE.getTableColumn());
    Assertions.assertEquals("d.NAME", SearchCriteria.DIRECTOR.getTableColumn());
  }

  private static Stream<Arguments> provideValidCriteriaStrings() {
    return Stream.of(
        Arguments.of(SearchCriteria.DIRECTOR, "DIRECTOR"),
        Arguments.of(SearchCriteria.TITLE, "title"),
        Arguments.of(SearchCriteria.TITLE, "TitLE")
    );
  }
}
