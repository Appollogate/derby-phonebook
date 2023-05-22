package phonebook;

import java.io.IOException;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {

    try {
      JDBCManager.createTable();
      FXMLLoader loader = new FXMLLoader();
      Parent root = loader.load(getClass().getResource("/phonebook.fxml").openStream());
      primaryStage.setTitle("Телефонная книга");
      primaryStage.setScene(new Scene(root, 1200, 800));
      primaryStage.setMinWidth(1200);
      primaryStage.setMinHeight(800);
      primaryStage.show();
    } catch (SQLException se) {
      ErrorAlertManager.showSQLErrorAlert(se.getMessage());
    } catch (IOException e) {
      ErrorAlertManager.showAlert("IOException", "Ошибка ввода-вывода",
          "Произошла ошибка при получении доступа к ресурсу phonebook.fxml:\n\n" + e.getMessage(),
          AlertType.ERROR);
    }
  }

  @Override
  public void stop() {
    try {
      JDBCManager.shutDown();
    } catch (SQLException se) {
      ErrorAlertManager.showSQLErrorAlert(se.getMessage());
    }
  }
}
