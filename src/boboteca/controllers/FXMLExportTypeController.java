package boboteca.controllers;

import boboteca.utils.ExportFlyweight;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLExportTypeController implements Initializable {
    @FXML
    private Label message;
    @FXML
    private JFXButton exportXml;
    @FXML
    private JFXButton exportJson;
    @FXML
    private JFXButton exportCsv;

    private Integer type;

    private ExportFlyweight exportFlyweight;
    private Stage dialogStage;

    public void setType(Integer type) {
        this.type = type;
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        exportFlyweight = ExportFlyweight.getInstance();
    }

    public void exportXml() throws IOException {
        exportFlyweight.getComando(this.type,1);
        dialogStage.close();
    }

    public void exportJson() throws IOException {
        exportFlyweight.getComando(this.type,2);
        dialogStage.close();
    }

    public void exportCsv() throws IOException {
        exportFlyweight.getComando(this.type,3);
        dialogStage.close();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}
