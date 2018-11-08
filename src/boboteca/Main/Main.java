package boboteca.Main;

import boboteca.Controllers.FXMLConsultaController;
import boboteca.Controllers.FXMLHomeController;
import boboteca.Utils.Route;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
//        new CreateDatabase().createTables();
//        if(loadMainView()){
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(FXMLHomeController.class.getResource(Route.MAINVIEW));
            AnchorPane parent = fxmlLoader.load();
            Scene scene = new Scene(parent);

            stage.setScene(scene);
            stage.setTitle("Boboteca");
            stage.setResizable(false);
            stage.show();
//        }
    }

    private boolean loadMainView() throws java.io.IOException {
        Stage dialogStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(FXMLConsultaController.class.getResource(Route.SEARCHVIEW));
        AnchorPane parent = fxmlLoader.load();
        Scene scene = new Scene(parent);

        dialogStage.setScene(scene);
        FXMLConsultaController controller = fxmlLoader.getController();
        dialogStage.setTitle("Boboteca");
        dialogStage.setResizable(false);
        controller.setMainStage(dialogStage);
        dialogStage.showAndWait();
        return controller.isBtnConfirmClicked();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
