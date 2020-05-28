package ui.auth.login;

import function.user.AuthUser;
import function.user.User;
import function.user.UserTemp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML private TextArea idTextarea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idTextarea.textProperty().addListener((a,z,e)-> System.out.println(e));
    }

    public void handleBackButtonAction(ActionEvent actionEvent) throws IOException {
        Parent home = FXMLLoader.load(getClass().getResource("../home/HomeView.fxml"));
        Scene homeScene = new Scene(home);
        Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(homeScene);
        window.show();
    }

    public void handleCreateAccountButtonAction(ActionEvent actionEvent) throws IOException {
        String id = idTextarea.getText();
        if(UserTemp.checkParameters(id)) {
            AuthUser.register();
            System.out.println("IT WORKED !");
        }
    }
}
