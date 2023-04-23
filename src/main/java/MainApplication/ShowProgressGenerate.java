package MainApplication;

import com.google.zxing.WriterException;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;

public class ShowProgressGenerate {

    public static void showInprogressGenerateQr(File saveFolder, File file, String sheetName) {

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Loading...");

        ProgressBar progressBar = new ProgressBar(0);
        VBox box = new VBox();
        box.getChildren().add(progressBar);

        window.setOnCloseRequest(e -> {
            e.consume();
        });

        Callback<Double, Void> callback = new Callback<Double, Void>() {
            @Override
            public Void call(Double aDouble) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(aDouble);
                    }
                });

                return null;
            }
        };

        Callback<Boolean, Void> callbackSuccess = new Callback<Boolean, Void>() {
            @Override
            public Void call(Boolean aBoolean) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        VBox vBox = new VBox();
                        vBox.setPadding(new Insets(20, 20, 20, 20));

                        String fileName = "a_temp.xlsx";
                        Label label = new Label("QR đã được tạo trong file " + fileName);

                        Button button = new Button("Ok");
                        button.setOnAction(e -> {
                            window.close();
                        });

                        vBox.getChildren().add(label);
                        vBox.getChildren().add(button);
                        StackPane stackPane = new StackPane(vBox);

                        Scene sceneSuccess = new Scene(stackPane, 300,100);
                        window.setScene(sceneSuccess);
                    }
                });
                return null;
            }
        };

        GenerateQrManager generateQrManager = new GenerateQrManager(saveFolder, file, callback, callbackSuccess);

        Thread taskThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    generateQrManager.generateQr(sheetName);
                } catch (IOException e) {
                    AlertBox.showAlertBox("Error", e.getLocalizedMessage());
                    return;
                } catch (WriterException e) {
                    AlertBox.showAlertBox("Error", e.getLocalizedMessage());
                    return;
                }
            }
        });
        taskThread.start();

        StackPane stackPane = new StackPane(progressBar);
        Scene scene = new Scene(stackPane, 300, 100);
        window.setScene(scene);
        window.showAndWait();
    }
}
