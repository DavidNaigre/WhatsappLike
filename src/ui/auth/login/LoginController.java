package ui.auth.login;

import function.user.AuthUser;
import function.user.UserTemp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML private TextArea idTextarea;
    @FXML private Button closeButton, reduceButton;
    @FXML private Pane titleBar;
    private double xOffset,yOffset;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idTextarea.textProperty().addListener((a,z,e)-> System.out.println(e));
        titleBar.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        titleBar.setOnMouseDragged(e -> {
            titleBar.getScene().getWindow().setX(e.getScreenX() - xOffset);
            titleBar.getScene().getWindow().setY(e.getScreenY() - yOffset);
        });
    }

    public void handleBackButtonAction(ActionEvent actionEvent) throws IOException {
        Parent home = FXMLLoader.load(getClass().getResource("../home/HomeView.fxml"));
        Scene homeScene = new Scene(home);
        Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(homeScene);
        window.centerOnScreen();
        window.show();
    }

    public void handleCreateAccountButtonAction(ActionEvent actionEvent) throws IOException {
        String id = idTextarea.getText();
        if(UserTemp.checkParameters(id)) {
            if(AuthUser.register()) {
                Parent home = FXMLLoader.load(getClass().getResource("../../chat/ChatView.fxml"));
                Scene homeScene = new Scene(home);
                Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
                window.setScene(homeScene);
                window.centerOnScreen();
                window.show();
            }
        }
    }
    public void closeBtn(){
        Runtime.getRuntime().exit(0);
        ((Stage)closeButton.getScene().getWindow()).close();
    }

    public void handleReduceButton() {
        ((Stage)reduceButton.getScene().getWindow()).setIconified(true);
    }
}
