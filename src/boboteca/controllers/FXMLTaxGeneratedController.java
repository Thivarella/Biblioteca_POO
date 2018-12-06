package boboteca.controllers;

import boboteca.model.Loan;
import boboteca.model.Tax;
import boboteca.proxy.ProxyBookDAO;
import boboteca.proxy.ProxyLoanDAO;
import boboteca.proxy.ProxyTaxDAO;
import boboteca.utils.Utils;
import com.jfoenix.controls.JFXButton;
import com.sun.prism.impl.Disposer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.joda.time.LocalDate;
import org.joda.time.Period;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static boboteca.comandos.OpenConfirmModal.openConfirmPanel;
import static boboteca.utils.Utils.getMaxId;
import static boboteca.utils.Utils.getToday;

public class FXMLTaxGeneratedController implements Initializable {
    @FXML
    private TableView taxTable;
    @FXML
    private TableColumn taxId;
    @FXML
    private TableColumn taxDescription;
    @FXML
    private TableColumn taxValue;
    @FXML
    private TableColumn taxBtn;
    @FXML
    private JFXButton payAllBtn;
    @FXML
    private JFXButton payLaterBtn;

    private List<Loan> loanList = new ArrayList<>();
    private List<Tax> taxList = new ArrayList<>();
    private Boolean btnConfirmClicked = false;
    private Stage dialogStage;
    private ProxyTaxDAO taxDAO = ProxyTaxDAO.getInstance();
    private ProxyBookDAO bookDAO = ProxyBookDAO.getInstance();
    private ProxyLoanDAO loanDAO = ProxyLoanDAO.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    private void loadTable() {
        taxId.setCellValueFactory(new PropertyValueFactory<>("id"));
        taxDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        taxDescription.setCellFactory(tc -> {
            TableCell<Tax, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(taxDescription.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        taxValue.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Tax, String>, ObservableValue<String>>) param -> {
            final Tax tax = param.getValue();
            return new SimpleObjectProperty<>(Utils.formatMoney(tax.getValue()));
        });

        taxBtn.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Disposer.Record, Boolean>, ObservableValue<Boolean>>) p -> new SimpleBooleanProperty(p.getValue() != null));

        taxBtn.setCellFactory(
                (Callback<TableColumn<Disposer.Record, Boolean>, TableCell<Disposer.Record, Boolean>>) p -> new ButtonCell(taxList));

        taxTable.setItems(FXCollections.observableArrayList(taxList));
    }

    public void payAll(ActionEvent actionEvent) {
        for (Tax tax: taxList) {
            updatePaidTax(tax, true);
        }
        taxList.clear();
        setBtnConfirmClicked(true);
        dialogStage.close();
    }

    private void updatePaidTax(Tax tax, Boolean isPaid) {
        tax.setPaid(isPaid);
        taxDAO.insertTax(tax);
        tax.getBook().setDisponibility(true);
        bookDAO.updateBook(tax.getBook());
        tax.getLoan().setReturnedDate(getToday());
        tax.getLoan().setChecked(true);
        loanDAO.doBookReturn(tax.getLoan());
    }

    public void payLater(ActionEvent actionEvent) {
        for (Tax tax: taxList) {
            updatePaidTax(tax,false);
        }
        taxList.clear();
        setBtnConfirmClicked(true);
        dialogStage.close();
    }

    public void setLoansToTax(List<Loan> loanList) {
        this.loanList = loanList;
        Integer id = getMaxId("taxes");
        for (Loan loan:loanList) {
            Period period = new Period(LocalDate.now(), new LocalDate(String.valueOf(loan.getReturnDate())));
            Integer days = Math.abs(period.getDays());
            taxList.add(new Tax(
                    id,
                    loan.getBook(),
                    loan.getUser(),
                    loan,
                    "Multa referente ao atraso de " + days + " dias do livro " + loan.getBook().getName(),
                    getValue(loan, days),
                    false
            ));
            id++;
        }
        loadTable();
    }

    private Double getValue(Loan loan, Integer days) {
        return Double.valueOf((loan.getBook().getPriority().getId() + loan.getUser().getCategory().getAdditionalValue()) * days);
    }

    public List<Loan> getLoansList() {
        return loanList;
    }

    public Boolean isBtnConfirmClicked() {
        return btnConfirmClicked;
    }

    public void setBtnConfirmClicked(Boolean btnConfirmClicked) {
        this.btnConfirmClicked = btnConfirmClicked;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public class ButtonCell extends TableCell<Disposer.Record, Boolean> {
        final JFXButton cellButton = new JFXButton("$");

        public ButtonCell(List<Tax> taxList){
            cellButton.getStyleClass().addAll("btn-success");
            cellButton.setOnAction(t -> {
                Tax currentTax = (Tax) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                try {
                    if(openConfirmPanel("Deseja pagar a multa do livro " + currentTax.getBook().getName() + "?")){
                        updatePaidTax(currentTax, true);
                        taxList.remove(currentTax);
                        loadTable();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                setGraphic(cellButton);
            }
        }
    }
}
