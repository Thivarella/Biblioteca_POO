package boboteca.controllers;

import boboteca.model.Booking;
import boboteca.proxy.ProxyBookingDAO;
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

public class FXMLConsultaReservaController implements Initializable {
    @FXML
    private JFXTextField searchBooking;
    @FXML
    private JFXRadioButton bookingByUserName;
    @FXML
    private ToggleGroup bookingTypeSearch;
    @FXML
    private JFXRadioButton bookingByUserCode;
    @FXML
    private JFXButton exportBooking;
    @FXML
    private TableView tableBooking;
    @FXML
    private TableColumn bookingTableUserCode;
    @FXML
    private TableColumn bookingTableUserName;
    @FXML
    private TableColumn bookingTableBookCode;
    @FXML
    private TableColumn bookingTableBookName;
    @FXML
    private TableColumn bookingTableBookingDate;
    @FXML
    private TableColumn bookingTableBookingStatus;
    @FXML
    private TableColumn bookingCancelBooking;

    private ProxyBookingDAO bookingDAO = ProxyBookingDAO.getInstance();
    private List<Booking> bookingList;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Preferences userPreferences = Preferences.userRoot();
        String info = userPreferences.get("logged","");
        Integer id = Integer.valueOf(userPreferences.get("userId",""));
        if(info.equals("true")){
            bookingList = bookingDAO.findAllBooking();
            bookingCancelBooking.setVisible(false);
        }else {
            searchBooking.setVisible(false);
            bookingByUserCode.setVisible(false);
            bookingByUserName.setVisible(false);
            bookingTableUserCode.setVisible(false);
            bookingTableUserName.setVisible(false);
            exportBooking.setVisible(false);
            bookingList = bookingDAO.findAllBookingByUserId(id);
        }

        searchBooking.textProperty().addListener(obs -> {
            loadTableBooking(searchBooking.getText());
        });

        loadTableBooking("");
    }

    private void loadTableBooking(String search) {
        bookingTableUserCode.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Booking, String>, ObservableValue<String>>) param -> {
            final Booking booking = param.getValue();
            return new SimpleObjectProperty<>(booking.getUser().getId().toString());
        });
        bookingTableUserName.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Booking, String>, ObservableValue<String>>) param -> {
            final Booking booking = param.getValue();
            return new SimpleObjectProperty<>(booking.getUser().getName());
        });
        bookingTableBookCode.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Booking, String>, ObservableValue<String>>) param -> {
            final Booking booking = param.getValue();
            return new SimpleObjectProperty<>(booking.getBook().getId().toString());
        });
        bookingTableBookName.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Booking, String>, ObservableValue<String>>) param -> {
            final Booking booking = param.getValue();
            return new SimpleObjectProperty<>(booking.getBook().getName());
        });
        bookingTableBookingDate.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Booking, String>, ObservableValue<String>>) param -> {
            final Booking booking = param.getValue();
            return new SimpleObjectProperty<>(dateFormat.format(booking.getBookingDate()));
        });
        bookingTableBookingStatus.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Booking, String>, ObservableValue<String>>) param -> {
            final Booking booking = param.getValue();
            return new SimpleObjectProperty<>(booking.getStaus()?"Reservado":"Reserva finalizada");
        });

        bookingCancelBooking.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Disposer.Record, Boolean>, ObservableValue<Boolean>>) p -> new SimpleBooleanProperty(p.getValue() != null));

        bookingCancelBooking.setCellFactory(
                (Callback<TableColumn<Disposer.Record, Boolean>, TableCell<Disposer.Record, Boolean>>) p -> new ButtonCell(bookingList));

        String[] parts = search.toUpperCase().split(" ");

        ObservableList<Booking> bookingObservableList = FXCollections.observableArrayList();
        for (Booking t: bookingList) {
            mountBookingList(parts, bookingObservableList, t);
        }

        tableBooking.setItems(bookingObservableList);
    }

    private void mountBookingList(String[] parts, ObservableList<Booking> bookingObservableList, Booking t) {
        boolean match = true;
        String entryText = null;
        if(bookingByUserCode.isSelected()){
            entryText = t.getUser().getId().toString();
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
            bookingObservableList.add(t);
        }
    }

    public class ButtonCell extends TableCell<Disposer.Record, Boolean> {
        final JFXButton cellButton = new JFXButton("X");

        public ButtonCell(List<Booking> bookingList) {
            cellButton.getStyleClass().addAll("btn-danger");
            cellButton.setOnAction(t -> {
                Booking currentBooking = (Booking) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                currentBooking.setStaus(false);
                bookingDAO.updateBooking(currentBooking);
                int index = bookingList.indexOf(currentBooking);
                bookingList.remove(currentBooking);
                bookingList.add(index,currentBooking);
                loadTableBooking("");
            });
        }

        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) {
                Booking booking = (Booking) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                if (booking.getStaus()) {
                    setGraphic(cellButton);
                }
            }
        }
    }


    public void exportBooking() throws IOException {
        openExportModal("Qual o formato de arquivo desejado para exportar o relatório de reservas?",5);
    }
}
