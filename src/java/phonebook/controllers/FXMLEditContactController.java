package phonebook.controllers;

import phonebook.Contact;
import phonebook.DateParser;
import phonebook.ErrorAlertManager;
import phonebook.JDBCManager;
import phonebook.Validator;
import java.sql.SQLException;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLEditContactController {

  private Contact contact;
  private Stage stage;
  private FXMLPhoneBookController controller1;
  private boolean isContactNew;

  @FXML
  private TextField nameField;
  @FXML
  private TextField surnameField;
  @FXML
  private TextField patronymicField;
  @FXML
  private TextField mobilePhoneField;
  @FXML
  private TextField homePhoneField;
  @FXML
  private TextField addressField;
  @FXML
  private DatePicker birthdayDatePicker;
  @FXML
  private TextArea commentArea;
  @FXML
  private Button buttonSave;
  @FXML
  private Button buttonClearDate;

  @FXML
  public void initialize() {
    // Contact must be correct (see task description). Make a binding to ensure that condition is met.
    BooleanBinding bb1 = wrongContactCriteria();
    // Disable 'Save' button if any contact data is incorrect.
    buttonSave.disableProperty().bind(bb1);
    // Enable 'Clear date' button only if dateField is not empty
    buttonClearDate.disableProperty()
        .bind(Bindings.isEmpty(birthdayDatePicker.getEditor().textProperty()));
  }

  void setContact(Contact contact) {
    this.contact = contact;
  }

  void setStage(Stage stage) {
    this.stage = stage;
  }

  void setController1(FXMLPhoneBookController controller1) {
    // Set controller of the main stage.
    this.controller1 = controller1;
  }

  void setIsContactNew(boolean isContactNew) {
    this.isContactNew = isContactNew;
  }

  void setTextFields() {
    // Initialize TextFields' values with Contact parameters
    // (if we're editing an existing contact).
    surnameField.setText(contact.getLastName());
    nameField.setText(contact.getFirstName());
    patronymicField.setText(contact.getPatronymic());
    mobilePhoneField.setText(contact.getMobilePhone());
    homePhoneField.setText(contact.getHomePhone());
    addressField.setText(contact.getAddress());
    setBirthdayDatePickerValue();
    commentArea.setText(contact.getComment());
  }

  /**
   * Sets the DatePicker value in the UI depending on existing contact's parameters.
   */
  private void setBirthdayDatePickerValue() {
    String date = contact.getDateOfBirth();
    if (DateParser.isStringDateFormatCorrect(date)) {
      birthdayDatePicker.setValue(DateParser.getLocalDateFromString(date));
    } else {
      // This case can happen if the date is not set at all.
      birthdayDatePicker.setValue(null);
    }
  }

  @FXML
  private void onCancelButtonPressed() {
    stage.close();
  }

  @FXML
  private void onSaveButtonPressed() {
    try {
      // Check if contact with this name + surname + patronymic
      // is already present in our table.
      if (cannotSaveContact()) {
        ErrorAlertManager.showAlert("Ошибка", "Невозможно сохранить контакт",
            "Контакт с данным ФИО уже существует в базе данных.", AlertType.ERROR);
        return;
      }
      // Check if any text fields contain more than 255 characters, since
      // it's a database restriction.
      if (areAnyTextFieldsTooLong()) {
        ErrorAlertManager.showAlert("Ошибка", "Невозможно сохранить контакт",
            "Текстовые поля не должны содержать более 255 символов.", AlertType.ERROR);
        return;
      }
      // Save information about current contact.
      setContactParams();
      // If we're creating a new contact, add it to the database.
      if (isContactNew) {
        JDBCManager.addContactToDB(contact);
      } else {
        // If we're editing an existing contacts, update its contents in database.
        JDBCManager.editExistingContactByID(contact);
      }
      // Update table to show all entries.
      controller1.refreshTable();
    } catch (SQLException se) {
      String context = "Произошла ошибка при проверки наличия существования контакта при выполнении операции ";
      context += isContactNew ? "добавления:\n\n" : "редактирования:\n\n";
      ErrorAlertManager.showSQLErrorAlert(context + se.getMessage());
    }
    // Close the window.
    stage.close();
  }

  @FXML
  private void onClearDatePressed() {
    // Clear the DatePicker value if user clicked 'Clear Date' button.
    birthdayDatePicker.getEditor().setText("");
    birthdayDatePicker.setValue(null);
    birthdayDatePicker.requestFocus();
  }

  /**
   * Sets criteria for incorrect contact.
   *
   * @return binding that specifies that contact with current values is invalid.
   */
  private BooleanBinding wrongContactCriteria() {
    return new BooleanBinding() {
      {
        super.bind(nameField.textProperty(), surnameField.textProperty(),
            patronymicField.textProperty(),
            mobilePhoneField.textProperty(), homePhoneField.textProperty(),
            addressField.textProperty(), birthdayDatePicker.getEditor().textProperty(),
            commentArea.textProperty());
      }

      @Override
      protected boolean computeValue() {
        // Contact can exist if it has a valid name, a valid surname
        // and at least one of 2 phones.
        boolean contactCannotExist = !Validator.canContactExist
            (nameField.getText(), surnameField.getText(),
                mobilePhoneField.getText(), homePhoneField.getText());
        boolean blankPatronymic = Validator.isEntryIncorrect(patronymicField.getText());
        boolean wrongMobileNum = Validator.isPhoneIncorrect(mobilePhoneField.getText());
        boolean wrongHomeNum = Validator.isPhoneIncorrect(homePhoneField.getText());
        boolean blankAddress = Validator.isEntryIncorrect(addressField.getText());
        boolean blankComment = Validator.isEntryIncorrect(commentArea.getText());
        boolean commentHasTabs = commentArea.getText().contains("\t");
        return contactCannotExist || wrongMobileNum || wrongHomeNum
            || blankPatronymic || blankAddress || blankComment || commentHasTabs;
      }
    };
  }

  /**
   * Fetch text from TextFields and edit corresponding properties of the current Contact instance.
   */
  private void setContactParams() {
    contact.setLastName(surnameField.getText().trim());
    contact.setFirstName(nameField.getText().trim());
    contact.setPatronymic(patronymicField.getText().trim());
    contact.setMobilePhone(mobilePhoneField.getText().trim());
    contact.setHomePhone(homePhoneField.getText().trim());
    contact.setAddress(addressField.getText().trim());
    contact.setDateOfBirth(DateParser.getStringFromLocalDate(birthdayDatePicker.getValue()));
    contact.setComment(commentArea.getText().trim());
  }

  /**
   * Checks if current contact can be saved to database by searching for its
   * full name in the database.
   * @return true if current contact cannot be saved to the database, false otherwise.
   * @throws SQLException if an error occurred while connecting to the database.
   */
  private boolean cannotSaveContact() throws SQLException {
    return JDBCManager.doesContactExistInDB
        (nameField.getText().trim(),
            surnameField.getText().trim(),
            patronymicField.getText().trim())
        &&
        (isContactNew ||
            isNameChanged() ||
            isSurnameChanged() ||
            isPatronymicChanged());
  }

  /**
   * Checks if any strings in text fields exceed maximum length.
   * @return true if any entry is too long, false otherwise.
   */
  private boolean areAnyTextFieldsTooLong() {
    int maxLength = JDBCManager.VARCHAR_MAX_SIZE;
    return Validator.isAnyFieldTooLong(maxLength, List.of(
        nameField.getText(), surnameField.getText(), patronymicField.getText(),
        mobilePhoneField.getText(), homePhoneField.getText(), addressField.getText(),
        commentArea.getText()));
  }

  private boolean isNameChanged() {
    return !nameField.getText().trim().equals(contact.getFirstName().trim());
  }

  private boolean isSurnameChanged() {
    return !surnameField.getText().trim().equals(contact.getLastName().trim());
  }

  private boolean isPatronymicChanged() {
    return !patronymicField.getText().trim().equals(contact.getPatronymic().trim());
  }

  /* Here comes the section of events which will put a red border
     around the TextField if its text is incorrect.*/

  @FXML
  private void onSurnameFieldChanged() {
    if (surnameField.getText().isBlank()) {
      surnameField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
    } else {
      surnameField.setStyle("");
    }
  }

  @FXML
  private void onNameFieldChanged() {
    if (nameField.getText().isBlank()) {
      nameField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
    } else {
      nameField.setStyle("");
    }
  }

  @FXML
  private void onPatronymicFieldChanged() {
    if (Validator.isEntryIncorrect(patronymicField.getText())) {
      patronymicField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
    } else {
      patronymicField.setStyle("");
    }
  }

  @FXML
  private void onMobilePhoneFieldChanged() {
    if (Validator.isPhoneIncorrect(mobilePhoneField.getText())) {
      mobilePhoneField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
    } else {
      mobilePhoneField.setStyle("");
    }
  }

  @FXML
  private void onHomePhoneFieldChanged() {
    if (Validator.isPhoneIncorrect(homePhoneField.getText())) {
      homePhoneField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
    } else {
      homePhoneField.setStyle("");
    }
  }

  @FXML
  private void onAddressFieldChanged() {
    if (Validator.isEntryIncorrect(addressField.getText())) {
      addressField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
    } else {
      addressField.setStyle("");
    }
  }

  @FXML
  private void onTextAreaChanged() {
    // Comment area must be free of tabulation symbols.
    // Otherwise we're going to have problems parsing it from file since tabulation is the separator...
    if (commentArea.getText().contains("\t")) {
      commentArea.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
    } else {
      commentArea.setStyle("");
    }
  }
}
