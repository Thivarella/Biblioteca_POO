package boboteca.controllers;

import boboteca.dao.GenericDAO;
import boboteca.model.Address;
import boboteca.model.Generic;
import boboteca.model.User;
import boboteca.proxy.ProxyUserDAO;
import boboteca.utils.Utils;
import boboteca.utils.Validate;
import com.jfoenix.controls.*;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static boboteca.comandos.OpenConfirmModal.openConfirmPanel;
import static boboteca.comandos.OpenExportModal.openExportModal;
import static boboteca.comandos.UpdateTable.update;
import static boboteca.utils.Utils.JfxTexts;
import static boboteca.utils.Utils.clearingFields;

public class FXMLCadastroUserController implements Initializable {
    @FXML
    public AnchorPane userPane;
    @FXML
    private TableView tableViewUsers;
    @FXML
    private TableColumn<Object, Object> userId;
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
    private JFXComboBox<Generic> userGenderSelect;
    @FXML
    private JFXComboBox<Generic> userCategorySelect;
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
    private JFXPasswordField txtUserPassword;
    @FXML
    private JFXPasswordField txtUserPasswordConfirm;
    @FXML
    private JFXButton saveBtn;
    @FXML
    private JFXButton updateBtn;

    private ProxyUserDAO userDAO = ProxyUserDAO.getInstance();
    private List<User> userList = userDAO.findAllUsers();
    private List<Generic> genderList = new GenericDAO().getGender();
    private List<Generic> categoryList = new GenericDAO().getCategory();
    private List<JFXTextField> jfxTextFields = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        saveBtn.setText("Novo");
        loadComboboxGender();
        loadComboboxCategory();
        JfxTexts(jfxTextFields, txtUserCEP, txtUserAddressStreet, txtUserAddressNumber, txtUserAddressNeighborhood, txtUserAddressComplement);
        JfxTexts(jfxTextFields, txtUserCode, txtUserName, txtUserTelephone, txtUserAddressCity, txtUserAddressState);

        searchText.textProperty().addListener(obs-> loadTableViewUser(searchText.getText()));

        tableViewUsers.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) ->
                selectTableViewUsers((User) newValue)));

        loadTableViewUser("");

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
        userIsLibraryCheck.setSelected(false);
        clearingFields(jfxTextFields);
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
        boolean match;
        String entryText;
        if(searchByCode.isSelected()){
            entryText = u.getId().toString();
        }else{
            entryText = u.getName();
        }
        match = isMatch(parts, entryText);

        if (match) {
            userObservableList.add(u);
        }
    }

    private boolean isMatch(String[] parts, String entryText) {
        boolean match = true;
        for (String part: parts) {
            if (!entryText.toUpperCase().contains(part)) {
                match = false;
                break;
            }
        }
        return match;
    }

    public void updateUser() {
        if(updateBtn.getText().equals("Cancelar")){
            clearFields();
            updateBtn.setText("Alterar");
            updateBtn.setDisable(true);
            userPane.setDisable(true);
            saveBtn.setText("Novo");
        } else {
            update(updateBtn, userPane, saveBtn, tableViewUsers);
        }
    }

    public void saveUser() throws IOException {
        boolean updates = false;
        switch (saveBtn.getText()) {
            case "Novo":
                tableViewUsers.getSelectionModel().clearSelection();
                userPane.setDisable(false);
                saveBtn.setText("Inserir");
                updateBtn.setText("Cancelar");
                updateBtn.setDisable(false);
                txtUserCode.setText(String.valueOf(Utils.getMaxId("user")));
                break;
            case "Inserir":
                if (Validate.validateUser(txtUserName, txtUserTelephone, txtUserCEP, txtUserAddressStreet, txtUserAddressNumber, txtUserAddressNeighborhood, txtUserAddressCity, txtUserAddressState, txtUserPassword, txtUserPasswordConfirm, userCategorySelect, userGenderSelect)) {
                    Address address = getAddress();
                    User user = getUser(address);
                    user.setPassword(txtUserPassword.getText());
                    if (userDAO.insertUser(user)) {
                        user.setPassword("");
                        userList.add(user);
                        updates = true;
                    }
                }
                break;
            case "Salvar":
                if (Validate.validateUserUpdate(txtUserName, txtUserTelephone, txtUserCEP, txtUserAddressStreet, txtUserAddressNumber, txtUserAddressNeighborhood, txtUserAddressCity, txtUserAddressState, userCategorySelect, userGenderSelect)) {
                    Address address = getAddress();
                    User user = getUser(address);
                    if (userDAO.updateUser(user)) {
                        userList = userDAO.findAllUsers();
                        updates = true;
                    }
                }
                break;
        }
        if (updates) {
            clearFields();
            loadTableViewUser(searchText.getText());
            userPane.setDisable(true);
            updateBtn.setText("Alterar");
            updateBtn.setDisable(true);
            saveBtn.setText("Novo");
        }
    }

    private User getUser(Address address) {
        return new User(Integer.valueOf(txtUserCode.getText()), txtUserName.getText(), userGenderSelect.getSelectionModel().getSelectedItem(), userCategorySelect.getSelectionModel().getSelectedItem(), address, txtUserTelephone.getText(), userIsLibraryCheck.isSelected());
    }

    private Address getAddress() {
        return new Address(Integer.valueOf(txtUserCode.getText()), txtUserAddressStreet.getText(), Integer.valueOf(txtUserAddressNumber.getText()), txtUserAddressComplement.getText(), txtUserAddressNeighborhood.getText(), txtUserCEP.getText().replace("-",""), txtUserAddressCity.getText(), txtUserAddressState.getText());
    }

    public void changeSearch() {
        loadTableViewUser(searchText.getText());
    }

    public void textCEPChanged() throws IOException, ParseException {
        Utils.cepField(txtUserCEP);
        Utils.searchCep(txtUserCEP.getText().replace("-",""),txtUserAddressStreet,txtUserAddressNumber,txtUserAddressNeighborhood, txtUserAddressComplement,txtUserAddressCity,txtUserAddressState);
    }

    public void maskPhone() {
        Utils.phoneField(txtUserTelephone);
    }

    public class ButtonCell extends TableCell<Disposer.Record, Boolean> {
        final JFXButton cellButton = new JFXButton("X");

        ButtonCell(List<User> userList) {
            cellButton.getStyleClass().addAll("btn-danger");
            cellButton.setOnAction(t -> {
                User currentUser = (User) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                try {
                    if(userDAO.getLoansCountByUser(currentUser.getId()) <= 0) {
                        if (openConfirmPanel("Deseja realmente exlcuir " + currentUser.getName() + "?")) {
                            userDAO.removeUser(currentUser.getId());
                            userList.remove(currentUser);
                            loadTableViewUser("");
                        }
                    }else {
                        Validate.showInfo("Usuário não pode ser excluído, pois já possui empréstimos realizados");
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

    public void exportUser() throws IOException {
        openExportModal("Qual o formato de arquivo desejado para exportar o relatório de usuário?",1);
    }
}


