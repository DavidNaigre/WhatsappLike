package ui.chat;

import function.messages.HistoryBuilder;
import function.user.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.File;
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
    private String toName = "Gilbert";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sendButton.setDisable(true);
        userName.setText(User.getIdentifiant());
        messageInput.textProperty().addListener(observable-> sendButton.setDisable(messageInput.getText().isEmpty()));
        chatBox.heightProperty().addListener(observable -> {
            scrollPane.layout();
            scrollPane.setVvalue(1D);
        });
        try {
            var path = new File(String.format("src/ui/ressources/profile/%s.png",toName)).toURI().toString();
            URL imgUrl = getClass().getResource(path);
            if(imgUrl == null) path = new File("src/ui/ressources/profile/default.png").toURI().toString();
            contactImageProfile.setImage(new Image(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<ArrayList<String>> history = new  ArrayList<>(HistoryBuilder.read(toName));
        if(!history.isEmpty()) history.forEach(line -> updateMSG(line.get(1), line.get(2)));
        updateUI(HistoryBuilder.getContactList());
    }


    public void handleEnterSearchKeyReleased(ActionEvent actionEvent) {
        String message = searchInput.getText().trim();
        updateMSG(User.getIdentifiant(),message);
        searchInput.setText("");
    }

    public void handleSendMessageClick(ActionEvent actionEvent) {
        String message = messageInput.getText().trim();
        if(!message.isEmpty() && updateMSG(userName.getText(),message)) HistoryBuilder.write(userName.getText(), toName, message);
        messageInput.clear();
    }

    public boolean updateMSG(String username, String message) {
        Text text = new Text(message);
        text.setFill(Color.BLACK);
        text.getStyleClass().add("message");
        TextFlow tempFlow = new TextFlow();

        tempFlow.getChildren().add(text);
        tempFlow.setMaxWidth(400);
        TextFlow flow = new TextFlow(tempFlow);
        HBox hbox = new HBox(12);

        if(!User.getIdentifiant().equals(username)) {
            tempFlow.getStyleClass().add("tempFlowFlipped");
            flow.getStyleClass().add("textFlowFlipped");
            chatBox.setAlignment(Pos.TOP_LEFT);
            hbox.setAlignment(Pos.CENTER_LEFT);
        } else {
            text.setFill(Color.BLACK);
            tempFlow.getStyleClass().add("tempFlow");
            flow.getStyleClass().add("textFlow");
            hbox.setAlignment(Pos.BOTTOM_RIGHT);
        }
        hbox.getChildren().add(flow);
        hbox.getStyleClass().add("hbox");
        Platform.runLater(()-> chatBox.getChildren().addAll(hbox));
        return true;
    }

    public void updateUI(ArrayList<String> contactList) {
        Platform.runLater(() -> clientListBox.getChildren().clear());
        for(String contact : contactList){
            if(contact.equals(this.userName.getText())) continue;
            HBox container = new HBox();
            container.setAlignment(Pos.CENTER);
            container.setSpacing(20);
            container.setPrefWidth(clientListBox.getPrefWidth());
            container.setPadding(new Insets(10));
            container.getStyleClass().add("online-user-container");
            //Photo de profile
            Circle img =  new Circle(60,60,30);
            try {
                var path = new File(String.format("src/ui/ressources/profile/%s.png",contact)).toURI().toString();
                URL imgUrl = getClass().getResource(path);
                if(imgUrl == null) path = new File("src/ui/ressources/profile/default.png").toURI().toString();
                img.setFill(new ImagePattern(new Image(path)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            container.getChildren().add(img);

            //Nom contact
            VBox userDetailContainer = new VBox();
            userDetailContainer.setPrefWidth(clientListBox.getPrefWidth()/1.5);
            Label lblUsername = new Label(contact);
            lblUsername.getStyleClass().add("online-label");
            userDetailContainer.getChildren().add(lblUsername);

            ArrayList<ArrayList<String>> history = new  ArrayList<>(HistoryBuilder.read(contact));
            String lastMessage = history.size() > 0 ? history.get(history.size()-1).get(2) : "";

            //Dernier message
            Label lblname = new Label(lastMessage);
            lblname.getStyleClass().add("online-label-details");
            userDetailContainer.getChildren().add(lblname);
            container.getChildren().add(userDetailContainer);

            //Option
            Label settings = new Label("...");
            settings.getStyleClass().add("online-settings");
            container.getChildren().add(settings);

            container.setOnMouseClicked(e -> {
                clientListBox.getChildren().forEach(client -> client.getStyleClass().remove("online-user-container-active"));
                container.getStyleClass().add("online-user-container-active");
                changeChat(lblUsername.getText());
            });
            Platform.runLater(() -> clientListBox.getChildren().add(container));
        }
    }

    public void changeChat (String to){
        Platform.runLater(()-> chatBox.getChildren().removeAll(chatBox.getChildren()));
        contactName.setText(to);
        messageInput.clear();
        ArrayList<ArrayList<String>> history = new  ArrayList<>(HistoryBuilder.read(to));
        if(!history.isEmpty()) history.forEach(line -> updateMSG(line.get(1), line.get(2)));
    }

    public void closeBtn (){}
}
