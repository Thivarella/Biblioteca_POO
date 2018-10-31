package boboteca.Controllers;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLLoginController implements Initializable {
    @FXML
    private JFXTextField cpfTextLogin;
    @FXML
    private JFXTextField passwordTextLogin;

    private boolean btnConfirmClicked = false;

    private Stage dialogStage;
    private Stage prevStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    public void cancelLogin(ActionEvent actionEvent) {
        dialogStage.close();
    }

    public void tryLogin(ActionEvent actionEvent) {
        btnConfirmClicked = true;
        dialogStage.close();
    }

    public boolean isBtnConfirmClicked() {
        return btnConfirmClicked;
    }

    public void setBtnConfirmClicked(boolean btnConfirmClicked) {
        this.btnConfirmClicked = btnConfirmClicked;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public Stage getPrevStage() {
        return prevStage;
    }

    public void setPrevStage(Stage prevStage) {
        this.prevStage = prevStage;
    }
}
