package ui.chat;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatView implements Initializable {
    @FXML private ScrollPane clientListScroll, scrollPane;
    @FXML private VBox clientListBox, chatBox;
    @FXML private TextField searchInput, messageInput;
    @FXML private Label userName, contactName;
    @FXML private ImageView userImageProfile, contactImageProfile;

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
