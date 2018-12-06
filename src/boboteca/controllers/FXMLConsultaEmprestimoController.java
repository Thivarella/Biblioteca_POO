package boboteca.controllers;

import boboteca.model.Loan;
import boboteca.proxy.ProxyLoanDAO;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static boboteca.comandos.OpenExportModal.openExportModal;

public class FXMLConsultaEmprestimoController implements Initializable {
    @FXML
    public JFXTextField searchLoan;
    @FXML
    public JFXRadioButton loanByBookId;
    @FXML
    public ToggleGroup loanTypeSearch;
    @FXML
    public JFXRadioButton loanByBookName;
    @FXML
    public JFXRadioButton loanByUserId;
    @FXML
    public JFXRadioButton loanByUserName;
    @FXML
    public JFXButton exportLoan;
    @FXML
    public TableView loanTable;
    @FXML
    public TableColumn loanTableUserCode;
    @FXML
    public TableColumn loanTableUserName;
    @FXML
    public TableColumn loanTableBookCode;
    @FXML
    public TableColumn loanTableBookName;
    @FXML
    public TableColumn loanTableBookReturnDate;
    @FXML
    public TableColumn loanTableBookIsReturned;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private ProxyLoanDAO loanDAO = ProxyLoanDAO.getInstance();
    private List<Loan> loanList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Preferences userPreferences = Preferences.userRoot();
        String info = userPreferences.get("logged","");
        Integer id = Integer.valueOf(userPreferences.get("userId",""));
        if(!info.equals("true")){
            loanTableUserCode.setVisible(false);
            loanByUserName.setVisible(false);
            loanTableUserName.setVisible(false);
            exportLoan.setVisible(false);
            loanByUserId.setVisible(false);
            loanList = loanDAO.findAllLoanByUserId(id);
        }else {
            loanList = loanDAO.findAllLoan();
        }
        searchLoan.textProperty().addListener(obs -> {
            loadTableLoan(searchLoan.getText());
        });

        loadTableLoan("");
    }

    private void loadTableLoan(String search) {
        loanTableUserCode.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Loan, String>, ObservableValue<String>>) param -> {
            final Loan loan = param.getValue();
            return new SimpleObjectProperty<>(loan.getUser().getId().toString());
        });
        loanTableUserName.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Loan, String>, ObservableValue<String>>) param -> {
            final Loan loan = param.getValue();
            return new SimpleObjectProperty<>(loan.getUser().getName());
        });
        loanTableBookCode.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Loan, String>, ObservableValue<String>>) param -> {
            final Loan loan = param.getValue();
            return new SimpleObjectProperty<>(loan.getBook().getId().toString());
        });
        loanTableBookName.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Loan, String>, ObservableValue<String>>) param -> {
            final Loan loan = param.getValue();
            return new SimpleObjectProperty<>(loan.getBook().getName());
        });
        loanTableBookReturnDate.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Loan, String>, ObservableValue<String>>) param -> {
            final Loan loan = param.getValue();
            return new SimpleObjectProperty<>(dateFormat.format(loan.getReturnDate()));
        });
        loanTableBookIsReturned.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Loan, String>, ObservableValue<String>>) param -> {
            final Loan loan = param.getValue();
            return new SimpleObjectProperty<>(loan.getChecked()?"Sim":"Não");
        });

        String[] parts = search.toUpperCase().split(" ");

        ObservableList<Loan> loanObservableList = FXCollections.observableArrayList();
        for (Loan l: loanList) {
            mountLoanList(parts, loanObservableList, l);
        }

        loanTable.setItems(loanObservableList);
    }

    private void mountLoanList(String[] parts, ObservableList<Loan> loanObservableList, Loan l) {
        boolean match = true;
        String entryText;
        if(loanByBookId.isSelected()){
            entryText = l.getBook().getId().toString();
        }else if(loanByBookName.isSelected()){
            entryText = l.getBook().getName();
        }else if(loanByUserId.isSelected()){
            entryText = l.getUser().getId().toString();
        }else{
            entryText = l.getUser().getName();
        }
        for (String part: parts) {
            if (!entryText.toUpperCase().contains(part)) {
                match = false;
                break;
            }
        }

        if (match) {
            loanObservableList.add(l);
        }
    }

    public void exportLoan() throws IOException {
        openExportModal("Qual o formato de arquivo desejado para exportar o relatório de empréstimos?",3);
    }
}
