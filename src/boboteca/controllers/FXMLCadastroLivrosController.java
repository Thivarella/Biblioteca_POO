package boboteca.controllers;

import boboteca.alerts.AlertsController;
import boboteca.comandos.LoadTableViewBooks;
import boboteca.dao.GenericDAO;
import boboteca.model.Book;
import boboteca.model.Booking;
import boboteca.model.Generic;
import boboteca.model.User;
import boboteca.proxy.ProxyBookDAO;
import boboteca.proxy.ProxyBookingDAO;
import boboteca.proxy.ProxyUserDAO;
import boboteca.utils.Utils;
import boboteca.utils.Validate;
import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static boboteca.comandos.OpenExportModal.openExportModal;
import static boboteca.comandos.UpdateTable.update;
import static boboteca.utils.Utils.*;

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
    public HBox bookBtnPane;
    @FXML
    public AnchorPane bookingPanel;
    @FXML
    public JFXButton bookingBtn;
    @FXML
    public JFXButton exportBook;
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

    private ProxyBookDAO bookDAO = ProxyBookDAO.getInstance();
    private ProxyBookingDAO bookingDAO = ProxyBookingDAO.getInstance();
    private ProxyUserDAO userDAO = ProxyUserDAO.getInstance();
    private LoadTableViewBooks loadBooks = new LoadTableViewBooks();

    private Book bookToBooking = new Book();
    private List<Book> bookList = bookDAO.findAllBooks("",null);
    private List<Generic> priorityList = new GenericDAO().getPriority();
    private Preferences userPreferences = Preferences.userRoot();
    private String info = userPreferences.get("logged","");
    private List<JFXTextField> jfxTextFields = new ArrayList<>();
    private AlertsController alerts = new AlertsController();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTable();
        saveBtn.setText("Novo");
        loadComboboxPriority();
        JfxTexts(jfxTextFields, txtBookCode, txtBookName, txtBookAuthor, txtBookCategory, txtBookYear);

        if(!info.equals("true")){
            bookPane.setVisible(false);
            bookBtnPane.setVisible(false);
            bookingPanel.setVisible(true);
            bookRemove.setVisible(false);
            exportBook.setVisible(false);
            bookName.setPrefWidth(bookName.getPrefWidth()+39);
        }

        searchText.textProperty().addListener(obs -> loadTable());

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
            bookToBooking = book;
            saveBtn.setText("Novo");
            bookPane.setDisable(true);
            updateBtn.setDisable(false);
            if(!book.getDisponibility()) {
                bookingBtn.setDisable(false);
            }else{
                bookingBtn.setDisable(true);
            }
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
        clearingFields(jfxTextFields);
        bookDisponibilityCheck.setSelected(false);
    }


    public void filterAvailables() {
        loadTable();
    }

    public void saveBook() throws IOException {
        boolean updates = false;
        switch (saveBtn.getText()) {
            case "Novo":
                bookTableView.getSelectionModel().clearSelection();
                bookPane.setDisable(false);
                updateBtn.setText("Cancelar");
                saveBtn.setText("Inserir");
                updateBtn.setDisable(false);
                txtBookCode.setText(String.valueOf(Utils.getMaxId("books")));
                break;
            case "Inserir":
                if (Validate.validateBook(txtBookName, txtBookAuthor, txtBookCategory, txtBookYear, bookPrioritySelect)) {
                    Book book = getBook();
                    bookDAO.insertBook(book);
                    updateBtn.setText("Alterar");
                    bookList.add(book);
                    updates = true;
                }
                break;
            case "Salvar":
                if (Validate.validateBook(txtBookName, txtBookAuthor, txtBookCategory, txtBookYear, bookPrioritySelect)) {
                    Book book = getBook();
                    bookDAO.updateBook(book);
                    updateBtn.setText("Alterar");
                    updateBtn.setDisable(true);
                    bookList = bookDAO.findAllBooks("", "");
                    updates = true;
                }
                break;
        }
        if (updates) {
            clearFields();
            loadTable();
            saveBtn.setText("Novo");
            updateBtn.setDisable(true);
            bookPane.setDisable(true);
        }
    }

    private Book getBook() {
        return new Book(Integer.valueOf(txtBookCode.getText()), txtBookName.getText(), txtBookAuthor.getText(), txtBookCategory.getText(), txtBookYear.getText(), (Generic) bookPrioritySelect.getSelectionModel().getSelectedItem(), bookDisponibilityCheck.isSelected());
    }

    public void updateBook() {
        if (updateBtn.getText().equals("Cancelar")) {
            updateBtn.setText("Alterar");
            updateBtn.setDisable(true);
            saveBtn.setText("Novo");
            bookPane.setDisable(true);
            clearFields();
        } else {
            update(updateBtn, bookPane, saveBtn, bookTableView);
        }
    }

    public void bookingBook() throws IOException {
        User user = userDAO.findUserById(Integer.valueOf(userPreferences.get("userId", "")));
        if(bookingDAO.findBookAlreadyBooked(user.getId(),bookToBooking.getId()) <= 0) {
            Booking booking = new Booking(getMaxId("booking"), bookToBooking, user, getToday(), true);
            bookingDAO.insertBooking(booking);
            alerts.showMessage("Livro reservado com sucesso!","alert-success",false,null,null);
        }else{
            alerts.showMessage("Você já reservou este livro!","alert-danger",false,null,null);
        }
    }
    public void exportBook() throws IOException {
        openExportModal("Qual o formato de arquivo desejado para exportar o relatório de livros?",2);
    }

    public void check() {
        Utils.numericField(txtBookYear);
        Utils.maxField(txtBookYear,4);
    }
}
