package MainApplication;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox {

    public static void showAlertBox(String title, String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        Label label = new Label(message);
        StackPane stackPane = new StackPane(label);
        Scene scene = new Scene(stackPane, 300, 100);
        window.setScene(scene);
        window.showAndWait();
    }
}
