package phonebook;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

class DateParserTest {

  @Test
  void getLocalDateFromStringTest() {
    LocalDate date = DateParser.getLocalDateFromString("01.01.1970");
    LocalDate date2 = LocalDate.of(1970, 1, 1);
    assertEquals(date, date2);
  }

  @Test
  void isStringDateFormatCorrectTest() {
    assertAll(
        () -> assertFalse(DateParser.isStringDateFormatCorrect("")),
        () -> assertFalse(DateParser.isStringDateFormatCorrect("01-01-1970")),
        () -> assertFalse(DateParser.isStringDateFormatCorrect("01/01/1970")),
        () -> assertFalse(DateParser.isStringDateFormatCorrect("1970-01-01")),
        () -> assertFalse(DateParser.isStringDateFormatCorrect("1970/01/01")),
        () -> assertFalse(DateParser.isStringDateFormatCorrect("1970.01.01")),
        () -> assertFalse(DateParser.isStringDateFormatCorrect("11-26-1970")),
        () -> assertFalse(DateParser.isStringDateFormatCorrect("11/26/1970")),
        () -> assertFalse(DateParser.isStringDateFormatCorrect("11.26.1970")),
        () -> assertTrue(DateParser.isStringDateFormatCorrect("26.11.1970"))
    );
    assertFalse(DateParser.isStringDateFormatCorrect(""));
  }

  @Test
  void getStringFromLocalDateTest() {
    assertEquals("", DateParser.getStringFromLocalDate(null));
    LocalDate localDate = LocalDate.parse("24.04.2001", DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    assertEquals("24.04.2001", DateParser.getStringFromLocalDate(localDate));

  }
}