import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent login = FXMLLoader.load(getClass().getResource("ui/auth/home/HomeView.fxml"));
            Scene loginScene = new Scene(login);
            primaryStage.setScene(loginScene);
            primaryStage.getIcons().add(new Image("./ui/ressources/images/logo150X150.png"));
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
