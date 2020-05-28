package ui.chat;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatView implements Initializable {
    @FXML private TextField searchInput, messageInput;
    @FXML private Label userName, contactName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void handleEnterSearchKeyReleased(KeyEvent keyEvent) {
    }

    public void handleEnterSendKeyReleased(KeyEvent keyEvent) {
    }

    public void handleSendMessageClick(MouseEvent mouseEvent) {
    }
}
