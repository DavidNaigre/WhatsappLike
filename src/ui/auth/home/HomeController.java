package ui.auth.home;

import function.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    @FXML private Button currentSession, closeButton, reduceButton;
    @FXML private Pane titleBar;
    private double xOffset,yOffset;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentSession.setText(User.getIdentifiant());
        titleBar.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        titleBar.setOnMouseDragged(e -> {
            titleBar.getScene().getWindow().setX(e.getScreenX() - xOffset);
            titleBar.getScene().getWindow().setY(e.getScreenY() - yOffset);
        });
    }

    public void handleSameAccountButtonClick(ActionEvent actionEvent) throws IOException {
        Parent chat = FXMLLoader.load(getClass().getResource("../../chat/ChatView.fxml"));
        Scene chatScene = new Scene(chat);
        Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(chatScene);
        window.centerOnScreen();
        window.show();
    }

    public void handleNewAccountButtonClick(ActionEvent actionEvent) throws IOException {
        Parent home = FXMLLoader.load(getClass().getResource("../create/CreateAccountView.fxml"));
        Scene homeScene = new Scene(home);
        Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(homeScene);
        window.centerOnScreen();
        window.show();
    }

    public void closeBtn(){
        Runtime.getRuntime().exit(0);
        ((Stage)closeButton.getScene().getWindow()).close();
    }
    public void handleReduceButton() {
        ((Stage)reduceButton.getScene().getWindow()).setIconified(true);
    }
}
