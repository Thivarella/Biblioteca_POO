package boboteca.controllers;

import boboteca.model.Tax;
import boboteca.proxy.ProxyTaxDAO;
import boboteca.utils.Utils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.sun.prism.impl.Disposer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
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

public class FXMLConsultaMultaController implements Initializable {
    @FXML
    private JFXTextField searchTax;
    @FXML
    private JFXRadioButton taxByUserName;
    @FXML
    private ToggleGroup taxTypeSearch;
    @FXML
    private JFXRadioButton taxByUserCode;
    @FXML
    private JFXButton exportTax;
    @FXML
    private TableView tableTax;
    @FXML
    private TableColumn taxTableUserCode;
    @FXML
    private TableColumn taxTableUserName;
    @FXML
    private TableColumn taxTableBookName;
    @FXML
    private TableColumn taxTableBookReturned;
    @FXML
    private TableColumn taxTableTaxValue;
    @FXML
    private TableColumn taxTableTaxIsPaid;
    @FXML
    private TableColumn taxTableTaxPayBtn;

    private ProxyTaxDAO taxDAO = ProxyTaxDAO.getInstance();
    private List<Tax> taxList;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Preferences userPreferences = Preferences.userRoot();
        String info = userPreferences.get("logged","");
        Integer id = Integer.valueOf(userPreferences.get("userId",""));
        if(!info.equals("true")){
            taxTableUserName.setVisible(false);
            taxByUserName.setVisible(false);
            taxByUserCode.setVisible(false);
            searchTax.setVisible(false);
            taxByUserCode.setVisible(false);
            taxTableUserCode.setVisible(false);
            taxTableTaxPayBtn.setVisible(false);
            exportTax.setVisible(false);
            taxList = taxDAO.findAllTaxByUserId(id,null);
        }else {
            taxList = taxDAO.findAllTax();
        }

        searchTax.textProperty().addListener(obs -> loadTableTax(searchTax.getText()));

        loadTableTax("");
    }

    private void loadTableTax(String search) {
        taxTableUserCode.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Tax, String>, ObservableValue<String>>) param -> {
            final Tax tax = param.getValue();
            return new SimpleObjectProperty<>(tax.getUser().getId().toString());
        });
        taxTableUserName.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Tax, String>, ObservableValue<String>>) param -> {
            final Tax tax = param.getValue();
            return new SimpleObjectProperty<>(tax.getUser().getName());
        });
        taxTableBookName.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Tax, String>, ObservableValue<String>>) param -> {
            final Tax tax = param.getValue();
            return new SimpleObjectProperty<>(tax.getBook().getName());
        });
        taxTableBookReturned.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Tax, String>, ObservableValue<String>>) param -> {
            final Tax tax = param.getValue();
            return new SimpleObjectProperty<>(dateFormat.format(tax.getLoan().getReturnedDate()));
        });
        taxTableTaxValue.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Tax, String>, ObservableValue<String>>) param -> {
            final Tax tax = param.getValue();
            return new SimpleObjectProperty<>(Utils.formatMoney(tax.getValue()));
        });
        taxTableTaxIsPaid.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Tax, String>, ObservableValue<String>>) param -> {
            final Tax tax = param.getValue();
            return new SimpleObjectProperty<>(tax.getPaid()?"Pago":"Em aberto");
        });

        taxTableTaxPayBtn.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Disposer.Record, Boolean>, ObservableValue<Boolean>>) p -> new SimpleBooleanProperty(p.getValue() != null));

        taxTableTaxPayBtn.setCellFactory(
                (Callback<TableColumn<Disposer.Record, Boolean>, TableCell<Disposer.Record, Boolean>>) p -> new ButtonCell(taxList));

        String[] parts = search.toUpperCase().split(" ");

        ObservableList<Tax> taxObservableList = FXCollections.observableArrayList();
        for (Tax t: taxList) {
            mountTaxList(parts, taxObservableList, t);
        }

        tableTax.setItems(taxObservableList);
    }

    private void mountTaxList(String[] parts, ObservableList<Tax> taxObservableList, Tax t) {
        boolean match = true;
        String entryText;
        if(taxByUserCode.isSelected()){
            entryText = t.getId().toString();
        }else{
            entryText = t.getUser().getName();
        }
        for (String part: parts) {
            if (!entryText.toUpperCase().contains(part)) {
                match = false;
                break;
            }
        }

        if (match) {
            taxObservableList.add(t);
        }
    }

    public class ButtonCell extends TableCell<Disposer.Record, Boolean> {
        final JFXButton cellButton = new JFXButton("$");

        ButtonCell(List<Tax> taxList) {
            cellButton.getStyleClass().addAll("btn-success");
            cellButton.setOnAction(t -> {
                Tax currentTax = (Tax) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                currentTax.setPaid(true);
                taxDAO.updateTax(currentTax);
                int index = taxList.indexOf(currentTax);
                taxList.remove(currentTax);
                taxList.add(index,currentTax);
                loadTableTax("");
            });
        }

        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) {
                Tax tax = (Tax) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                if (!tax.getPaid()) {
                    setGraphic(cellButton);
                }
            }
        }
    }


    public void exportTax() throws IOException {
        openExportModal("Qual o formato de arquivo desejado para exportar o relatório de multas?",4);
    }
}
