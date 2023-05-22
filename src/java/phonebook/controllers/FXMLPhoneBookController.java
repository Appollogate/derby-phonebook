package phonebook.controllers;

import phonebook.ErrorAlertManager;
import phonebook.Contact;
import phonebook.JDBCManager;
import phonebook.Validator;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLPhoneBookController {

  @FXML
  private TextField searchTextField;
  @FXML
  private MenuItem editContactMenuItem;
  @FXML
  private MenuItem deleteContactMenuItem;
  @FXML
  private Button editContactButton;
  @FXML
  private Button deleteContactButton;
  @FXML
  private TableView<Contact> tableView;

  /**
   * Called on start of the program.
   */
  @FXML
  public void initialize() {
    // Loads data from database, if there are any records.
    refreshTableEntries();
    // Disable buttons used for editing and deleting contacts
    // if the current selection in table is empty.
    disableButtonsIfSelectionEmpty();
  }

  /**
   * Shows program info in a separate window.
   */
  @FXML
  private void showInfo() {
    String context = "Программу разработала студент БПИ198 Скрыпина Дарья Кирилловна.\n\n"
        + "Краткая справка о приложении:\n\n"
        + "Данное приложение представляет из себя телефонную книгу с возможностью добавления, "
        + "изменения, удаления, поиска и сохранения контактов. При добавлении нового контакта необходимо указывать "
        + "как минимум имя, фамилию и хотя бы один из телефонов. Список контактов сохраняется "
        + "автоматически при выходе из приложения с помощью базы данных Apache Derby. "
        + "Импорт и экспорт контактов происходит с использованием файлов формата CSV с табуляцией в качестве "
        + "разделителя (именно по этой причине в комментарии к контакту знак табуляции не разрешён).";
    ErrorAlertManager.showAlert("Информация о программе", "Справочная информация", context,
        AlertType.INFORMATION);
  }

  /**
   * Called when the user is exiting the main window.
   */
  @FXML
  private void exitApp() {
    Platform.exit();
  }

  /**
   * Called when the user presses a button/menu item to add a new contact.
   */
  @FXML
  private void onAddContactPressed() {
    // Save the controller of editContact scene.
    FXMLEditContactController controller2;
    try {
      controller2 = showEditContactWindow("Добавление контакта");
    } catch (IOException e) {
      ErrorAlertManager.showAlert("Ошибка", "Произошла ошибка при загрузке .fxml файла",
          e.getMessage(), AlertType.ERROR);
      return;
    }
    // Set the settings for adding a new contact.
    controller2.setContact(new Contact());
    controller2.setController1(this);
    controller2.setIsContactNew(true);
  }

  /**
   * Called when the user presses a button/menu item to edit an existing contact.
   */
  @FXML
  private void onEditContactPressed() {
    // Get selected contact from table.
    Contact contactToEdit = tableView.getSelectionModel().getSelectedItem();
    // Open a window to edit it.
    FXMLEditContactController controller2;
    try {
      controller2 = showEditContactWindow("Редактирование контакта");
    } catch (IOException e) {
      ErrorAlertManager.showAlert("Ошибка", "Произошла ошибка при загрузке .fxml файла",
          e.getMessage(), AlertType.ERROR);
      return;
    }
    controller2.setController1(this);
    // Set the settings for editing an existing contact.
    controller2.setContact(contactToEdit);
    controller2.setTextFields();
    controller2.setIsContactNew(false);
  }

  /**
   * Called when the user presses a button/menu item to delete an existing contact.
   */
  @FXML
  private void onDeleteContactPressed() {
    // Get selected contact from table.
    Contact selectedContact = tableView.getSelectionModel().getSelectedItem();
    // Delete selected contact from database by its ID.
    try {
      JDBCManager.deleteContactFromDBByID(selectedContact.getId());
    } catch (SQLException se) {
      ErrorAlertManager.showSQLErrorAlert(se.getMessage());
    }
    refreshTable();
  }

  /**
   * Load the stage for adding/editing contact window and show it.
   *
   * @param title title of the created window.
   * @return controller of EditContact stage.
   * @throws IOException if corresponding .fxml file is not found
   */
  private FXMLEditContactController showEditContactWindow(String title) throws IOException {
    FXMLLoader loader = new FXMLLoader();
    Parent root = loader.load(getClass().getResource("/editContact.fxml").openStream());
    Stage editStage = new Stage();
    // Make window modal.
    editStage.initModality(Modality.APPLICATION_MODAL);
    editStage.setTitle(title);
    editStage.setScene(new Scene(root, 450, 450));
    editStage.setResizable(false);
    editStage.show();
    // Save the controller of editContact scene.
    FXMLEditContactController controller = loader.getController();
    controller.setStage(editStage);
    return controller;
  }


  /**
   * Called when the user presses the search button to filter existing contacts by entered query.
   */
  @FXML
  private void onSearchButtonPressed() {
    // Retrieve search query from TextField.
    String searchQuery = searchTextField.getText().trim();
    if (searchQuery.isBlank()) {
      // Refresh table if search query is blank.
      refreshTableEntries();
    } else {
      // Otherwise, apply filter on current contacts and show filtered ones.
      try {
        tableView.setItems(JDBCManager.getAllContactsMatchingSearchQuery(searchQuery));
      } catch (SQLException se) {
        ErrorAlertManager.showSQLErrorAlert(
            "Ошибка: не удалось отфильтровать контакты в БД по заданному запросу.\n\n"
                + se.getMessage());
      }
    }
  }

  /**
   * Called when the user presses the button to reset search filter.
   */
  @FXML
  private void onResetButtonPressed() {
    refreshTable();
  }

  /**
   * Validate contacts in the given list and add correct ones to database and table.
   *
   * @param contactsToLoad list of contacts to be loaded.
   */
  void addLoadedContactsToTable(List<Contact> contactsToLoad) {
    // List of contacts in correct format.
    List<Contact> correctContacts = new ArrayList<>();
    // Number of contacts that have incorrect format.
    int incorrectContacts = 0;
    // Number of contacts whose full name already exists in the table.
    int existingContacts = 0;
    try {
      for (Contact contact : contactsToLoad) {
        // Check if current contact is in incorrect format.
        if (!Validator.isContactValid(contact)) {
          ++incorrectContacts;
        }
        // Check if a contact with exact same full name already exists.
        else if (JDBCManager.doesContactExistInDB(contact.getFirstName(), contact.getLastName(),
            contact.getPatronymic())) {
          ++existingContacts;
        } else {
          // If everything's okay, add it to database
          correctContacts.add(contact);
        }
      }
    } catch (SQLException se) {
      ErrorAlertManager.showSQLErrorAlert(
          "Ошибка: возникла ошибка при подключении к БД в ходе проверки существования "
              + "контакта в БД при импортировании новых контактов.\n\n"
              + se.getMessage());
      return;
    }
    // Show a warning if there were any contacts in incorrect format.
    if (incorrectContacts > 0) {
      String context = "В импортированных даннных обнаружено " + incorrectContacts
          + " контакта(-ов) в неправильном формате.\n"
          + "Данные контакты не были загружены в таблицу.\n\n" +
          "Возможные причины:\n - текстовое поле в контакте содержит более 255 символов;"
          + "\n - У контакта не указано имя/фамилия/хотя бы один из телефонов;"
          + "\n - Дата имеет формат, отличный от дд.мм.гггг;"
          + "\n - Номер телефона включает в себя символы, не являющиеся цифрами.";
      ErrorAlertManager.showAlert("Ошибка", "Не удалось полностью загрузить сохранённые контакты",
          context, AlertType.WARNING);
    }
    // Show a warning if there were any already existing contacts.
    if (existingContacts > 0) {
      String context = "В импортированных даннных обнаружено " + existingContacts
          + " контакта(-ов), уже существующих в таблице с такими ФИО.\n"
          + "Данные контакты не были загружены в таблицу.";
      ErrorAlertManager.showAlert("Ошибка", "Не удалось полностью загрузить сохранённые контакты",
          context, AlertType.WARNING);
    }
    try {
      // Add all correct contacts to database.
      for (Contact contact : correctContacts) {
        JDBCManager.addContactToDB(contact);
      }
      refreshTableEntries();
    } catch (SQLException se) {
      ErrorAlertManager.showSQLErrorAlert(
          "Ошибка: не получилось добавить импортированные контакты в базу данных.\n\n"
              + se.getMessage());
    }
  }

  /**
   * Load import window and set its settings.
   */
  @FXML
  private void importContacts() {
    FXMLLoader loader = new FXMLLoader();
    Parent root;
    try {
      root = loader.load(getClass().getResource("/contactImporter.fxml").openStream());
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    Stage importStage = new Stage();
    // Make window modal.
    importStage.initModality(Modality.APPLICATION_MODAL);
    importStage.setTitle("Импорт контактов");
    importStage.setScene(new Scene(root, 500, 125));
    importStage.setResizable(false);
    importStage.show();
    ContactImporterController controller = loader.getController();
    controller.setStage(importStage);
    // Pass the current instance of this class to the new controller.
    controller.setMainController(this);
  }

  /**
   * Load export window and set its settings.
   */
  @FXML
  private void exportContacts() {
    FXMLLoader loader = new FXMLLoader();
    Parent root;
    try {
      root = loader.load(getClass().getResource("/contactExporter.fxml").openStream());
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    Stage exportStage = new Stage();
    // Make window modal.
    exportStage.initModality(Modality.APPLICATION_MODAL);
    exportStage.setTitle("Экспорт контактов");
    exportStage.setScene(new Scene(root, 500, 150));
    exportStage.setResizable(false);
    exportStage.show();
    ContactExporterController controller = loader.getController();
    controller.setStage(exportStage);
    try {
      controller.setContacts(JDBCManager.getAllContactsFromDB());
    } catch (SQLException se) {
      ErrorAlertManager.showSQLErrorAlert(
          "Ошибка: не удалось получить все записи из БД при экспорте данных.\n\n" +
              se.getMessage());
    }
  }

  /**
   * Disable buttons used for editing and deleting contacts if the table selection is empty.
   */
  private void disableButtonsIfSelectionEmpty() {
    // Disable 'edit contact' button if selection is empty.
    editContactButton.disableProperty()
        .bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
    // Do the same for the menu item.
    editContactMenuItem.disableProperty()
        .bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
    // Disable 'delete contact' button if selection is empty.
    deleteContactButton.disableProperty()
        .bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
    // Do the same for the menu item.
    deleteContactMenuItem.disableProperty()
        .bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
  }

  /**
   * Cancel any applied filters and renew table entries.
   */
  void refreshTable() {
    // Empty text in search bar.
    searchTextField.setText("");
    // Refresh table and make it show all contacts data.
    refreshTableEntries();
  }

  /**
   * Pull existing contact data from database and set it as table contents.
   */
  private void refreshTableEntries() {
    try {
      // Get all contacts that currently exist in the table.
      ObservableList<Contact> data = JDBCManager.getAllContactsFromDB();
      tableView.setItems(data);
    } catch (SQLException se) {
      ErrorAlertManager.showSQLErrorAlert(
          "Ошибка: не удалось получить все записи из БД при обновлении таблицы.\n\n" +
              se.getMessage());
    }
  }
}
