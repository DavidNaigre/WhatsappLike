package ui.chat;

import function.TextConstructor;
import function.messages.HistoryBuilder;
import function.user.User;
import function.user.UserAction;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatView implements Initializable {
    @FXML private ScrollPane scrollPane;
    @FXML private VBox clientListBox, chatBox;
    @FXML private TextField searchInput, messageInput, newContactInput;
    @FXML private TextFlow serverReponseBox;
    @FXML private Text errorMessageMail, serverReponseText;
    @FXML private Label userName, contactName, concactNameDeleteLabel;
    @FXML private ImageView userImageProfile, contactImageProfile;
    @FXML private Button sendButton, closeButton, reduceButton, newContactSend;
    @FXML private Pane startPane, titleBar, newRelationPane, overlayPane;
    private double xOffset,yOffset;
    private String contactId = "";

    private Map<String, String> initContactList;
    private boolean lastMessageThreadStop = true;
    private int nbrOfContact;
    private static final Pattern VALIDEMAIL = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private final int THREADS_NUMBER_ALLOW = 2;
    private final  ScheduledExecutorService pool = Executors.newScheduledThreadPool(THREADS_NUMBER_ALLOW);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sendButton.setDisable(true);
        userName.setText(User.getIdentifiant());
        titleBar.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        titleBar.setOnMouseDragged(e -> {
            titleBar.getScene().getWindow().setX(e.getScreenX() - xOffset);
            titleBar.getScene().getWindow().setY(e.getScreenY() - yOffset);
        });
        newContactSend.setDisable(true);
        newContactInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newContactInput.getText().isEmpty()) {
                errorMessageMail.setText("Veuillez compléter ce champ");
                errorMessageMail.setVisible(true);
                newContactSend.setDisable(true);
            } else {
                if (!checkMail(newContactInput.getText())){
                    errorMessageMail.setText("Veuillez renseigner une adresse email valide");
                    errorMessageMail.setVisible(true);
                    newContactSend.setDisable(true);
                } else {
                    errorMessageMail.setText("");
                    errorMessageMail.setVisible(false);
                    newContactSend.setDisable(false);
                }
            }
        });

        startPane.visibleProperty().addListener(observable -> {
            if(startPane.isVisible()){
                searchInput.setDisable(true);
                messageInput.setDisable(true);
            } else {
                searchInput.setDisable(false);
                messageInput.setDisable(false);
            }
        });
        try {
            var path = new File("src/ui/ressources/profile/user.png").toURI().toString();
            URL imgUrl = getClass().getResource(path);
            if(imgUrl == null) path = new File("src/ui/ressources/profile/default.png").toURI().toString();
            userImageProfile.setImage(new Image(path));
            userImageProfile.getStyleClass().add("image-view");

        } catch (Exception e) {
            e.printStackTrace();
        }
        messageInput.textProperty().addListener(observable-> sendButton.setDisable(messageInput.getText().isEmpty()));
        chatBox.heightProperty().addListener(observable -> {
            scrollPane.layout();
            scrollPane.setVvalue(1D);
        });
        initContactList = updateContactList();
        nbrOfContact = initContactList.size();
        pool.scheduleAtFixedRate(()->{
            if (searchInput.getText().isEmpty()) {
                int tempNbrOfContact = updateContactList().size();
                if(tempNbrOfContact > nbrOfContact) System.out.println((tempNbrOfContact - nbrOfContact)+" contact add");
                else if(tempNbrOfContact < nbrOfContact) System.out.println((nbrOfContact - tempNbrOfContact)+" contact removed");
                nbrOfContact = tempNbrOfContact;
            }
        },2000,2000, TimeUnit.MILLISECONDS);
        pool.scheduleAtFixedRate(()->{
            if(!contactId.isEmpty()){
                ArrayList<ArrayList<String>> messageList = UserAction.readMessage(contactId);
                messageList.forEach(message -> {
                    updateMSG(message.get(0), message.get(1));
                    HistoryBuilder.write(contactId, message.get(0), message.get(1));
                });
            }
        },500, 500, TimeUnit.MILLISECONDS);
        pool.scheduleAtFixedRate(()->{
            if(lastMessageThreadStop){
                contactLastMessage();
            }
        },1000 ,1000, TimeUnit.MILLISECONDS);
    }
    public void contactLastMessage () {
        System.out.println("IN LAST MESSAGE METHOD \n");
        String state;
        for ( Node child : clientListBox.getChildren() ){
            String id = child.getId(), messageInfo = ((String) child.getUserData());
            try {
                JSONObject json = (JSONObject) new JSONParser().parse(messageInfo);
                if(json.containsKey("message")) {
                    String jsonLastMessage = (String) json.get("message");
                    ArrayList<ArrayList<String>> messageList = UserAction.readMessage(id);
                    messageList.forEach(message -> HistoryBuilder.write(id, message.get(0), message.get(1)));
                    JSONObject jsonTemp = (JSONObject) new JSONParser().parse(HistoryBuilder.lastMessage(id));
                    messageInfo = jsonTemp.toString();
                    String lastMessage = (String) jsonTemp.get("message");
                    if(child instanceof  HBox){
                        if(messageList.size() > 0) state = "Nouveau message";
                        else state = "";
                        Label lblname = new Label(state);
                        lblname.getStyleClass().add("online-label-details");
                        Platform.runLater(() -> ((VBox)((HBox)child).getChildren().get(1)).getChildren().set(1,lblname));
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println(id+" : "+ messageInfo);
        }
    }

    public void handleEnterSearchKeyReleased() {
        String message = searchInput.getText().trim();
        updateMSG(User.getIdentifiant(),message);
        searchInput.setText("");
    }

    public void handleSendMessageClick() {
        String message = messageInput.getText().trim();
        UserAction.sendMessage(contactId, message);
        messageInput.clear();
    }

    public void updateMSG(String username, String message) {
        TextFlow tempFlow = TextConstructor.BOLD(message);
        tempFlow.setMaxWidth(400);
        TextFlow flow = new TextFlow(tempFlow);
        HBox hbox = new HBox(12);

        if (!User.getIdentifiant().equals(username)) {
            tempFlow.getStyleClass().add("tempFlowFlipped");
            flow.getStyleClass().add("textFlowFlipped");
            chatBox.setAlignment(Pos.TOP_LEFT);
            hbox.setAlignment(Pos.CENTER_LEFT);
        } else {
            tempFlow.getStyleClass().add("tempFlow");
            flow.getStyleClass().add("textFlow");
            hbox.setAlignment(Pos.BOTTOM_RIGHT);
        }
        hbox.getChildren().add(flow);
        hbox.getStyleClass().add("hbox");
        Platform.runLater(() -> chatBox.getChildren().addAll(hbox));
    }

    private Map<String, String> updateContactList() {
        lastMessageThreadStop = false;
        Map<String, String> contactList = UserAction.getListRelation();
        if (!contactList.equals(initContactList)) {
            Platform.runLater(() -> clientListBox.getChildren().clear());
            for ( Map.Entry<String, String> entry : contactList.entrySet() ) {
                String id = entry.getKey();
                String name = entry.getValue();

                if (name.equals(this.userName.getText())) continue;
                HBox container = new HBox();
                container.setAlignment(Pos.CENTER);
                container.setSpacing(20);
                container.setPrefWidth(clientListBox.getPrefWidth());
                container.setPadding(new Insets(10));
                container.getStyleClass().add("online-user-container");
                //Photo de profile
                Circle img = new Circle(60, 60, 30);
                try {
                    var path = new File(String.format("src/ui/ressources/profile/%s.png", name)).toURI().toString();
                    URL imgUrl = getClass().getResource(path);
                    if (imgUrl == null) path = new File("src/ui/ressources/profile/default.png").toURI().toString();
                    img.setFill(new ImagePattern(new Image(path)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                container.getChildren().add(img);

                //Nom contact
                VBox userDetailContainer = new VBox();
                userDetailContainer.setPrefWidth(clientListBox.getPrefWidth() / 1.5);
                Label lblUsername = new Label(name);
                lblUsername.getStyleClass().add("online-label");
                userDetailContainer.getChildren().add(lblUsername);

                //Dernier message
                String lastMessage = "", messageInfo = HistoryBuilder.lastMessage(id);
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(messageInfo);
                    lastMessage = json.containsKey("message") ? (String) json.get("message") : "";

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Label lblname = new Label("");
                lblname.getStyleClass().add("online-label-details");
                userDetailContainer.getChildren().add(lblname);
                container.getChildren().add(userDetailContainer);

                container.setOnMouseClicked(e -> {
                    if(!id.equals(contactId)) {
                        startPane.setVisible(false);
                        startPane.setDisable(true);
                        lblname.setText("");
                        clientListBox.getChildren().forEach(client -> client.getStyleClass().remove("online-user-container-active"));
                        container.getStyleClass().add("online-user-container-active");
                        changeChat(lblUsername.getText(), id);
                    }
                });
                container.setId(id);
                container.setUserData(messageInfo);
                Platform.runLater(() -> clientListBox.getChildren().add(container));
            }
            initContactList = contactList;
        }
        lastMessageThreadStop = true;
        return contactList;
    }

    public void changeChat(String to, String id){
        Platform.runLater(()-> chatBox.getChildren().removeAll(chatBox.getChildren()));
        contactName.setText(to);
        contactId = id;
        messageInput.clear();
        try {
            var path = new File(String.format("src/ui/ressources/profile/%s.png",contactName.getText())).toURI().toString();
            URL imgUrl = getClass().getResource(path);
            if(imgUrl == null) path = new File("src/ui/ressources/profile/default.png").toURI().toString();
            contactImageProfile.setImage(new Image(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<ArrayList<String>> history = new  ArrayList<>(HistoryBuilder.read(contactId));
        if(!history.isEmpty()) history.forEach(line -> updateMSG(line.get(1), line.get(2)));
    }

    public void handleNewRelationButton() {
        if(!newRelationPane.isVisible()) {
            newRelationPane.setVisible(true);
            searchInput.setDisable(true);
        } else {
            newRelationPane.setVisible(false);
            searchInput.setDisable(false);
            serverReponseBox.setVisible(false);
        }
    }

    public void handleNewContactCancel() {
        newRelationPane.setVisible(false);
        searchInput.setDisable(false);
        serverReponseBox.setVisible(false);
    }

    public void handelNewContactSend() {
        String mail = newContactInput.getText();
        if(!mail.isEmpty() && checkMail(mail)){
            Task task = new Task<Void>() {
                @Override
                protected Void call() {
                    serverReponseBox.getStyleClass().removeAll("request-statut-fail","request-statut-success");
                    if(UserAction.newRelation(mail)) {
                        updateContactList();
                        serverReponseBox.getStyleClass().add("request-statut-success");
                        serverReponseText.setText("Nouvelle relation créé");
                        serverReponseText.setFill(Color.valueOf("#155724"));
                    } else {
                        serverReponseBox.getStyleClass().add("request-statut-fail");
                        serverReponseText.setText("Adresse e-mail invalide");
                        serverReponseText.setFill(Color.valueOf("#721c24"));
                    }
                    serverReponseBox.setVisible(true);
                    return null;
                }
            };
            new Thread(task).start();
        }
    }

    public void closeBtn(){
        pool.shutdown();
        Runtime.getRuntime().exit(0);
        ((Stage)closeButton.getScene().getWindow()).close();
    }

    public void handleReduceButton() {
        ((Stage)reduceButton.getScene().getWindow()).setIconified(true);
    }

    public boolean checkMail(String emailStr) {
        Matcher matcher = VALIDEMAIL.matcher(emailStr);
        return matcher.find();
    }

    public void handleDeleteFriendButton() {
        concactNameDeleteLabel.setText(contactName.getText());
        overlayPane.setVisible(true);
    }

    public void handleDeleteFriendCancelButton() {
        overlayPane.setVisible(false);
    }

    public void handleDeleteFriendSendButton() {
        if(UserAction.delRelation(contactId)){
            try {
                File file = new File("../../function/messages/history/"+contactId+".json");
                file.delete();
                contactId = "";
                contactName.setText("");
                startPane.setVisible(true);
                overlayPane.setVisible(false);
                updateContactList();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

