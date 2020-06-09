import function.user.AuthUser;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private Boolean checkAuth = false;

    @Override
    public void init() throws Exception {
        super.init();
        checkAuth = AuthUser.isAuth();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent login = FXMLLoader.load(getClass().getResource("ui/auth/home/HomeView.fxml"));
            Parent create = FXMLLoader.load(getClass().getResource("./ui/auth/create/CreateAccountView.fxml"));

            Scene loginScene = new Scene(login);
            Scene createScene = new Scene(create);

            if(checkAuth) primaryStage.setScene(loginScene);
            else primaryStage.setScene(createScene);

            primaryStage.setTitle("Not WhatsApp");
            primaryStage.getIcons().add(new Image("./ui/ressources/images/favicon.png"));
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
//            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("yop");
            e.printStackTrace();
        }
    }

}
