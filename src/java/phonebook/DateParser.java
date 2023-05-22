package phonebook;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateParser {

  // Formatter for printing and parsing dates.
  static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

  /**
   * Constructs a LocalDate object from given String.
   *
   * @param dateString String to be parsed into a LocalDate Object. Must follow pattern dd.MM.yyyy.
   * @return constructed LocalDate object.
   */
  public static LocalDate getLocalDateFromString(String dateString) {
    return LocalDate.parse(dateString, FORMATTER);
  }

  /**
   * Constructs a String representing a date from given LocalDate object.
   *
   * @param localDate LocalDate object to be formatted into a string.
   * @return resulting date String.
   */
  public static String getStringFromLocalDate(LocalDate localDate) {
    if (localDate == null) {
      return "";
    }
    try {
      return localDate.format(FORMATTER);
    } catch (DateTimeException e) {
      return "";
    }
  }

  /**
   * Checks string date format against specified DateTimeFormatter format.
   *
   * @param date date String to be validated.
   * @return true if date is in correct format, false otherwise.
   */
  public static boolean isStringDateFormatCorrect(String date) {
    try {
      LocalDate.parse(date, FORMATTER);
    } catch (DateTimeException e) {
      return false;
    }
    return true;
  }
}
