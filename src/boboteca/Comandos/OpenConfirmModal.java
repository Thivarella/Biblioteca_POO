package boboteca.Comandos;

import boboteca.Controllers.FXMLConfirmDeletionController;
import boboteca.Utils.Route;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class OpenConfirmModal {
    
    public static Boolean openConfirmPanel(String message) throws IOException {
        Stage dialogStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(FXMLConfirmDeletionController.class.getResource(Route.CONFIRMDELETIONVIEW));
        AnchorPane parent = fxmlLoader.load();
        Scene scene = new Scene(parent);

        dialogStage.setScene(scene);
        FXMLConfirmDeletionController controller = fxmlLoader.getController();
        dialogStage.setResizable(false);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        controller.setMessage(message);
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
        return controller.isBtnConfirmClicked();
    }
}
