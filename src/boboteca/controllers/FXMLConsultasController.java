package boboteca.controllers;

import boboteca.utils.Route;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.fxml.FXMLLoader.load;

public class FXMLConsultasController implements Initializable {
    @FXML
    public Tab taxTab;
    @FXML
    public Tab loanTab;
    @FXML
    public Tab bookingTab;
    @FXML
    public AnchorPane loanHolderPane;
    @FXML
    public AnchorPane taxHolderPane;
    @FXML
    public AnchorPane bookingHolderPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            loanHolderPane.getChildren().setAll((Node) load(getClass().getResource(Route.LOANSEARCH)));
            taxHolderPane.getChildren().setAll((Node) load(getClass().getResource(Route.TAXSEARCH)));
            bookingHolderPane.getChildren().setAll((Node) load(getClass().getResource(Route.BOOKINGSEARCH)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
