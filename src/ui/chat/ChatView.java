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
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.json.simple.parser.ParseException;

import java.io.IOException;
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
        messageInput.textProperty().addListener((observable, oldValue, newValue)->{
            sendButton.setDisable(messageInput.getText().isEmpty());
        });

        ArrayList<ArrayList<String>> history = new  ArrayList<>(HistoryBuilder.read(toName));
        if(!history.isEmpty()){
            history.forEach(line -> updateMSG(line.get(1), line.get(2)));
            scrollPane.setVvalue(1.0);
        }
        updateUI(HistoryBuilder.getContactList());
    }


    public void handleEnterSearchKeyReleased(ActionEvent actionEvent) {
        String message = searchInput.getText().trim();
        updateMSG(User.getIdentifiant(),message);
        searchInput.setText("");
    }

    public void handleSendMessageClick(ActionEvent actionEvent) {
        String message = messageInput.getText().trim();
        if(!message.isEmpty() && updateMSG(toName,message)) scrollPane.setVvalue(1.0);
        messageInput.setText("");
    }

    public boolean updateMSG(String username, String message){
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
        try {
            HistoryBuilder.write(User.getIdentifiant(),toName,message);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        Platform.runLater(()->chatBox.getChildren().addAll(hbox));
        return true;

    }

    public void updateUI(ArrayList<String> contactList) {
        Platform.runLater(() -> clientListBox.getChildren().clear());
        for(String contact : contactList){

            if(contact.equals(this.userName.getText())) continue;
            HBox container = new HBox();
            container.setAlignment(Pos.CENTER_LEFT);
            container.setSpacing(10);
            container.setPrefWidth(clientListBox.getPrefWidth());
            container.setPadding(new Insets(3));
            container.getStyleClass().add("online-user-container");

//            Circle img =  new Circle(30,30,15);
//            try {
//                String path = new File("../resources/profile/default.png").toURI().toString();
//                System.out.println(path);
////                String path = new File(String.format("",contact)).toURI().toString();
//                img.setFill(new ImagePattern(new Image("../resources/profile/default.png")));
//            } catch (Exception e) {
//                String path = new File("ui/resources/profile/default.png").toURI().toString();
//                img.setFill(new ImagePattern(new Image(path)));
//            }
//            container.getChildren().add(img);

            VBox userDetailContainer = new VBox();
            userDetailContainer.setPrefWidth(clientListBox.getPrefWidth()/1.7);
            Label lblUsername = new Label(contact);
            lblUsername.getStyleClass().add("online-label");
            userDetailContainer.getChildren().add(lblUsername);

            ArrayList<ArrayList<String>> history = new  ArrayList<>(HistoryBuilder.read(contact));
            String lastMessage = history.get(history.size()-1).get(2);

            Label lblname = new Label(lastMessage);
            lblname.getStyleClass().add("online-label-details");
            userDetailContainer.getChildren().add(lblname);
            container.getChildren().add(userDetailContainer);

            Label settings = new Label("...");
            settings.getStyleClass().add("online-settings");
            container.getChildren().add(settings);

            System.out.println(container.getChildren().size());
            Platform.runLater(() -> clientListBox.getChildren().add(container));
        }
    }
}
