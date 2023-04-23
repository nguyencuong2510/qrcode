package MainApplication;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProgressBox {

    public static Stage show(String title, String message) {
        Stage window = new Stage();
        window.setOnCloseRequest(e -> {
            e.isConsumed();
        });
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        Label label = new Label(message);
        StackPane stackPane = new StackPane(label);
        Scene scene = new Scene(stackPane, 300, 100);
        window.setScene(scene);
        window.showAndWait();
        return window;
    }
}
