package MainApplication;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javafx.util.Callback;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

public class GenerateQrManager {

    private Workbook saveWorkBox;
    private Sheet sheet;
    private File saveFolder;
    private File file;
    private String fileName = "a_temp.xlsx";

    private Callback<Double, Void> callback;
    private Callback<Boolean, Void> callbackShowSuccess;

    public GenerateQrManager(File saveFolder, File file, Callback<Double, Void> callback, Callback<Boolean, Void> callbackShowSuceess) {
        this.saveFolder = saveFolder;
        this.file = file;
        this.callback = callback;
        this.callbackShowSuccess = callbackShowSuceess;
    }

    public void generateQr(String sheetName) throws IOException, WriterException {
        saveWorkBox = new XSSFWorkbook();
        sheet = saveWorkBox.createSheet("QrCode");

        Workbook workbook;

        workbook = new XSSFWorkbook(this.file.getPath());
        ArrayList<String> listQR = getAllValueStringFrom(workbook.getSheet(sheetName));
        System.out.println(listQR);
        if (saveFolder == null) {
            AlertBox.showAlertBox("Thông báo", "Bạn cần chọn folder chứa ảnh qr!");
            workbook.close();
            return;
        }

        if (listQR.stream().count() == 0) {
            AlertBox.showAlertBox("Thông báo", "File không chứa qrCode nào.!");
            workbook.close();
            return;
        }
        long numberQr = listQR.stream().count();
        for (int i = 0; i < numberQr; i++) {
            String type = "png";
            long timeInterger = new Date().getTime();
            String fileName = "image_" + (i + 1) + "_" + timeInterger + "." + type;
            String path = this.saveFolder.getPath();
            File qrCode = new File(path + "/" + fileName);

            Double progress = Double.valueOf((Double.valueOf(i) / numberQr));
            callback.call(progress);

            this.createQRImage(qrCode, listQR.get(i), 125, type);
            this.saveValue(i + 1, listQR.get(i), fileName, qrCode.getAbsolutePath());

            if (i == numberQr - 1) {
                workbook.close();
                saveToFile();
                Double finalProgress = 1.0;
                callback.call(finalProgress);
                callbackShowSuccess.call(true);
            }
        }
    }

    private ArrayList<String> getAllValueStringFrom(Sheet sheet) {
        Set<String> listQr = new HashSet<>();
        int i = 0;
        for (Row row : sheet) {
            for (Cell cell : row) {
                switch (cell.getCellType()) {
                    case STRING:

                        String dataValueAtCell = cell.getRichStringCellValue().getString().trim();
                        if (isUrlValid(dataValueAtCell)) {
                            listQr.add(dataValueAtCell);
                        }
                        break;
                    case NUMERIC:
                        break;
                    case BOOLEAN:
                        break;
                    case FORMULA:
                        break;
                    default:
                        break;
                }
            }
            i++;
        }
        return new ArrayList<>(listQr);
    }

    private boolean isUrlValid(String string) {
        try {
            new URL(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void createQRImage(File qrFile, String qrCodeText, int size, String fileType)
            throws WriterException, IOException {
        // Create the ByteMatrix for the QR-Code that encodes the given String
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
        // Make the BufferedImage that are to hold the QRCode
        int matrixWidth = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(java.awt.Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);
        // Paint and save the image using the ByteMatrix
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        ImageIO.write(image, fileType, qrFile);
    }

    private void saveValue(int index, String qrCode, String nameQr, String pathQr) {

        Row header = sheet.createRow(index);

        CellStyle headerStyle = saveWorkBox.createCellStyle();

        XSSFFont font = ((XSSFWorkbook) saveWorkBox).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 12);
        font.setBold(false);
        headerStyle.setFont(font);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue(nameQr);
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue(qrCode);
        headerCell.setCellStyle(headerStyle);
    }

    private String saveToFile() throws IOException {
        String fileLocation = this.saveFolder.getPath() + "/" + this.fileName;
        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        saveWorkBox.write(outputStream);
        saveWorkBox.close();
        return fileLocation;
    }

}
