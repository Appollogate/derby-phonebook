package phonebook;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

public class ErrorAlertManager {

  /**
   * Shows a modal system alert window with specified settings.
   * @param title title of alert window.
   * @param header header of alert window.
   * @param context main text of alert window.
   * @param alertType type of alert window.
   */
    public static void showAlert(String title, String header, String context, AlertType alertType){
      Alert alert = new Alert(alertType, context, ButtonType.CLOSE);
      alert.setHeaderText(header);
      alert.setTitle(title);
      alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      alert.showAndWait();
    }

  /**
   * Shows a modal system alert window specifically designed for showing SQL exceptions.
   * @param context main text of alert window.
   */
  public static void showSQLErrorAlert(String context){
      Alert alert = new Alert(AlertType.ERROR, context, ButtonType.CLOSE);
      alert.setHeaderText("Произошла ошибка при работе с БД:");
      alert.setTitle("SQL Error");
      alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      alert.showAndWait();
    }
}
