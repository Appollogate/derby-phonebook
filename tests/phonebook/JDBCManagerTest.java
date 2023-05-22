package phonebook;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

// Ask JUnit to create only one instance of JdbcSampleTest for all tests.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
// Fixate order of test by using @Order annotation.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JDBCManagerTest {

  private static final String DERBY_URL = "jdbc:derby:memory:sampledb;create=true";

  @BeforeAll
  static void setConnectionURL() throws SQLException {
    JDBCManager.setDerbyURL(DERBY_URL);
    // Create table.
    JDBCManager.createTable();
  }

  @AfterAll
  static void tearDown() throws SQLException {
    try (Connection connection = JDBCManager.getConnection();
        Statement statement = connection.createStatement()) {
      // Delete table
      statement.execute("drop table contacts");
    }
    JDBCManager.shutDown();
  }

  @Test
  @Order(1)
  void AddContactTest() throws SQLException {
    Contact contact = new Contact();
    contact.setFirstName("Daria");
    contact.setLastName("Skrypina");
    contact.setMobilePhone("123");
    // Try to add a new contact to database.
    JDBCManager.addContactToDB(contact);
    // Retrieve it and check data.
    List<Contact> contacts = JDBCManager.getAllContactsFromDB();
    assertEquals(1, contacts.size());
    assertEquals("Daria", contacts.get(0).getFirstName());
    assertEquals("Skrypina", contacts.get(0).getLastName());
    assertEquals("123", contacts.get(0).getMobilePhone());
  }

  @Test
  @Order(2)
  void deleteContactTest() throws SQLException {
    // Retrieve the single contact from database.
    List<Contact> contacts = JDBCManager.getAllContactsFromDB();
    String id = contacts.get(0).getId();
    // Try to delete that one contact from database by its id.
    JDBCManager.deleteContactFromDBByID(id);
    // Check that now there are no contacts in database.
    contacts = JDBCManager.getAllContactsFromDB();
    assertEquals(0, contacts.size());
  }

  @Test
  @Order(3)
  void editContactTest() throws SQLException {
    Contact contact = new Contact();
    contact.setFirstName("A");
    contact.setLastName("B");
    contact.setMobilePhone("12345");
    JDBCManager.addContactToDB(contact);
    // Retrieve contact with id
    contact = JDBCManager.getAllContactsFromDB().get(0);
    // Update data in contact
    contact.setFirstName("Peter");
    contact.setLastName("Parker");
    // Edit contact in database
    JDBCManager.editExistingContactByID(contact);
    // Retrieve contact again
    Contact updatedContact = JDBCManager.getAllContactsFromDB().get(0);
    // Check data
    assertEquals("Peter", updatedContact.getFirstName());
    assertEquals("Parker", updatedContact.getLastName());
  }

  @Test
  @Order(4)
  void doesContactExistTest() throws SQLException {
    assertTrue(JDBCManager.doesContactExistInDB("Peter", "Parker", ""));
  }

  @Test
  @Order(5)
  void searchContactsTest() throws SQLException {
    Contact contact1 = new Contact("", "Peter", "Jackson", "", "123", "", "", "", "");
    Contact contact2 = new Contact("", "Peter", "Pan", "", "111", "", "", "", "");
    Contact contact3 = new Contact("", "Gwen", "Stacy", "", "321", "", "", "", "");
    JDBCManager.addContactToDB(contact1);
    JDBCManager.addContactToDB(contact2);
    JDBCManager.addContactToDB(contact3);
    List<Contact> matchingContacts = JDBCManager.getAllContactsMatchingSearchQuery("Peter");
    assertEquals(3, matchingContacts.size());
  }

  @Test
  @Order(6)
  void exportImportTest() throws SQLException, IOException {
    List<Contact> allContacts = JDBCManager.getAllContactsFromDB();
    ContactDataManager.writeContactsToCSV(allContacts, "test.csv");
    List<Contact> contactsFromCSV = ContactDataManager.readContactsFromCSV("test.csv");
    assertAll(
        () -> assertEquals(allContacts.get(0).getLastName(), contactsFromCSV.get(0).getLastName()),
        () -> assertEquals(allContacts.get(1).getLastName(), contactsFromCSV.get(1).getLastName()),
        () -> assertEquals(allContacts.get(2).getLastName(), contactsFromCSV.get(2).getLastName()),
        () -> assertEquals(allContacts.get(3).getLastName(), contactsFromCSV.get(3).getLastName())
    );
  }
}