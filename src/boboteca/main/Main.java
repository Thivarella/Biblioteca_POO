package boboteca.main;

import boboteca.controllers.FXMLLoginController;
import boboteca.utils.Route;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
//        new CreateDatabase().createTables();
        loadMainView();
    }

    private void loadMainView() throws java.io.IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLLoginController.class.getResource(Route.LOGINVIEW));
        AnchorPane page = loader.load();

        Stage dialogStage = new Stage();
        Scene scene = new Scene(page);
//        dialogStage.getIcons().add(new Image());
        dialogStage.setScene(scene);

        FXMLLoginController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
