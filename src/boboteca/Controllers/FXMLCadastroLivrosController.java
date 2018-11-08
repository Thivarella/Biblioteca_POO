package boboteca.Controllers;

import boboteca.Comandos.LoadTableViewBooks;
import boboteca.DAO.BookDAO;
import boboteca.DAO.GenericDAO;
import boboteca.Model.Book;
import boboteca.Model.Generic;
import boboteca.Utils.Utils;
import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static boboteca.Comandos.updateTable.update;

public class FXMLCadastroLivrosController implements Initializable {
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
    public TableColumn bookRemove;
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

    private BookDAO bookDAO = new BookDAO();
    private LoadTableViewBooks loadBooks = new LoadTableViewBooks();

    private List<Book> bookList = bookDAO.findAllBooks("",null);
    private List<Generic> priorityList = new GenericDAO().getPriority();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTable();
        saveBtn.setText("Novo");
        loadComboboxPriority();

        searchText.textProperty().addListener(obs->{
            loadTable();
        });

        bookTableView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) ->
                selectBookTableView((Book) newValue)));

    }

    private void loadTable() {
        loadBooks.loadTableViewBooks(searchText.getText(),(searchBookByName.isSelected())?"name":(searchBookByAuthor.isSelected())?"author":(searchBookByCategory.isSelected())?"category":null,bookId,bookName,bookAuthor,bookCategory,bookYear,bookDisponibility,bookList,availableOnly,bookTableView,bookRemove);
    }

    private void loadComboboxPriority() {
        bookPrioritySelect.valueProperty().setValue(null);
        bookPrioritySelect.setItems(FXCollections.observableArrayList(priorityList));
    }

    private void selectBookTableView(Book book) {
        if (book != null) {
            saveBtn.setText("Novo");
            bookPane.setDisable(true);
            updateBtn.setDisable(false);
            txtBookCode.setText(String.valueOf(book.getId()));
            txtBookName.setText(book.getName());
            txtBookAuthor.setText(book.getAuthor());
            txtBookCategory.setText(book.getCategory());
            txtBookYear.setText(book.getYear());
            bookDisponibilityCheck.setSelected(book.getDisponibility());
            bookPrioritySelect.getSelectionModel().select(priorityList.get(book.getPriority().getId()-1));
        } else {
            clearFields();
        }
    }

    private void clearFields() {
        loadComboboxPriority();
        txtBookCode.setText("");
        txtBookName.setText("");
        txtBookAuthor.setText("");
        txtBookCategory.setText("");
        txtBookYear.setText("");
        bookDisponibilityCheck.setSelected(false);
    }

    public void filterAvailables(ActionEvent actionEvent) {
        loadTable();
    }

    public void saveBook(ActionEvent actionEvent) {
        Boolean updates = false;
        if(saveBtn.getText().equals("Novo")){
            bookTableView.getSelectionModel().clearSelection();
            bookPane.setDisable(false);
            saveBtn.setText("Inserir");
            updateBtn.setDisable(true);
            txtBookCode.setText(String.valueOf(Utils.getMaxId("books")));
        }else if (saveBtn.getText().equals("Inserir")){
            Book book = getBook();
            bookDAO.insertBook(book);
            bookList.add(book);
            updates = true;
        } else if (saveBtn.getText().equals("Salvar")){
            Book book = getBook();
            bookDAO.updateBook(book);
            updateBtn.setText("Alterar");
            updateBtn.setDisable(true);
            bookList = bookDAO.findAllBooks("","");
            updates = true;
        }
        if(updates){
            clearFields();
            loadTable();
            saveBtn.setText("Novo");
            bookPane.setDisable(true);
        }
    }

    private Book getBook() {
        return new Book(Integer.valueOf(txtBookCode.getText()), txtBookName.getText(), txtBookAuthor.getText(), txtBookCategory.getText(), txtBookYear.getText(), (Generic) bookPrioritySelect.getSelectionModel().getSelectedItem(), bookDisponibilityCheck.isSelected());
    }

    public void updateBook(ActionEvent actionEvent) {
        update(updateBtn, bookPane, saveBtn, bookTableView);
    }
}
