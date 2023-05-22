package phonebook.controllers;

import phonebook.Contact;
import phonebook.ContactDataManager;
import phonebook.ErrorAlertManager;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class ContactExporterController {

  @FXML
  private Button OKButton;
  @FXML
  private TextField outputFileNameTextField;
  @FXML
  private TextField directoryPathTextField;

  private Stage stage;
  private List<Contact> contactList;

  /**
   * Called when export window is created.
   */
  @FXML
  public void initialize() {
    // Disable OK button if directory path or file name is blank,
    // or if file name contains prohibited characters.
    OKButton.disableProperty().bind(bindIncorrectFields());
  }

  /**
   * Sets the list of contacts to be exported.
   *
   * @param contacts list of contacts to be exported.
   */
  void setContacts(ObservableList<Contact> contacts) {
    contactList = contacts;
  }

  /**
   * Sets the stage for current window.
   *
   * @param stage stage to be shown in export window.
   */
  void setStage(Stage stage) {
    this.stage = stage;
  }

  /**
   * Called when user clicks on a TextField to choose export path.
   */
  @FXML
  private void onDirectoryPathFieldClicked() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    File selectedDirectory = directoryChooser.showDialog(stage);
    if (selectedDirectory != null) {
      // If directory was chosen successfully, save absolute path to it
      // as text in the TextField.
      directoryPathTextField.setText(selectedDirectory.getAbsolutePath());
    }
  }

  /**
   * Called when user exits export window.
   */
  @FXML
  private void onCancelButtonPressed() {
    stage.close();
  }

  /**
   * Called when user presses OK button. Writes all contacts from database to a .csv file with
   * specified name.
   */
  @FXML
  private void onOKButtonPressed() {
    // Get the path to a file where contacts will be exported.
    String pathToExportFile = directoryPathTextField.getText() +
        File.separator + outputFileNameTextField.getText() + ".csv";
    try {
      // Try to serialize contacts from the main controller to a .csv file with given path.
      ContactDataManager.writeContactsToCSV(contactList, pathToExportFile);
      // If serialization process completed successfully, show
      // a confirmation window.
      ErrorAlertManager.showAlert(
          "Экспортирование контактов выполнено", "Контакты успешно экспортированы",
          "Контакты экспортированы по следующему пути: " + pathToExportFile,
          AlertType.CONFIRMATION);
    } catch (IOException e) {
      // If an exception occurs during serialization, warn the user.
      ErrorAlertManager.showAlert("Ошибка", "Невозможно экспортировать контакты",
          e.getMessage(), AlertType.ERROR);
    }
    stage.close();
  }

  /**
   * Sets criteria for prohibition of illegal characters that cannot be used in file names, as well
   * as disallowing blank characters in text fields.
   *
   * @return binding that specifies prohibited characters.
   */
  private BooleanBinding bindIncorrectFields() {
    return new BooleanBinding() {
      {
        super.bind(outputFileNameTextField.textProperty(), directoryPathTextField.textProperty());
      }

      @Override
      protected boolean computeValue() {
        return outputFileNameTextField.getText().contains(" ") ||
            outputFileNameTextField.getText().contains("<") ||
            outputFileNameTextField.getText().contains(">") ||
            outputFileNameTextField.getText().contains(":") ||
            outputFileNameTextField.getText().contains("\"") ||
            outputFileNameTextField.getText().contains("/") ||
            outputFileNameTextField.getText().contains("\\") ||
            outputFileNameTextField.getText().contains("|") ||
            outputFileNameTextField.getText().contains("?") ||
            outputFileNameTextField.getText().contains("*") ||
            outputFileNameTextField.getText().isBlank() ||
            directoryPathTextField.getText().isBlank();
      }
    };
  }
}
