package boboteca.controllers;

import boboteca.dao.UserDAO;
import boboteca.model.User;
import boboteca.proxy.*;
import boboteca.utils.Route;
import boboteca.utils.Validate;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class FXMLLoginController implements Initializable {
    @FXML
    public JFXButton btnCancel;
    @FXML
    public JFXButton btnLogin;
    @FXML
    public ProgressBar progress;
    @FXML
    private JFXTextField cpfTextLogin;
    @FXML
    private JFXPasswordField passwordTextLogin;

    private Stage dialogStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void cancelLogin() {
        dialogStage.close();
    }

    private void tryLogin() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(FXMLHomeController.class.getResource(Route.MAINVIEW));
        AnchorPane parent = fxmlLoader.load();
        Scene scene = new Scene(parent);
        Stage stage = new Stage();

        FXMLHomeController controller = fxmlLoader.getController();
        stage.setScene(scene);
        stage.setTitle("Boboteca");
        stage.setResizable(false);
        dialogStage.hide();
        controller.setStage(stage);
        stage.showAndWait();
        if(controller.getBtnLogoutClicked()){
            cpfTextLogin.clear();
            passwordTextLogin.clear();
            dialogStage.show();
            cpfTextLogin.requestFocus();
        }
    }

    public void validLogin() throws IOException {
        if(Validate.validLogin(cpfTextLogin, passwordTextLogin,false)) {
            User user = new UserDAO().Login(cpfTextLogin.getText(), passwordTextLogin.getText());
            if (user.getId() != null) {
                Preferences userPreference = Preferences.userRoot();
                userPreference.put("logged", user.getLibrarian().toString());
                userPreference.put("userId", user.getId().toString());

                if (openLoader(user.getLibrarian(), user.getId())) {
                    tryLogin();
                }
            }else{
                Validate.validLogin(cpfTextLogin, passwordTextLogin,true);
            }
        }
    }

    private boolean openLoader(Boolean librarian, Integer id) {
        ProxyBookDAO bookDAO = ProxyBookDAO.getInstance();
        ProxyTaxDAO taxDAO = ProxyTaxDAO.getInstance();
        ProxyLoanDAO loanDAO = ProxyLoanDAO.getInstance();
        ProxyUserDAO userDAO = ProxyUserDAO.getInstance();
        ProxyBookingDAO bookingDAO = ProxyBookingDAO.getInstance();

        bookDAO.findAllBooks("", "");
        if (!librarian) {
            taxDAO.findAllTaxByUserId(id, null);
            loanDAO.findAllLoanByUserId(id);
            bookingDAO.findAllBookingByUserId(id);
        } else {
            taxDAO.findAllTax();
            loanDAO.findAllLoan();
            bookingDAO.findAllBooking();
        }
        userDAO.findAllUsers();
        return true;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

}
