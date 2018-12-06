package boboteca.utils;

import boboteca.alerts.AlertsController;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import java.io.IOException;

public class Validate {

    public static boolean validateBook(JFXTextField txtBookName, JFXTextField txtBookAuthor, JFXTextField txtBookCategory, JFXTextField txtBookYear, JFXComboBox bookPrioritySelect) throws IOException {
        String message = "";
        if (txtBookName.getText().isEmpty() || txtBookAuthor.getText().isEmpty() || txtBookCategory.getText().isEmpty() || txtBookYear.getText().isEmpty() || bookPrioritySelect.getSelectionModel().getSelectedIndex() == -1){
            message += "É obrigatório o preenchimento de todos os campos.";
        }
        if(message.equals("")){
            return true;
        }else{
            new AlertsController().showMessage(message,"alert-danger",true,null,null);
            return false;
        }
    }

    public static boolean validLogin(JFXTextField cpfTextLogin, JFXPasswordField passwordTextLogin, Boolean fields) throws IOException {
        String message = "";
        if(fields){
            message += "Usuário não encontrado.\nVerifique os dados e tente novamente.";
        } else if (cpfTextLogin.getText().isEmpty()){
            message += "O número do usuário não pode estar em branco.";
        } else if (passwordTextLogin.getText().isEmpty()){
            message += "A senha não pode estar em branco.";
        }
        if(message.equals("")){
            return true;
        }else{
            new AlertsController().showMessage(message,"alert-danger",false,null,null);
            return false;
        }
    }

    public static boolean validateUser(JFXTextField txtUserName, JFXTextField txtUserTelephone, JFXTextField txtUserCEP, JFXTextField txtUserAddressStreet, JFXTextField txtUserAddressNumber, JFXTextField txtUserAddressNeighborhood, JFXTextField txtUserAddressCity, JFXTextField txtUserAddressState, JFXPasswordField txtUserPassword, JFXPasswordField txtUserPasswordConfirm, JFXComboBox userCategorySelect, JFXComboBox userGenderSelect) throws IOException {
        String message = "";
        if (txtUserName.getText().isEmpty() || txtUserTelephone.getText().isEmpty() || txtUserCEP.getText().isEmpty() || txtUserAddressStreet.getText().isEmpty() || txtUserAddressNumber.getText().isEmpty() || txtUserAddressNeighborhood.getText().isEmpty() || txtUserAddressCity.getText().isEmpty() || txtUserAddressState.getText().isEmpty() || txtUserPassword.getText().isEmpty() || txtUserPasswordConfirm.getText().isEmpty() || userCategorySelect.getSelectionModel().getSelectedIndex() == -1 || userGenderSelect.getSelectionModel().getSelectedIndex() == -1){
            message += "É obrigatório o preenchimento de todos os campos.";
        } else if (!txtUserPassword.getText().equals(txtUserPasswordConfirm.getText())){
            message += "Senhas não conferem";
        }
        if(message.equals("")){
            return true;
        }else{
            new AlertsController().showMessage(message,"alert-danger",false,null,null);
            return false;
        }
    }

    public static boolean validateUserUpdate(JFXTextField txtUserName, JFXTextField txtUserTelephone, JFXTextField txtUserCEP, JFXTextField txtUserAddressStreet, JFXTextField txtUserAddressNumber, JFXTextField txtUserAddressNeighborhood, JFXTextField txtUserAddressCity, JFXTextField txtUserAddressState, JFXComboBox userCategorySelect, JFXComboBox userGenderSelect) throws IOException {
        String message = "";
        if (txtUserName.getText().isEmpty() || txtUserTelephone.getText().isEmpty() || txtUserCEP.getText().isEmpty() || txtUserAddressStreet.getText().isEmpty() || txtUserAddressNumber.getText().isEmpty() || txtUserAddressNeighborhood.getText().isEmpty() || txtUserAddressCity.getText().isEmpty() || txtUserAddressState.getText().isEmpty() || userCategorySelect.getSelectionModel().getSelectedIndex() == -1 || userGenderSelect.getSelectionModel().getSelectedIndex() == -1){
            message += "É obrigatório o preenchimento de todos os campos.";
        }
        if(message.equals("")){
            return true;
        }else{
            new AlertsController().showMessage(message,"alert-danger",false,null,null);
            return false;
        }
    }

    public static void showInfo(String message) throws IOException {
        new AlertsController().showMessage(message,"alert-info",false,null,null);
    }
}
