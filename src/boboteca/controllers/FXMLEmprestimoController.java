package boboteca.controllers;

import boboteca.alerts.AlertsController;
import boboteca.dao.UserDAO;
import boboteca.model.Book;
import boboteca.model.Loan;
import boboteca.model.User;
import boboteca.proxy.ProxyBookDAO;
import boboteca.proxy.ProxyBookingDAO;
import boboteca.proxy.ProxyLoanDAO;
import boboteca.proxy.ProxyUserDAO;
import boboteca.utils.Route;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import com.sun.prism.impl.Disposer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import static boboteca.comandos.OpenConfirmModal.openConfirmPanel;
import static boboteca.utils.Utils.*;

public class FXMLEmprestimoController implements Initializable {
    @FXML
    public JFXButton removeAllBooksBtn;
    @FXML
    private JFXTextField bookCode;
    @FXML
    private JFXTextField bookName;
    @FXML
    private JFXTextField bookAuthor;
    @FXML
    private JFXButton addBookBtn;
    @FXML
    private TableView tableLoan;
    @FXML
    private TableColumn columnBookIdLoan;
    @FXML
    private TableColumn columnBookNameLoan;
    @FXML
    private TableColumn columnBookRemove;
    @FXML
    private JFXTextField userCodeLoan;
    @FXML
    private JFXTextField userNameLoan;
    @FXML
    private JFXButton generateLoanBtn;
    @FXML
    private JFXTextField returnDate;
    @FXML
    private JFXTextField loanNumber;
    @FXML
    private JFXTextField userCodeReturn;
    @FXML
    private JFXTextField userNameReturn;
    @FXML
    private TableView returnTable;
    @FXML
    private TableColumn returnCheckBook;
    @FXML
    private TableColumn bookIdReturn;
    @FXML
    private TableColumn bookNameReturn;
    @FXML
    private TableColumn bookReturnDate;
    @FXML
    private JFXButton returnBookBtn;
    @FXML
    private JFXButton renewLoanBtn;

    private List<Book> bookList = new ArrayList<>();
    private Book book = new Book();
    private User userLoan = new User();
    private ProxyBookDAO bookDAO = ProxyBookDAO.getInstance();

    private List<Loan> loanList = new ArrayList<>();
    private User userReturn = new User();
    private ProxyUserDAO userDAO = ProxyUserDAO.getInstance();
    private ProxyLoanDAO loanDAO = ProxyLoanDAO.getInstance();
    private ProxyBookingDAO bookingDAO = ProxyBookingDAO.getInstance();
    private AlertsController alerts = new AlertsController();
    private Book reservedBook = new Book();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public static Boolean openTaxesPanel(List<Loan> loanToTax) throws IOException {
        Stage dialogStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(FXMLTaxGeneratedController.class.getResource(Route.CONFIRMTAXES));
        AnchorPane parent = fxmlLoader.load();
        Scene scene = new Scene(parent);

        FXMLTaxGeneratedController controller = fxmlLoader.getController();
        dialogStage.setScene(scene);
        dialogStage.setResizable(false);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        controller.setDialogStage(dialogStage);
        controller.setLoansToTax(loanToTax);
        dialogStage.showAndWait();
        return controller.isBtnConfirmClicked();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addBookBtn.setDisable(true);

        bookCode.textProperty().addListener(obs -> {
            if (bookCode.getText().length() <= 0) {
                bookName.setText("");
                bookAuthor.setText("");
                addBookBtn.setDisable(true);
            } else {
                book = bookDAO.findBookByIdAndDisponibilityTrue(Integer.valueOf(bookCode.getText()));
                if (book != null && book.getId() != null) {
                    bookName.setText(book.getName());
                    bookAuthor.setText(book.getAuthor());
                    addBookBtn.setDisable(false);
                }else {
                    bookName.setText("");
                    bookAuthor.setText("");
                    addBookBtn.setDisable(true);
                }
            }
        });

        userCodeLoan.textProperty().addListener(obs -> {
            if (userCodeLoan.getText().length() <= 0) {
                userNameLoan.setText("");
            } else {
                userLoan = userDAO.findUserById(Integer.valueOf(userCodeLoan.getText()));
                if (userLoan != null) {
                    userNameLoan.setText(userLoan.getName());
                } else {
                    userNameLoan.setText("");
                }
            }
        });

        userCodeReturn.textProperty().addListener(obs -> {
            if (userCodeReturn.getText().length() <= 0) {
                userNameReturn.setText("");
                loanList.clear();
                userReturn = null;
                loadTableReturn();
            } else {
                userReturn = userDAO.findUserById(Integer.valueOf(userCodeReturn.getText()));
                if (userReturn != null) {
                    userNameReturn.setText(userReturn.getName());
                    loanList = loanDAO.findAllLoanByUserId(userReturn.getId());
                    loadTableReturn();
                }
            }
        });
    }

    public void addBook(ActionEvent actionEvent) {
        if (bookCode.getText().length() > 0 && book != null) {
            bookList.add(book);
            book.setDisponibility(false);
            bookDAO.updateBook(book);
            loadTableLoan();
            clearFieldBooks();
        }
    }

    private void clearFieldBooks() {
        bookCode.setText("");
        bookName.setText("");
        bookAuthor.setText("");
        bookCode.requestFocus();
    }

    private void loadTableLoan() {
        columnBookIdLoan.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnBookNameLoan.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnBookRemove.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Disposer.Record, Boolean>, ObservableValue<Boolean>>) p -> new SimpleBooleanProperty(p.getValue() != null));

        columnBookRemove.setCellFactory(
                (Callback<TableColumn<Disposer.Record, Boolean>, TableCell<Disposer.Record, Boolean>>) p -> new ButtonCell(bookList));

        ObservableList<Book> bookObservableList = FXCollections.observableArrayList(bookList);

        tableLoan.setItems(bookObservableList);
    }

    private void loadTableReturn() {
        bookIdReturn.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Loan, String>, ObservableValue<String>>) param -> {
            final Loan loan = param.getValue();
            return new SimpleObjectProperty<>(loan.getBook().getId().toString());
        });
        bookNameReturn.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Loan, String>, ObservableValue<String>>) param -> {
            final Loan loan = param.getValue();
            return new SimpleObjectProperty<>(loan.getBook().getName());
        });
        bookReturnDate.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Loan, String>, ObservableValue<String>>) param -> {
            final Loan loan = param.getValue();
            return new SimpleObjectProperty<>(dateFormat.format(loan.getReturnDate()));
        });


        returnCheckBook.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Disposer.Record, Boolean>, ObservableValue<Boolean>>) p -> new SimpleBooleanProperty(p.getValue() != null));

        returnCheckBook.setCellFactory(
                (Callback<TableColumn<Disposer.Record, Boolean>, TableCell<Disposer.Record, Boolean>>) p -> new CheckBoxCell(loanList));

        ObservableList<Loan> bookObservableList = FXCollections.observableArrayList(loanList);

        returnTable.setRowFactory(new Callback<TableView<Loan>, TableRow<Loan>>() {
            @Override
            public TableRow<Loan> call(TableView<Loan> param) {
                return new TableRow<Loan>() {
                    @Override
                    protected void updateItem(Loan item, boolean empty) {
                        super.updateItem(item, empty);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new LocalDate().toDate());
                        Date today = calendar.getTime();
                        if (!empty) {
                            if (item.getReturnDate().compareTo(new java.sql.Date(today.getTime())) < 0) {
                                getStyleClass().setAll("out-of-date");
                                getStylesheets().add("bootstrap.css");
                            } else {
                                setTextFill(Color.BLACK);
                            }

                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        returnTable.setItems(bookObservableList);
    }

    public void generateLoan(ActionEvent actionEvent) throws IOException {
        if (!bookList.isEmpty()) {
            if(bookIsReserved(bookList,userLoan)) {
                if (userTaxesStillOpen(userLoan) <= 0) {
                    if (openConfirmPanel("Deseja realmente finalizar este empréstimo?")) {
                        for (Book book : bookList) {
                            bookingDAO.findBookingByBookIdAndStatusTrue(book.getId());
                            Loan loan = mountLoan(book, userLoan);
                            loanDAO.insertLoan(loan);
                            loanDAO.findAllLoan();
                        }
                        clearFieldsUserLoan();
                        bookList.clear();
                        loadTableLoan();
                    }
                } else {
                    alerts.showMessage("Este usuário possui multas pendentes de pagamento", "alert-danger", true, null, null);
                }
            }else {
                alerts.showMessage("O livro " + reservedBook.getName() + " está reservado para outra pessoa", "alert-danger", true, null, null);
            }
        }
    }

    private boolean bookIsReserved(List<Book> bookList, User userLoan) {
        for (Book book: bookList) {
            if(bookingDAO.findReservedBook(book.getId(),userLoan.getId()) > 0){
                return false;
            }
        }
        return true;
    }

    private Integer userTaxesStillOpen(User userLoan) {
        return new UserDAO().getTaxesCountByUser(userLoan.getId());
    }

    private void clearFieldsUserLoan() {
        userCodeLoan.setText("");
        userNameLoan.setText("");
    }

    private Loan mountLoan(Book book, User user) {
        Loan loan = new Loan(
                getMaxId("loans"),
                book,
                user,
                getToday(),
                getReturnDate(user.getCategory().getValue()),
                false
        );
        return loan;
    }

    public void returnBook(ActionEvent actionEvent) throws IOException {
        List<Loan> loanToReturn = new ArrayList<>();
        List<Loan> loanToTax = new ArrayList<>();
        for (Loan loan : loanList) {
            if (loan.getChecked() && loan.getReturnDate().compareTo(getToday()) >= 0) {
                loanToReturn.add(loan);
            } else if (loan.getChecked() && loan.getReturnDate().compareTo(getToday()) < 0) {
                loanToTax.add(loan);
            }
        }

        for (Loan loan : loanToReturn) {
            loanDAO.doBookReturn(loan);
            loanList.remove(loan);
            loan.getBook().setDisponibility(true);
            bookDAO.updateBook(loan.getBook());
        }

        if (!loanToTax.isEmpty()) {
            if (openConfirmPanel("Existem um ou mais livros com devolução em atraso.\n Deseja gerar a multa agora?")) {
                if (openTaxesPanel(loanToTax)) {
                    loanList = loanDAO.findAllLoanByUserId(loanToTax.get(0).getUser().getId());
                }
            }
        }
        loadTableReturn();
    }

    public void renewLoan(ActionEvent actionEvent) throws IOException {
        for (Loan loan : loanList) {
            if (loan.getChecked() && loan.getReturnDate().compareTo(getToday()) >= 0) {
                loanDAO.doBookRenew(loan);
            }else{
                alerts.showMessage("Um ou mais livros estão em atraso e não podem ser renovados","alert-danger",false,null,null);
            }
        }
        loanList = loanDAO.findAllLoanByUserId(Integer.valueOf(userCodeReturn.getText()));
        loadTableReturn();
    }

    public void removeAllBooks(ActionEvent actionEvent) throws IOException {
        if (!bookList.isEmpty()) {
            if (openConfirmPanel("Deseja realmente retirar todos os livros?")) {
                for (Book b : bookList) {
                    b.setDisponibility(true);
                    bookDAO.updateBook(b);
                }
                bookList.clear();
                loadTableLoan();
            }
        }
    }

    public class ButtonCell extends TableCell<Disposer.Record, Boolean> {
        final JFXButton cellButton = new JFXButton("X");

        public ButtonCell(List<Book> bookList) {
            cellButton.getStyleClass().addAll("btn-danger");
            cellButton.setOnAction(t -> {
                Book currentBook = (Book) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                try {
                    if (openConfirmPanel("Deseja realmente retirar " + currentBook.getName() + " da lista?")) {
                        bookList.remove(currentBook);
                        currentBook.setDisponibility(true);
                        bookDAO.updateBook(currentBook);
                        loadTableLoan();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) {
                setGraphic(cellButton);
            }
        }
    }

    public class CheckBoxCell extends TableCell<Disposer.Record, Boolean> {
        final JFXCheckBox checkBoxCell = new JFXCheckBox();

        public CheckBoxCell(List<Loan> loanList) {
            checkBoxCell.setOnAction(t -> {
                Loan currentLoan = (Loan) CheckBoxCell.this.getTableView().getItems().get(CheckBoxCell.this.getIndex());
                currentLoan.setChecked(true);
            });
        }

        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) {
                setGraphic(checkBoxCell);
            }
        }
    }
}
