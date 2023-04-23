package MainApplication;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.xb.xsdschema.ListDocument;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;



public class Main extends Application {

    Stage window;
    Stage loading;

    // folder save image
    File file;
    File saveFolder;

    BorderPane layout;

    String fileName = "a_temp.xlsx";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        window = stage;

        window.setTitle("Tạo Qr");

        layout = new BorderPane();
        layout.setTop(createTopBox());

        Label label = new Label("Developer code java.!");
        label.setPadding(new Insets(20));
        StackPane bottomPanel = new StackPane(label);
        layout.setBottom(bottomPanel);

        Scene scene = new Scene(layout);

        window.setScene(scene);
        window.setMaximized(true);
        window.show();
    }

    public VBox createTopBox() {
        HBox filePanel = new HBox(5);
        filePanel.setPadding(new Insets(10, 10, 10, 10));
        Button chooseFileExcel = new Button("Choose file xlsx");
        Label path = new Label();
        chooseFileExcel.setOnAction(e -> {
            File file = selectFileXlsx();
            if (file != null) {
                this.file = file;
                path.setText(this.file.getAbsoluteFile().getPath());
                leftBorderPanel();
            }
        });
        filePanel.getChildren().addAll(chooseFileExcel, path);

        HBox folderPanel = new HBox(5);
        folderPanel.setPadding(new Insets(10, 10, 10, 10));
        Button chooseFolder = new Button("Choose Folder");
        Label pathFolder = new Label();
        chooseFolder.setOnAction(e -> {
            File file = selectFolderSave();
            if (file != null) {
                this.saveFolder = file;
                pathFolder.setText(this.saveFolder.getAbsoluteFile().getPath());
            }
        });
        folderPanel.getChildren().addAll(chooseFolder, pathFolder);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(folderPanel, filePanel);

        return vBox;
    }

    private void leftBorderPanel() {
        ArrayList<String> names = this.getSheetNames();

        ListView<String> listView = new ListView<>();
        listView.setItems(FXCollections.observableList(names));
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                centerBorderPanel(t1);
            }
        });
        listView.setMinWidth(200);
        layout.setLeft(listView);
    }
    private void centerBorderPanel(String sheetName) {
        StackPane pane = new StackPane();

        Button btnGenerate = new Button("Tạo mã QR");
        btnGenerate.setOnAction(event -> {
            if (checkFolderExits(this.saveFolder) == false) {
                return;
            }
            ShowProgressGenerate.showInprogressGenerateQr(saveFolder, file, sheetName);
        });
        pane.getChildren().addAll(btnGenerate);
        layout.setCenter(pane);
    }

    // folder save image
    private File selectFileXlsx() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn thư mục lưu file.");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XLSX", "*.xlsx")
        );
        return fileChooser.showOpenDialog(window);
    }

    private File selectFolderSave() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File folder = directoryChooser.showDialog(window);
        if (checkFolderExits(folder) == false) {
            return null;
        }
        return folder;
    }

    private Boolean checkFolderExits(File folder) {
        if (folder != null) {
            String pathFile = folder.getPath() + "/" + fileName;
            File file = new File(pathFile);

            if (file.exists()) {
                file.deleteOnExit();
                AlertBox.showAlertBox("Thông báo", "Bạn đã tạo Qr rồi\nXoá file " + fileName + " để có thể tiếp tục.");
                return false;
            } else {
                return true;
            }
        } else {
            return  true;
        }
    }

    private ArrayList<String> getSheetNames() {
        Workbook workbook;
        ArrayList<String> list = new ArrayList<>();
        try {
            workbook = new XSSFWorkbook(file);
            for (int i = 0; i <workbook.getNumberOfSheets(); i++) {
                list.add(workbook.getSheetName(i));
            }
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

}
