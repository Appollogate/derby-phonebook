package phonebook.controllers;

import phonebook.ErrorAlertManager;
import phonebook.Contact;
import phonebook.ContactDataManager;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ContactImporterController {

  @FXML
  private Button OKButton;
  @FXML
  private TextField filePathTextField;

  private Stage stage;
  private FXMLPhoneBookController mainController;

  @FXML
  public void initialize() {
    OKButton.disableProperty().bind(Bindings.isEmpty(filePathTextField.textProperty()));
  }

  void setStage(Stage stage) {
    this.stage = stage;
  }

  void setMainController(FXMLPhoneBookController controller) {
    this.mainController = controller;
  }

  @FXML
  private void onFilePathFieldClicked() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new ExtensionFilter("CSV Files", "*.csv"));
    // Choose a CSV file, contents of which will be imported to the table.
    File selectedFile = fileChooser.showOpenDialog(stage);
    if (selectedFile != null) {
      // Save absolute path to CSV file (if chosen) to TextField
      filePathTextField.setText(selectedFile.getAbsolutePath());
    }
  }

  @FXML
  private void onCancelButtonPressed() {
    stage.close();
  }

  @FXML
  private void onOKButtonPressed() {
    List<Contact> importedContacts;
    try {
      // Try to deserialize contacts from the CSV file.
      importedContacts = ContactDataManager.readContactsFromCSV(filePathTextField.getText());
    } catch (IllegalArgumentException e) {
      // Happens when CSV file has wrong format for deserializing contacts.
      ErrorAlertManager
          .showAlert("Ошибка", "Невозможно импортировать контакты", e.getMessage(), AlertType.ERROR);
      return;
    } catch (IOException e) {
      // Happens when an file system error occurs.
      ErrorAlertManager
          .showAlert("Ошибка", "Произошла ошибка при работе с файлами", e.getMessage(), AlertType.ERROR);
      return;
    }
    // Add imported contacts to the main table.
    mainController.addLoadedContactsToTable(importedContacts);
    stage.close();
  }
}
