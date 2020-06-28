import function.appPath;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent login = FXMLLoader.load(getClass().getResource("ui/auth/home/HomeView.fxml"));
            Scene loginScene = new Scene(login);
            primaryStage.setScene(loginScene);
            primaryStage.getIcons().add(new Image("ui/ressources/images/logo150X150.png"));
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() throws Exception {
        super.init();
        String authDIRECTORY, historyDIRECTORY;
        String ParentPATH = getParentPath();

        if(getCurrentPath().contains("jar")){
            authDIRECTORY = ParentPATH+"/WALLOG/auth/";
            historyDIRECTORY = ParentPATH+"/WALLOG/message/history/";

            File auth_dir = new File(authDIRECTORY);
            File hist_dir = new File(historyDIRECTORY);

            if(!auth_dir.exists()) auth_dir.mkdirs();
            if(!hist_dir.exists()) hist_dir.mkdirs();
        } else {
            authDIRECTORY = "src/function/user/";
            historyDIRECTORY = "src/function/messages/history/";
        }
        appPath.setPaths(ParentPATH, authDIRECTORY, historyDIRECTORY);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static String getParentPath() throws URISyntaxException {
        return new File(Main.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getParent();
    }
    public static String getCurrentPath() throws URISyntaxException {
        return new File(Main.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getPath();
    }
}
