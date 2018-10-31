package boboteca.Controllers;

import boboteca.DAO.UserDAO;
import boboteca.Model.Address;
import boboteca.Model.Generic;
import boboteca.Model.User;
import boboteca.Utils.Utils;
import com.jfoenix.controls.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
    private JFXComboBox userCategotySelect;
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

    private List<User> userList = new UserDAO().findAllUsers();
    private List<Generic> genderList = new ArrayList<>();
    private List<Generic> categoryList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTableViewUser("");
        saveBtn.setText("Novo");
        genderList.add(new Generic(1,"Feminino"));
        genderList.add(new Generic(2,"Masculino"));
        genderList.add(new Generic(3,"Intersexo"));
        userGenderSelect.setItems(FXCollections.observableArrayList(genderList));
        categoryList.add(new Generic(1,"Aluno"));
        categoryList.add(new Generic(2,"Professor"));
        categoryList.add(new Generic(3,"Funcionário"));
        categoryList.add(new Generic(4,"Comunidade"));
        userCategotySelect.setItems(FXCollections.observableArrayList(categoryList));

        searchText.textProperty().addListener(obs->{
            if(searchText.getText().length() <= 0) {
                loadTableViewUser("");
            }
            else{
                loadTableViewUser(searchText.getText());
            }
        });
    }

    private void loadTableViewUser(String search) {
        userId.setCellValueFactory(new PropertyValueFactory<>("id"));
        userName.setCellValueFactory(new PropertyValueFactory<>("name"));
        userCategory.setCellValueFactory((Callback<TableColumn.CellDataFeatures<User, String>, ObservableValue<String>>) param -> {
            final User user = param.getValue();
            return new SimpleObjectProperty<>(categoryList.get(user.getCategory()).getLabel());
        });

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
    }

    public void saveUser(ActionEvent actionEvent) {
        if(saveBtn.getText().equals("Novo")){
            userPane.setDisable(false);
            saveBtn.setText("Inserir");
            txtUserCode.setText(String.valueOf(Utils.getMaxId("user")));
        }else if (saveBtn.getText().equals("Inserir")){
            Address address = new Address(Integer.valueOf(txtUserCode.getText()),txtUserAddressStreet.getText(),Integer.valueOf(txtUserAddressNumber.getText()),txtUserAddressComplement.getText(),txtUserAddressNeighborhood.getText(),txtUserCEP.getText(),txtUserAddressCity.getText(),txtUserAddressState.getText());
            User user = new User(Integer.valueOf(txtUserCode.getText()),txtUserName.getText(),userGenderSelect.getSelectionModel().getSelectedIndex(),userCategotySelect.getSelectionModel().getSelectedIndex(),address,txtUserTelephone.getText(),userIsLibraryCheck.isSelected());
            user.setPassword(txtUserPassword.getText());
            new UserDAO().insertUser(user);
            userList.add(user);
            loadTableViewUser("");
        }
    }

    public void changeSearch(ActionEvent actionEvent) {
        loadTableViewUser(searchText.getText());
    }
}
