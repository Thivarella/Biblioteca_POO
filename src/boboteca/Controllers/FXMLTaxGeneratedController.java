package boboteca.Controllers;

import boboteca.DAO.BookDAO;
import boboteca.DAO.TaxDAO;
import boboteca.Model.Book;
import boboteca.Model.Loan;
import boboteca.Model.Tax;
import com.jfoenix.controls.JFXButton;
import com.sun.prism.impl.Disposer;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.joda.time.LocalDate;
import org.joda.time.Period;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static boboteca.Comandos.OpenConfirmModal.openConfirmPanel;
import static boboteca.Utils.Utils.getMaxId;

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
        taxValue.setCellValueFactory(new PropertyValueFactory<>("value"));

        taxBtn.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Disposer.Record, Boolean>, ObservableValue<Boolean>>) p -> new SimpleBooleanProperty(p.getValue() != null));

        taxBtn.setCellFactory(
                (Callback<TableColumn<Disposer.Record, Boolean>, TableCell<Disposer.Record, Boolean>>) p -> new ButtonCell(taxList));

        taxTable.setItems(FXCollections.observableArrayList(taxList));
    }

    public void payAll(ActionEvent actionEvent) {
    }

    public void payLater(ActionEvent actionEvent) {
        dialogStage.close();
    }

    public void setLoansToTax(List<Loan> loanList) {
        this.loanList = loanList;
        for (Loan loan:loanList) {
            Period period = new Period(LocalDate.now(), new LocalDate(String.valueOf(loan.getReturnDate())));
            Integer days = Math.abs(period.getDays());
            taxList.add(new Tax(
                    getMaxId("taxes"),
                    loan.getBook(),
                    loan.getUser(),
                    "Multa referente ao atraso de " + days + "dias do livro " + loan.getBook().getName(),
                    getValue(loan, days),
                    false
            ));
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
                        currentTax.setPaid(true);
                        new TaxDAO().insertTax(currentTax);
                        currentTax.getBook().setDisponibility(true);
                        new BookDAO().updateBook(currentTax.getBook());
                        
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
