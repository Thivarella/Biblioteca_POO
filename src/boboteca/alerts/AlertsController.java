package boboteca.alerts;

import boboteca.utils.Route;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AlertsController implements Initializable{
    @FXML
    private Label lblMessage;

    private Boolean hide = false;

    private Stage dialogStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
            PauseTransition pauseTransition = new PauseTransition();
            pauseTransition.setDuration(Duration.seconds(5));
            pauseTransition.setOnFinished(ev -> {
                if(hide)
                    dialogStage.close();
            });
            pauseTransition.play();

    }

    private void setLblMessage(String lblMessage) {
        this.lblMessage.setText(lblMessage);
    }

    public void showMessage(String message, String css, Boolean show, Double layoutY, Double layoutX) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(AlertsController.class.getResource(Route.ALERTS));
        AnchorPane page = loader.load();
        page.getStyleClass().add(css);
        page.getStylesheets().add("/boboteca/bootstrap.css");
        if (!show) {
            page.getChildren().get(1).setVisible(false);
        }
        Stage stage = new Stage();
        Scene scene = new Scene(page);
        scene.setFill(Color.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.setResizable(false);

        AlertsController controller = loader.getController();
        if (!show) {
            page.getChildren().get(1).setVisible(false);
            controller.setHide();
        }

        controller.setDialogStage(stage);
        controller.setLblMessage(message);
        if (layoutY != null){
            stage.setY(layoutY);
        }
        if (layoutX != null){
            stage.setX(layoutX);
        }
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    private void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    public void handleCloseable() {
        dialogStage.close();
    }

    private void setHide() {
        this.hide = true;
    }
}
