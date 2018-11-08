package boboteca.Controllers;

import boboteca.Utils.Route;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.fxml.FXMLLoader.load;

public class FXMLHomeController implements Initializable {
    @FXML
    public JFXButton booksBtn;
    @FXML
    public AnchorPane holderPane;
    public JFXButton taxBtn;
    public JFXButton loansBtn;
    public JFXButton usersBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            changePanel(Route.BOOKVIEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changePanel(String path) throws IOException {
        holderPane.getChildren().removeAll();
        AnchorPane a = load(getClass().getResource(path));
        holderPane.getChildren().setAll(a);
    }

    public void openBookPanel(ActionEvent actionEvent) throws IOException {
        changePanel(Route.BOOKVIEW);
    }

    public void openTaxPanel(ActionEvent actionEvent) throws IOException {
        changePanel(Route.TAXVIEW);
    }

    public void openLoansPanel(ActionEvent actionEvent) throws IOException {
        changePanel(Route.LOANVIEW);
    }

    public void openUsersPanel(ActionEvent actionEvent) throws IOException {
        changePanel(Route.USERVIEW);
    }
}
