package boboteca.controllers;

import boboteca.model.Book;
import boboteca.proxy.*;
import boboteca.utils.Route;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static javafx.fxml.FXMLLoader.load;

public class FXMLHomeController implements Initializable {
    @FXML
    public JFXButton booksBtn;
    @FXML
    public AnchorPane holderPane;
    @FXML
    public JFXButton taxBtn;
    @FXML
    public JFXButton loansBtn;
    @FXML
    public JFXButton usersBtn;
    private Boolean isBtnLogoutClicked;
    private Stage stage;
    private ProxyBookDAO bookDAO = ProxyBookDAO.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Preferences userPreferences = Preferences.userRoot();
        String info = userPreferences.get("logged","");
        if(!info.equals("true")){
            loansBtn.setVisible(false);
            usersBtn.setVisible(false);
        }
        try {
            changePanel(Route.BOOKVIEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changePanel(String path) throws IOException {
        for (Node node:holderPane.getChildren()) {
           Node table = node.lookup("#tableLoan");
           if(table != null) {
               for (Object book:((TableView) table).getItems()) {
                   ((Book) book).setDisponibility(true);
                   bookDAO.updateBook((Book) book);
               }
           }
        }
        holderPane.getChildren().removeAll();
        AnchorPane a = load(getClass().getResource(path));
        holderPane.getChildren().setAll(a);
    }

    public void openBookPanel() throws IOException {
        changePanel(Route.BOOKVIEW);
    }

    public void openTaxPanel() throws IOException {
        changePanel(Route.TAXVIEW);
    }

    public void openLoansPanel() throws IOException {
        changePanel(Route.LOANVIEW);
    }

    public void openUsersPanel() throws IOException {
        changePanel(Route.USERVIEW);
    }

    Boolean getBtnLogoutClicked() {
        return isBtnLogoutClicked;
    }

    public void logout() {
        this.isBtnLogoutClicked = true;
        ProxyBookDAO.getInstance().setCache(null);
        ProxyBookingDAO.getInstance().setCache(null);
        ProxyUserDAO.getInstance().setCache(null);
        ProxyTaxDAO.getInstance().setCache(null);
        ProxyLoanDAO.getInstance().setCache(null);
        stage.close();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
