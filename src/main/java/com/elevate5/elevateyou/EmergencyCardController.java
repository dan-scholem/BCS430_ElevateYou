package com.elevate5.elevateyou;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import com.elevate5.elevateyou.dao.EmergencyCardDao;
import com.elevate5.elevateyou.model.EmergencyCardModel;
import com.elevate5.elevateyou.session.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.ExecutionException;

public class EmergencyCardController {

    @FXML private TextField fullNameField;
    @FXML private TextField phoneField;
    @FXML private TextField dobField;
    @FXML private TextField emContactNameField;
    @FXML private TextField emContactPhoneField;
    @FXML private TextArea conditionsArea;
    @FXML private TextArea allergiesArea;
    @FXML private TextArea medicationsArea;
    @FXML private TextArea surgeryHistoryArea;
    @FXML private TextArea implantsArea;
    @FXML private TextField anticoagulantField;
    @FXML private TextArea insuranceArea;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button exportButton;

    private final EmergencyCardDao dao = new EmergencyCardDao();
    private String uid;

    @FXML
    private void initialize() {
        uid = (SessionManager.getSession() != null && SessionManager.getSession().getUserID() != null)
                ? SessionManager.getSession().getUserID()
                : "dev-demo-uid";

        try {
            EmergencyCardModel existing = dao.load(uid);
            if (existing != null) {
                fillForm(existing);
                addButton.setDisable(true);
                updateButton.setDisable(false);
            } else {
                addButton.setDisable(false);
                updateButton.setDisable(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load emergency card: " + e.getMessage());
        }
    }

    private void fillForm(EmergencyCardModel m) {
        fullNameField.setText(m.getFullName());
        phoneField.setText(m.getPhone());
        dobField.setText(m.getDob());
        emContactNameField.setText(m.getEmergencyContactName());
        emContactPhoneField.setText(m.getEmergencyContactPhone());
        conditionsArea.setText(m.getConditions());
        allergiesArea.setText(m.getAllergies());
        medicationsArea.setText(m.getMedications());
        surgeryHistoryArea.setText(m.getSurgeryHistory());
        implantsArea.setText(m.getImplants());
        anticoagulantField.setText(m.getAnticoagulant());
        insuranceArea.setText(m.getInsurance());
    }

    private EmergencyCardModel collectForm() {
        return new EmergencyCardModel(
                fullNameField.getText(),
                phoneField.getText(),
                dobField.getText(),
                emContactNameField.getText(),
                emContactPhoneField.getText(),
                conditionsArea.getText(),
                allergiesArea.getText(),
                medicationsArea.getText(),
                surgeryHistoryArea.getText(),
                implantsArea.getText(),
                anticoagulantField.getText(),
                insuranceArea.getText()
        );
    }

    @FXML
    private void onAdd() {
        EmergencyCardModel m = collectForm();
        try {
            dao.upsert(uid, m);
            addButton.setDisable(true);
            updateButton.setDisable(false);
            showInfo("Emergency card added.");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to add emergency card: " + e.getMessage());
        }
    }

    @FXML
    private void onUpdate() {
        EmergencyCardModel m = collectForm();
        try {
            dao.upsert(uid, m);
            showInfo("Emergency card updated.");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to update emergency card: " + e.getMessage());
        }
    }

    @FXML
    private void onExport() {
        try {
            EmergencyCardModel m = dao.load(uid);
            if (m == null) {
                showError("No emergency card to export.");
                return;
            }

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Export Emergency Card");
            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"),
                    new FileChooser.ExtensionFilter("All Files (*.*)", "*.*")
            );
            chooser.setInitialFileName("EmergencyCard.txt");
            File file = chooser.showSaveDialog(getStage());
            if (file == null) return;

            try (FileWriter fw = new FileWriter(file)) {
                fw.write(buildExportText(m));
            }
            showInfo("Exported to: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to export emergency card: " + e.getMessage());
        }
    }

    @FXML
    private void onGenerateQr() {
        try {
            EmergencyCardModel m = dao.load(uid);
            if (m == null) {
                showError("No emergency card to export.");
                return;
            }

            String content = buildExportText(m);

            int size = 400;
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size);

            BufferedImage qrBuffered = MatrixToImageWriter.toBufferedImage(matrix);
            WritableImage fxImage = SwingFXUtils.toFXImage(qrBuffered, null);
            ImageView imageView = new ImageView(fxImage);
            imageView.setFitWidth(300);
            imageView.setPreserveRatio(true);

            Button saveBtn = new Button("Save QR as PNG");
            saveBtn.setOnAction(ev -> {
                try {
                    FileChooser chooser = new FileChooser();
                    chooser.setTitle("Save QR Code");
                    chooser.getExtensionFilters().add(
                            new FileChooser.ExtensionFilter("PNG Image", "*.png")
                    );
                    File file = chooser.showSaveDialog(getStage());
                    if (file == null) return;

                    ImageIO.write(qrBuffered, "PNG", file);
                    showInfo("QR saved to: " + file.getAbsolutePath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Failed to save QR image: " + ex.getMessage());
                }
            });

            VBox root = new VBox(10, imageView, saveBtn);
            root.setPadding(new Insets(10));
            root.setAlignment(Pos.CENTER);

            Stage dialog = new Stage();
            dialog.initOwner(getStage());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Emergency Card QR Code");
            dialog.setScene(new Scene(root, 350, 420));
            dialog.show();

        } catch (WriterException we) {
            we.printStackTrace();
            showError("Failed to generate QR code: " + we.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to export QR code: " + e.getMessage());
        }
    }

    private String buildExportText(EmergencyCardModel m) {
        StringBuilder sb = new StringBuilder();
        sb.append("Emergency Card\n");
        sb.append("================================\n");
        sb.append("Full Name: ").append(m.getFullName()).append("\n");
        sb.append("Phone: ").append(m.getPhone()).append("\n");
        sb.append("Date of Birth: ").append(m.getDob()).append("\n\n");

        sb.append("Emergency Contact:\n");
        sb.append("  Name: ").append(m.getEmergencyContactName()).append("\n");
        sb.append("  Phone: ").append(m.getEmergencyContactPhone()).append("\n\n");

        sb.append("Medical History:\n").append(m.getConditions()).append("\n\n");
        sb.append("Allergies:\n").append(m.getAllergies()).append("\n\n");
        sb.append("Medications:\n").append(m.getMedications()).append("\n\n");

        sb.append("Surgery History:\n").append(m.getSurgeryHistory()).append("\n\n");
        sb.append("Implanted Devices:\n").append(m.getImplants()).append("\n\n");
        sb.append("Anticoagulant Use:\n").append(m.getAnticoagulant()).append("\n\n");
        sb.append("Insurance Information:\n").append(m.getInsurance()).append("\n");

        return sb.toString();
    }

    @FXML
    private void onClose() {
        Stage s = getStage();
        if (s != null) s.close();
    }

    private Stage getStage() {
        return (Stage) fullNameField.getScene().getWindow();
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}