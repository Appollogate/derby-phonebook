package phonebook;

import java.util.List;

public class Validator {

  /**
   * Check if a contact with current name, surname and phone numbers can exist. A contact can exist
   * if it has a correct name, a correct surname and at least one of two phones (which must also be
   * correct).
   *
   * @param name        contact's name.
   * @param surname     contact's last name.
   * @param mobilePhone contact's mobile phone.
   * @param homePhone   contact's home phone
   * @return true if contact with these parameters can exist, false otherwise.
   */
  public static boolean canContactExist(String name, String surname, String mobilePhone,
      String homePhone) {
    return !name.isBlank() && !surname.isBlank()
        && (!mobilePhone.isBlank() || !homePhone.isBlank());
  }

  /**
   * Checks if a phone number is valid (consists of digits only).
   *
   * @param number string containing a phone number.
   * @return false if the phone is valid, true otherwise.
   */
  public static boolean isPhoneIncorrect(String number) {
    // Phone number is correct if it contains digits only.
    return (!number.isEmpty() && number.isBlank()) || !number.isEmpty()
        && !number.replaceAll("\\s", "").matches("[0-9]+");
  }

  /**
   * Check if an abstract entry is incorrect (consists of white spaces only).
   *
   * @param entry string to check.
   * @return true if entry consists of white spaces only, false otherwise.
   */
  public static boolean isEntryIncorrect(String entry) {
    // Entry is considered incorrect if it is not empty and consists of white spaces only.
    return !entry.isEmpty() && entry.isBlank();
  }

  /**
   * Checks if given contact is valid by checking all necessary fields of it. A contact is valid if
   * it has a name, a surname and at least one of two phones, all of which are valid and if its date
   * of birth is presented in the right format. Moreover, any text field of a contact cannot exceed
   * VARCHAR_MAx_SIZE (255 by default) characters because of database restrictions.
   *
   * @param contact contact to be checked.
   * @return true if the contact is valid, false otherwise.
   */
  public static boolean isContactValid(Contact contact) {
    return canContactExist(contact.getFirstName(), contact.getLastName(),
        contact.getMobilePhone(), contact.getHomePhone()) &&
        !isPhoneIncorrect(contact.getHomePhone()) &&
        !isPhoneIncorrect(contact.getMobilePhone()) &&
        (contact.getDateOfBirth().isBlank() || DateParser
            .isStringDateFormatCorrect(contact.getDateOfBirth())) &&
        !isAnyFieldTooLong(JDBCManager.VARCHAR_MAX_SIZE, List.of(
            contact.getFirstName(), contact.getLastName(),
            contact.getPatronymic(), contact.getHomePhone(),
            contact.getMobilePhone(), contact.getAddress(),
            contact.getComment()
        ));
  }

  /**
   * Checks the length of all given Strings.
   * @param maxLength maximum length of any given String.
   * @param stringList a list of Strings to be checked.
   * @return true if any string is too long, false otherwise.
   */
  public static boolean isAnyFieldTooLong(int maxLength, List<String> stringList){
    return stringList.stream().anyMatch(s -> s.length() > maxLength);
  }
}
