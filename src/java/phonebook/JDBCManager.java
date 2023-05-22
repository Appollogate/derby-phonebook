package phonebook;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class JDBCManager {

  /**
   * Creates a table in the database according to the task description.
   * @throws SQLException if an error occurs while working wih the database.
   */
  public static void createTable() throws SQLException {
    try (Connection connection = getConnection();
        Statement statement = connection.createStatement()) {
      // Create table from task description.
      statement.execute("CREATE TABLE contacts (id bigint "
          + "constraint contact_pk primary key generated always as identity,"
          + "surname varchar(255),"
          + "name varchar(255),"
          + "patronymic varchar(255),"
          + "mobile_phone varchar(255),"
          + "home_phone varchar(255),"
          + "address varchar(255),"
          + "dateOfBirth varchar(255),"
          + "comment varchar(255),"
          + "constraint contact_is_unique UNIQUE (surname, name, patronymic))");
    } catch (SQLException se) {
      if (se.getSQLState().equals("X0Y32")) {
        // Exception is thrown because table already exists, it's okay.
        return;
      }
      throw se;
    }
  }

  /**
   * Shuts down the database.
   * @throws SQLException if an error occurs while working wih the database.
   */
  public static void shutDown() throws SQLException {
    // Shut down database
    try (Connection ignored = DriverManager.getConnection("jdbc:derby:;shutdown=true")) {
    } catch (SQLException e) {
      if (!(e.getErrorCode() == 50000 && "XJ015".equals(e.getSQLState()))) {
        throw new SQLException("Unable to shut down database", e);
      }
    }
  }

  /**
   * Adds specified contact to the database.
   * @param contact contact instance to be added.
   * @throws SQLException if an error occurs while working wih the database.
   */
  public static void addContactToDB(Contact contact)
      throws SQLException {
    try (Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement("insert into contacts "
            + "(surname, name, patronymic, mobile_phone, home_phone, address, dateOfBirth, comment) "
            + "values (?, ?, ?, ?, ?, ?, ?, ?)")) {
      ps.setString(1, contact.getLastName());
      ps.setString(2, contact.getFirstName());
      ps.setString(3, contact.getPatronymic());
      ps.setString(4, contact.getMobilePhone());
      ps.setString(5, contact.getHomePhone());
      ps.setString(6, contact.getAddress());
      ps.setString(7, contact.getDateOfBirth());
      ps.setString(8, contact.getComment());
      ps.executeUpdate();
    }
  }

  /**
   * Retrieves all entries from the database and converts them to a collection of Contact instances.
   * @return a list of all contacts from the database.
   * @throws SQLException if an error occurs while working wih the database.
   */
  public static ObservableList<Contact> getAllContactsFromDB() throws SQLException {
    try (Connection con = getConnection();
        Statement statement = con.createStatement()) {
      ResultSet rs = statement.executeQuery("select * from contacts");
      return getContactsFromResultSet(rs);
    }
  }

  /**
   * Retrieves all contacts from database that match given search query.
   * @param searchQuery query for contacts to be filtered by.
   * @return a list of contacts from the database that match the search pattern.
   * @throws SQLException if an error occurs while working wih the database.
   */
  public static ObservableList<Contact> getAllContactsMatchingSearchQuery(String searchQuery)
      throws SQLException {
    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(
            "select * from contacts where name like ? or surname like ? or patronymic like ?")) {
      statement.setString(1, "%" + searchQuery + "%");
      statement.setString(2, "%" + searchQuery + "%");
      statement.setString(3, "%" + searchQuery + "%");
      ResultSet resultSet = statement.executeQuery();
      return getContactsFromResultSet(resultSet);
    }
  }

  /**
   * Checks if a contact with the given full name already exists in the database.
   * @param name name of the contact to be searched for.
   * @param surname surname of the contact to be searched for.
   * @param patronymic patronymic of the contact to be searched for.
   * @return true if a contact with the given full name already exists in the database, false otherwise.
   * @throws SQLException if an error occurs while working wih the database.
   */
  public static boolean doesContactExistInDB(String name, String surname, String patronymic)
      throws SQLException {
    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(
            "select * from contacts where name = ? and surname = ? and patronymic = ?")) {
      statement.setString(1, name);
      statement.setString(2, surname);
      statement.setString(3, patronymic);
      ResultSet rs = statement.executeQuery();
      return rs.next();
    }
  }

  /**
   * Searches for a contact in the database based on its ID and updates its
   * data to match the fields of given Contact instance.
   * @param editedContact instance of a Contact with edited data. Its ID is used to find
   *                      an older version of the contact in the database and update it.
   * @throws SQLException if an error occurs while working wih the database.
   */
  public static void editExistingContactByID(Contact editedContact) throws SQLException {
    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement
            ("update contacts set name = ?, surname = ?, patronymic = ?, "
                + "mobile_phone = ?, home_phone = ?, address = ?, "
                + "dateOfBirth = ?, comment = ? where id = ?")) {
      statement.setString(1, editedContact.getFirstName());
      statement.setString(2, editedContact.getLastName());
      statement.setString(3, editedContact.getPatronymic());
      statement.setString(4, editedContact.getMobilePhone());
      statement.setString(5, editedContact.getHomePhone());
      statement.setString(6, editedContact.getAddress());
      statement.setString(7, editedContact.getDateOfBirth());
      statement.setString(8, editedContact.getComment());
      statement.setString(9, editedContact.getId());
      int rowsAffected = statement.executeUpdate();
      if (rowsAffected != 1) {
        throw new HW6Exception(
            "Encountered N <> 1 contacts with same id while processing Edit operation.");
      }
    }
  }

  /**
   * Searches for a contact with given ID in the database and deletes it.
   * @param id id of the contact to be deleted.
   * @throws SQLException if an error occurs while working wih the database.
   */
  public static void deleteContactFromDBByID(String id) throws SQLException {
    try (Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement
            ("delete from contacts where id = ?")) {
      statement.setString(1, id);
      int rowsAffected = statement.executeUpdate();
      if (rowsAffected != 1) {
        throw new HW6Exception(
            "Encountered N <> 1 contacts with same id while processing Delete operation.");
      }
    }
  }

  /**
   * Transforms a ResultSet received from a DB statement to a list of corresponding Contact instances.
   * @param rs ResultSet of rows depicting contacts.
   * @return a list of contacts formed from row data.
   * @throws SQLException if an error occurs while working wih the database.
   */
  private static ObservableList<Contact> getContactsFromResultSet(ResultSet rs)
      throws SQLException {
    ObservableList<Contact> result = FXCollections.observableArrayList();
    Contact contact;
    if (!rs.isClosed()) {
      while (rs.next()) {
        contact = new Contact(rs.getString(1), rs.getString(3), rs.getString(2), rs.getString(4),
            rs.getString(5), rs.getString(6), rs.getString(7),
            rs.getString(8), rs.getString(9));
        result.add(contact);
      }
    }
    return result;
  }

  /**
   * Establishes a connection to specified database by its URL and returns it.
   * @return a connection to the database specified by its URL.
   * @throws SQLException if an error occurs while working wih the database.
   */
  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(derbyURL);
  }

  /**
   * Sets the URL for creating a specified database or connecting to it if it already exists.
   * @param url Apache Derby URL used for connecting to an embedded database.
   */
  public static void setDerbyURL(String url){
    derbyURL = url;
  }

  // Max length of String (VARCHAR) values in the database.
  public static final int VARCHAR_MAX_SIZE = 255;
  // URL used for connecting to a database.
  private static String derbyURL = "jdbc:derby:contacts_db;create=true";
}
