package phonebook;

import java.sql.SQLException;

public class HW6Exception extends SQLException {

  public HW6Exception(String errorMessage) {
    super(errorMessage);
  }
}
