package boboteca.Controllers;

import boboteca.DAO.BookDAO;
import boboteca.Model.Book;
import boboteca.Model.Generic;
import boboteca.Utils.Route;
import boboteca.Utils.Utils;
import com.jfoenix.controls.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public AnchorPane bookPane;
    @FXML
    public JFXTextField txtBookCode;
    @FXML
    public JFXTextField txtBookName;
    @FXML
    public JFXTextField txtBookAuthor;
    @FXML
    public JFXTextField txtBookCategory;
    @FXML
    public JFXTextField txtBookYear;
    @FXML
    public JFXCheckBox bookDisponibilityCheck;
    @FXML
    public JFXComboBox bookPrioritySelect;
    @FXML
    public JFXButton saveBtn;
    @FXML
    public JFXButton updateBtn;
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

    private List<Generic> priorityList = new ArrayList<>();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTableViewBooks("","");
        saveBtn.setText("Novo");
        priorityList.add(new Generic(1,"1"));
        priorityList.add(new Generic(2,"2"));
        priorityList.add(new Generic(3,"3"));
        bookPrioritySelect.setItems(FXCollections.observableArrayList(priorityList));
        searchText.textProperty().addListener(obs->{
            if(searchText.getText().length() <= 0) {
                loadTableViewBooks("","");
            }
            else{
                loadTableViewBooks(searchText.getText(),(searchBookByName.isSelected())?"name":(searchBookByAuthor.isSelected())?"author":(searchBookByCategory.isSelected())?"category":null);
            }
        });
    }

    private void loadTableViewBooks(String search,String filter) {
        bookId.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookName.setCellValueFactory(new PropertyValueFactory<>("name"));
        bookAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        bookCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        bookYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        bookDisponibility.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Book, String>, ObservableValue<String>>) param -> {
            final Book book = param.getValue();
            return new SimpleObjectProperty<>((book.getDisponibility())?"Sim":"Não");
        });


        String[] parts = search.toUpperCase().split(" ");

        ObservableList<Book> bookObservableList = FXCollections.observableArrayList();
        for (Book b: bookList) {
            if(b.getDisponibility() && availableOnly.isSelected()){
                mountBookList(filter, parts, bookObservableList, b);
            }else if (!availableOnly.isSelected()){
                mountBookList(filter, parts, bookObservableList, b);
            }
        }

        bookTableView.setItems(bookObservableList);
    }

    private void mountBookList(String filter, String[] parts, ObservableList<Book> bookObservableList, Book b) {
        boolean match = true;
        String entryText = "";
        switch (filter){
            case "name":
                entryText = b.getName();
                break;
            case "author":
                entryText = b.getAuthor();
                break;
            case "category":
                entryText = b.getCategory();
                break;
            default:
                break;
        }
        for (String part: parts) {
            if (!entryText.toUpperCase().contains(part)) {
                match = false;
                break;
            }
        }

        if (match) {
            bookObservableList.add(b);
        }
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
            loadTableViewBooks("","");
        }
        else{
            loadTableViewBooks(searchText.getText(),(searchBookByName.isSelected())?"name":(searchBookByAuthor.isSelected())?"author":(searchBookByCategory.isSelected())?"category":null);
        }
    }

    public void saveBook(ActionEvent actionEvent) {
        if(saveBtn.getText().equals("Novo")){
            bookPane.setDisable(false);
            saveBtn.setText("Inserir");
            txtBookCode.setText(String.valueOf(Utils.getMaxId("books")));
        }else if (saveBtn.getText().equals("Inserir")){
            Book book = new Book(Integer.valueOf(txtBookCode.getText()),txtBookName.getText(),txtBookAuthor.getText(),txtBookCategory.getText(),txtBookYear.getText(),bookPrioritySelect.getSelectionModel().getSelectedIndex(),bookDisponibilityCheck.isSelected());
            new BookDAO().insertBook(book);
            bookList.add(book);
            loadTableViewBooks("","");
        }
    }

    public void updateBook(ActionEvent actionEvent) {
    }
}
