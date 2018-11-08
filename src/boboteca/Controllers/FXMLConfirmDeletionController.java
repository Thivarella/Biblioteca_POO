package boboteca.Controllers;

import boboteca.Model.Book;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLConfirmDeletionController implements Initializable {
    @FXML
    private Label message;
    @FXML
    private JFXButton confirmDeletion;
    @FXML
    private JFXButton cancelDeletion;

    private Boolean btnConfirmClicked = false;
    private Stage dialogStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }


    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isBtnConfirmClicked() {
        return btnConfirmClicked;
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }

    public void confirmDeletion(ActionEvent actionEvent) {
        btnConfirmClicked = true;
        dialogStage.close();
    }

    public void cancelDeletion(ActionEvent actionEvent) {
        dialogStage.close();
    }
}
