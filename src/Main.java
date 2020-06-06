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


//    public static void main(String[] args) throws IOException, ParseException {
    /* UTILISE*/
//        launch(args);
//        System.out.println(AuthUser.register("90d3a5ef2bdd3b6ca01dac0045e3f41fed0f0334ca48c97a0e78b3247492b7c9","Constantin","davidnaigre@gmail.com"));
//        System.out.println(AuthUser.isAuth());
//        System.out.println(AuthUser.getUserMail());

//        HistoryBuilder.write("Gilbert","Gilbert","MAMAMIA");
//        System.out.println(HistoryBuilder.read("Gilbert"));
//        System.out.println(HistoryBuiler.read("Jacque"));
//        System.out.println(u1.sendMessage("474","Message avec espace deuxième test avec +"));
//        System.out.println(u1.readMessage("474"));
//        System.out.println(u1.newRelation("menelik-971@hotmail.com"));

    /* NON UTILISE*/
//        System.out.println(u1.deleteRelation("menelik-971@hotmail.com"));

//    }
}
