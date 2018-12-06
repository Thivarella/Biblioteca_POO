package boboteca.controllers;

import com.jfoenix.controls.JFXButton;
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
        if(message.equals("Este usuário possui multas pendentes de pagamento")){
            confirmDeletion.setVisible(false);
            cancelDeletion.setText("OK");
            cancelDeletion.getStyleClass().removeAll();
            cancelDeletion.getStyleClass().add("btn-info");
        }
    }

    public void confirmDeletion() {
        btnConfirmClicked = true;
        dialogStage.close();
    }

    public void cancelDeletion() {
        dialogStage.close();
    }

}
