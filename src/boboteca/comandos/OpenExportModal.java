package boboteca.comandos;

import boboteca.controllers.FXMLExportTypeController;
import boboteca.utils.Route;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class OpenExportModal {
    
    public static void openExportModal(String message, Integer type) throws IOException {
        Stage dialogStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(FXMLExportTypeController.class.getResource(Route.EXPORTVIEW));
        AnchorPane parent = fxmlLoader.load();
        Scene scene = new Scene(parent);

        dialogStage.setScene(scene);
        FXMLExportTypeController controller = fxmlLoader.getController();
        dialogStage.setResizable(false);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        controller.setMessage(message);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        controller.setType(type);
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
    }
}
