package ui.chat;

import function.messages.HistoryBuilder;
import function.user.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ChatView implements Initializable {
    @FXML private ScrollPane clientListScroll, scrollPane;
    @FXML private VBox clientListBox, chatBox;
    @FXML private TextField searchInput, messageInput;
    @FXML private Label userName, contactName;
    @FXML private ImageView userImageProfile, contactImageProfile;
    @FXML private Button sendButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sendButton.setDisable(true);
        userName.setText(User.getIdentifiant());
        messageInput.textProperty().addListener((observable, oldValue, newValue)->{
            if (messageInput.getText().isEmpty()) sendButton.setDisable(true);
            else sendButton.setDisable(false);
        });

        String toName = "Gilbert";
        ArrayList<ArrayList<String>> history = new  ArrayList<>(HistoryBuilder.read(toName));
        if(!history.isEmpty()){
            history.forEach(line -> updateMSG(line.get(1), line.get(2)));
            scrollPane.setVvalue(1.0);
        }
    }


    public void handleEnterSearchKeyReleased(ActionEvent actionEvent) {
        String message = searchInput.getText();
        updateMSG(User.getIdentifiant(),message.trim());
        searchInput.setText("");
    }

    public void handleSendMessageClick(ActionEvent actionEvent) {
        String message = messageInput.getText();
        updateMSG("Gigi",message.trim());
        messageInput.setText("");
    }

    public void updateMSG(String username, String message){
        Text text = new Text(message);
        text.setFill(Color.BLACK);
        text.getStyleClass().add("message");
        TextFlow tempFlow = new TextFlow();

        tempFlow.getChildren().add(text);
        tempFlow.setMaxWidth(400);
        TextFlow flow = new TextFlow(tempFlow);
        HBox hbox = new HBox(12);

        if(!this.userName.getText().equals(username)) {
            tempFlow.getStyleClass().add("tempFlowFlipped");
            flow.getStyleClass().add("textFlowFlipped");
            chatBox.setAlignment(Pos.TOP_LEFT);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.getChildren().add(flow);
        } else {
            text.setFill(Color.BLACK);
            tempFlow.getStyleClass().add("tempFlow");
            flow.getStyleClass().add("textFlow");
            hbox.setAlignment(Pos.BOTTOM_RIGHT);
            hbox.getChildren().add(flow);
        }
        hbox.getStyleClass().add("hbox");
        Platform.runLater(()->chatBox.getChildren().addAll(hbox));
        scrollPane.setVvalue(1.0);
    }

    public void updateUI(String username, String message){}
}
