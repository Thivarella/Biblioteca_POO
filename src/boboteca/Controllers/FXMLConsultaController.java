package boboteca.Controllers;

import boboteca.Comandos.LoadTableViewBooks;
import boboteca.DAO.BookDAO;
import boboteca.Model.Book;
import boboteca.Utils.Route;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLConsultaController implements Initializable {
    @FXML
    public TableView bookTableView;
    @FXML
    public TableColumn bookId;
    @FXML
    public TableColumn bookName;
    @FXML
    public TableColumn bookAuthor;
    @FXML
    public TableColumn bookCategory;
    @FXML
    public TableColumn bookYear;
    @FXML
    public TableColumn bookDisponibility;
    @FXML
    private JFXTextField searchText;
    @FXML
    private JFXRadioButton searchBookByName;
    @FXML
    private JFXRadioButton searchBookByAuthor;
    @FXML
    private JFXRadioButton searchBookByCategory;
    @FXML
    private JFXToggleButton availableOnly;

    private Stage mainStage;
    private boolean btnConfirmClicked = false;

    private List<Book> bookList = new BookDAO().findAllBooks("",null);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new LoadTableViewBooks().loadTableViewBooks("", "", bookId, bookName, bookAuthor, bookCategory, bookYear, bookDisponibility, bookList, availableOnly, bookTableView, null);
        searchText.textProperty().addListener(obs->{
            if(searchText.getText().length() <= 0) {
                new LoadTableViewBooks().loadTableViewBooks("", "", bookId, bookName, bookAuthor, bookCategory, bookYear, bookDisponibility, bookList, availableOnly, bookTableView, null);
            }
            else{
                new LoadTableViewBooks().loadTableViewBooks(searchText.getText(), (searchBookByName.isSelected()) ? "name" : (searchBookByAuthor.isSelected()) ? "author" : (searchBookByCategory.isSelected()) ? "category" : null, bookId, bookName, bookAuthor, bookCategory, bookYear, bookDisponibility, bookList, availableOnly, bookTableView, null);
            }
        });
    }

    public void openLogin(ActionEvent actionEvent) throws IOException {
        if(confirmLogin()){
            btnConfirmClicked = true;
            mainStage.close();
        }else {
            mainStage.close();
        }
    }

    private boolean confirmLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLLoginController.class.getResource(Route.LOGINVIEW));
        AnchorPane page = loader.load();

        Stage dialogStage = new Stage();
        Scene scene = new Scene(page);
//        dialogStage.getIcons().add(new Image());
        dialogStage.setScene(scene);

        FXMLLoginController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setPrevStage(mainStage);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.showAndWait();
        return controller.isBtnConfirmClicked();
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public boolean isBtnConfirmClicked() {
        return btnConfirmClicked;
    }

    public void setBtnConfirmClicked(boolean btnConfirmClicked) {
        this.btnConfirmClicked = btnConfirmClicked;
    }

    public void closeApp(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void filterAvailables(ActionEvent actionEvent) {
        if(searchText.getText().length() <= 0) {
            new LoadTableViewBooks().loadTableViewBooks("", "", bookId, bookName, bookAuthor, bookCategory, bookYear, bookDisponibility, bookList, availableOnly, bookTableView, null);
        }
        else{
            new LoadTableViewBooks().loadTableViewBooks(searchText.getText(), (searchBookByName.isSelected()) ? "name" : (searchBookByAuthor.isSelected()) ? "author" : (searchBookByCategory.isSelected()) ? "category" : null, bookId, bookName, bookAuthor, bookCategory, bookYear, bookDisponibility, bookList, availableOnly, bookTableView, null);
        }
    }

}
