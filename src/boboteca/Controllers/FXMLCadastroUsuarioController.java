package boboteca.Controllers;

import boboteca.DAO.GenericDAO;
import boboteca.DAO.UserDAO;
import boboteca.Model.Address;
import boboteca.Model.Generic;
import boboteca.Model.User;
import boboteca.Utils.Utils;
import com.jfoenix.controls.*;
import com.sun.prism.impl.Disposer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static boboteca.Comandos.OpenConfirmModal.openConfirmPanel;
import static boboteca.Comandos.updateTable.update;

public class FXMLCadastroUsuarioController implements Initializable {
    @FXML
    public AnchorPane userPane;
    @FXML
    private TableView tableViewUsers;
    @FXML
    private TableColumn userId;
    @FXML
    private TableColumn userName;
    @FXML
    private TableColumn userCategory;
    @FXML
    public TableColumn userRemove;
    @FXML
    private JFXTextField searchText;
    @FXML
    private JFXToggleButton searchByCode;
    @FXML
    private JFXTextField txtUserCode;
    @FXML
    private JFXTextField txtUserName;
    @FXML
    private JFXComboBox userGenderSelect;
    @FXML
    private JFXComboBox userCategorySelect;
    @FXML
    private JFXTextField txtUserTelephone;
    @FXML
    private JFXCheckBox userIsLibraryCheck;
    @FXML
    private JFXTextField txtUserCEP;
    @FXML
    private JFXTextField txtUserAddressStreet;
    @FXML
    private JFXTextField txtUserAddressNumber;
    @FXML
    private JFXTextField txtUserAddressNeighborhood;
    @FXML
    private JFXTextField txtUserAddressComplement;
    @FXML
    private JFXTextField txtUserAddressCity;
    @FXML
    private JFXTextField txtUserAddressState;
    @FXML
    private JFXTextField txtUserPassword;
    @FXML
    private JFXTextField txtUserPasswordConfirm;
    @FXML
    private JFXButton saveBtn;
    @FXML
    private JFXButton updateBtn;

    private UserDAO userDAO = new UserDAO();
    private List<User> userList = userDAO.findAllUsers();
    private List<Generic> genderList = new GenericDAO().getGender();
    private List<Generic> categoryList = new GenericDAO().getCategory();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTableViewUser("");
        saveBtn.setText("Novo");
        loadComboboxGender();
        loadComboboxCategory();

        searchText.textProperty().addListener(obs->{
            loadTableViewUser(searchText.getText());
        });

        tableViewUsers.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) ->
                selectTableViewUsers((User) newValue)));
    }

    private void loadComboboxCategory() {
        userCategorySelect.valueProperty().setValue(null);
        userCategorySelect.setItems(FXCollections.observableArrayList(categoryList));
    }

    private void loadComboboxGender() {
        userGenderSelect.valueProperty().setValue(null);
        userGenderSelect.setItems(FXCollections.observableArrayList(genderList));
    }

    private void selectTableViewUsers(User user) {
        if (user != null) {
            userPane.setDisable(true);
            saveBtn.setText("Novo");
            updateBtn.setDisable(false);
            txtUserCode.setText(String.valueOf(user.getId()));
            txtUserName.setText(user.getName());
            txtUserTelephone.setText(user.getTelephone());
            userIsLibraryCheck.setSelected(user.getLibrarian());
            txtUserCEP.setText(user.getAddress().getCep());
            txtUserAddressStreet.setText(user.getAddress().getStreet());
            txtUserAddressNumber.setText(String.valueOf(user.getAddress().getNumber()));
            txtUserAddressNeighborhood.setText(user.getAddress().getNeighborhood());
            txtUserAddressComplement.setText(user.getAddress().getComplement());
            txtUserAddressCity.setText(user.getAddress().getCity());
            txtUserAddressState.setText(user.getAddress().getState());
            txtUserPassword.setText(user.getPassword());
            txtUserPasswordConfirm.setText(user.getPassword());
            userCategorySelect.getSelectionModel().select(categoryList.get(user.getCategory().getId() - 1));
            userGenderSelect.getSelectionModel().select(genderList.get(user.getGender().getId() - 1));
        } else {
            clearFields();
        }
    }

    private void clearFields() {
        loadComboboxCategory();
        loadComboboxGender();
        txtUserCode.setText("");
        txtUserName.setText("");
        txtUserTelephone.setText("");
        userIsLibraryCheck.setSelected(false);
        txtUserCEP.setText("");
        txtUserAddressStreet.setText("");
        txtUserAddressNumber.setText("");
        txtUserAddressNeighborhood.setText("");
        txtUserAddressComplement.setText("");
        txtUserAddressCity.setText("");
        txtUserAddressState.setText("");
        txtUserPassword.setText("");
        txtUserPasswordConfirm.setText("");
    }


    private void loadTableViewUser(String search) {
        userId.setCellValueFactory(new PropertyValueFactory<>("id"));
        userName.setCellValueFactory(new PropertyValueFactory<>("name"));
        userCategory.setCellValueFactory((Callback<TableColumn.CellDataFeatures<User, String>, ObservableValue<String>>) param -> {
            final User user = param.getValue();
            return new SimpleObjectProperty<>(user.getCategory().getLabel());
        });
        userRemove.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Disposer.Record, Boolean>, ObservableValue<Boolean>>) p -> new SimpleBooleanProperty(p.getValue() != null));

        userRemove.setCellFactory(
                (Callback<TableColumn<Disposer.Record, Boolean>, TableCell<Disposer.Record, Boolean>>) p -> new ButtonCell(userList));

        String[] parts = search.toUpperCase().split(" ");

        ObservableList<User> userObservableList = FXCollections.observableArrayList();
        for (User u: userList) {
            mountUserList(parts, userObservableList, u);
        }

        tableViewUsers.setItems(userObservableList);
    }

    private void mountUserList(String[] parts, ObservableList<User> userObservableList, User u) {
        boolean match = true;
        String entryText = null;
        if(searchByCode.isSelected()){
            entryText = u.getId().toString();
        }else{
            entryText = u.getName();
        }
        for (String part: parts) {
            if (!entryText.toUpperCase().contains(part)) {
                match = false;
                break;
            }
        }

        if (match) {
            userObservableList.add(u);
        }
    }

    public void updateUser(ActionEvent actionEvent) {
        update(updateBtn, userPane, saveBtn, tableViewUsers);
    }

    public void saveUser(ActionEvent actionEvent) {
        Boolean updates = false;
        if(saveBtn.getText().equals("Novo")){
            tableViewUsers.getSelectionModel().clearSelection();
            userPane.setDisable(false);
            updateBtn.setDisable(true);
            saveBtn.setText("Inserir");
            txtUserCode.setText(String.valueOf(Utils.getMaxId("user")));
        }else if (saveBtn.getText().equals("Inserir")){
            Address address = getAddress();
            User user = getUser(address);
            user.setPassword(txtUserPassword.getText());
            if (userDAO.insertUser(user)) {
                userList.add(user);
                updates = true;
            }
        } else if (saveBtn.getText().equals("Salvar")) {
            Address address = getAddress();
            User user = getUser(address);
            updateBtn.setText("Alterar");
            updateBtn.setDisable(true);
            if (userDAO.updateUser(user)) {
                userList = userDAO.findAllUsers();
                updates = true;
            }
        }
        if (updates) {
            clearFields();
            loadTableViewUser(searchText.getText());
            userPane.setDisable(true);
            saveBtn.setText("Novo");
        }
    }

    private User getUser(Address address) {
        return new User(Integer.valueOf(txtUserCode.getText()), txtUserName.getText(), (Generic) userGenderSelect.getSelectionModel().getSelectedItem(), (Generic) userCategorySelect.getSelectionModel().getSelectedItem(), address, txtUserTelephone.getText(), userIsLibraryCheck.isSelected());
    }

    private Address getAddress() {
        return new Address(Integer.valueOf(txtUserCode.getText()), txtUserAddressStreet.getText(), Integer.valueOf(txtUserAddressNumber.getText()), txtUserAddressComplement.getText(), txtUserAddressNeighborhood.getText(), txtUserCEP.getText(), txtUserAddressCity.getText(), txtUserAddressState.getText());
    }

    public void changeSearch(ActionEvent actionEvent) {
        loadTableViewUser(searchText.getText());
    }

    public class ButtonCell extends TableCell<Disposer.Record, Boolean> {
        final JFXButton cellButton = new JFXButton("X");

        public ButtonCell(List<User> userList) {
            cellButton.getStyleClass().addAll("btn-danger");
            cellButton.setOnAction(t -> {
                User currentUser = (User) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                try {
                    if (openConfirmPanel("Deseja realmente exlcuir " + currentUser.getName() + "?")) {
                        userDAO.removeUser(currentUser.getId());
                        userList.remove(currentUser);
                        loadTableViewUser("");
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
}


