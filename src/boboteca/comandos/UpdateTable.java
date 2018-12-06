package boboteca.comandos;

import com.jfoenix.controls.JFXButton;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

public class UpdateTable {

    public static void update(JFXButton updateBtn, AnchorPane bookPane, JFXButton saveBtn, TableView bookTableView) {
        if(updateBtn.getText().equals("Alterar")){
            bookPane.setDisable(false);
            saveBtn.setText("Salvar");
            updateBtn.setText("Cancelar");
        }else if (updateBtn.getText().equals("Cancelar")){
            bookPane.setDisable(true);
            saveBtn.setText("Novo");
            updateBtn.setText("Alterar");
            updateBtn.setDisable(true);
            bookTableView.getSelectionModel().clearSelection();
        }
    }

}
