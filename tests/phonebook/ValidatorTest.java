package phonebook;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ValidatorTest {
  @Test
  void canContactExistTest() {
    assertAll(
        () -> assertFalse(Validator.canContactExist("  ", "B", "1", "2")),
        () -> assertFalse(Validator.canContactExist("A", "   ", "1", "2")),
        () -> assertFalse(Validator.canContactExist("A", "B", "  ", "  ")),
        () -> assertTrue(Validator.canContactExist("A", "B", "1", "  ")),
        () -> assertTrue(Validator.canContactExist("A", "B", "  ", "2"))
    );
  }

  @Test
  void isPhoneIncorrect() {
    assertAll(
        () -> assertTrue(Validator.isPhoneIncorrect("test")),
        () -> assertTrue(Validator.isPhoneIncorrect("   ")),
        () -> assertFalse(Validator.isPhoneIncorrect("123"))
    );
  }

  @Test
  void isEntryIncorrect() {
    assertAll(
        () -> assertTrue(Validator.isEntryIncorrect("    ")),
        () -> assertFalse(Validator.isEntryIncorrect("test")),
        () -> assertFalse(Validator.isEntryIncorrect(""))
    );
  }

  @Test
  void isContactValid() {
    Contact emptyContact = new Contact();
    Contact noFirstNameContact = new Contact("", "", "B", "C", "1", "2", "address", "12.03.2003", "c");
    Contact noLastNameContact = new Contact("", "A", "", "C", "1", "2", "address", "12.03.2003", "c");
    Contact noPhonesContact = new Contact("", "A", "B", "C", "   ", "  ", "address", "12.03.2003", "c");
    Contact incorrectPhonesContact = new Contact("", "A", "B", "C", "hi", "hello", "address",
        "12.03.2003", "c");
    Contact blankPhoneContact = new Contact("", "A", "B", "C", "123", "  ", "", "", "");
    Contact wrongDateContact = new Contact("", "A", "B", "C", "12", "34", "", "today", "");
    Contact correctContact1 = new Contact("", "A", "B", "", "1", "", "", "", "");
    Contact correctContact2 = new Contact("", "A", "B", "C", "1", "2", "Moscow", "01.01.1970", "text");
    assertAll(
        () -> assertFalse(Validator.isContactValid(emptyContact)),
        () -> assertFalse(Validator.isContactValid(noFirstNameContact)),
        () -> assertFalse(Validator.isContactValid(noLastNameContact)),
        () -> assertFalse(Validator.isContactValid(noPhonesContact)),
        () -> assertFalse(Validator.isContactValid(incorrectPhonesContact)),
        () -> assertFalse(Validator.isContactValid(blankPhoneContact)),
        () -> assertFalse(Validator.isContactValid(wrongDateContact)),
        () -> assertTrue(Validator.isContactValid(correctContact1)),
        () -> assertTrue(Validator.isContactValid(correctContact2))
    );

  }
}