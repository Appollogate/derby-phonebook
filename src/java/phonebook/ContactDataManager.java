package phonebook;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class ContactDataManager {

  /**
   * Reads contacts from a file with a given path.
   *
   * @param filepath path to the file.
   * @return list of deserialized contacts.
   * @throws IOException              if an error occurs while working with files.
   * @throws IllegalArgumentException if the csv file has wrong format for parsing contacts (not
   *                                  enough / too many delimiters).
   */
  public static List<Contact> readContactsFromCSV(String filepath)
      throws IOException, IllegalArgumentException {
    List<Contact> contacts = new ArrayList<>();
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(new FileInputStream(filepath)));
    String currentLine;
    while ((currentLine = reader.readLine()) != null && !currentLine.isBlank()) {
      contacts.add(getContactFromString(currentLine));
    }
    reader.close();
    return contacts;
  }

  /**
   * Writes a list of contacts to a csv file with a given path.
   *
   * @param contacts list of contacts to serialize.
   * @param path     path to the file.
   * @throws IOException if an error occurs while working with files.
   */
  public static void writeContactsToCSV(List<Contact> contacts, String path) throws IOException {
    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));
    for (Contact contact : contacts) {
      String line = contact.getLastName()
          + CSV_SEPARATOR
          + contact.getFirstName()
          + CSV_SEPARATOR
          + contact.getPatronymic()
          + CSV_SEPARATOR
          + contact.getMobilePhone()
          + CSV_SEPARATOR
          + contact.getHomePhone()
          + CSV_SEPARATOR
          + contact.getAddress()
          + CSV_SEPARATOR
          + contact.getDateOfBirth()
          + CSV_SEPARATOR
          + contact.getComment();
      bw.write(line);
      bw.newLine();
    }
    bw.flush();
    bw.close();
  }

  /**
   * @param str one-line String that contains all parameters of a single Contact instance separated
   *            by the CSV_SEPARATOR.
   * @return constructed Contact from string.
   * @throws IllegalArgumentException if the CSV file has incorrect format for parsing Contacts.
   */
  public static Contact getContactFromString(String str) throws IllegalArgumentException {
    String[] contactDetails = str.split(CSV_SEPARATOR, -1);
    if (contactDetails.length != 8) {
      throw new IllegalArgumentException(
          "Неправильный формат CSV-файла для извлечения контактов.");
    }
    return new Contact("", contactDetails[1].trim(), contactDetails[0].trim(),
        contactDetails[2].trim(), contactDetails[3].trim(), contactDetails[4].trim(),
        contactDetails[5].trim(), contactDetails[6].trim(), contactDetails[7].trim());
  }

  private static final String CSV_SEPARATOR = "\t";
}

