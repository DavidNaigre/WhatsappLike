package ui.chat;

import function.TextConstructor;
import function.appPath;
import function.messages.HistoryBuilder;
import function.user.User;
import function.user.UserAction;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    @FXML private ScrollPane scrollPane,clientListScrollSearch;
    @FXML private VBox clientListBox, chatBox, clientListBoxSearch;
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
//            String path= new File(imagePath+"user.png").toURI().toString();
            URL imageUrl = ClassLoader.getSystemResource("user.png");
            userImageProfile.setImage(new Image(String.valueOf(imageUrl)));
        } catch (Exception e) {
//            String path= new File(imagePath+"default.png").toURI().toString();
            URL imageUrl = ClassLoader.getSystemResource("default.png");
            userImageProfile.setImage(new Image(String.valueOf(imageUrl)));
        }
        userImageProfile.getStyleClass().add("image-view");

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

//        pool.scheduleAtFixedRate(()->{
//            if(lastMessageThreadStop){
//                contactLastMessage();
//            }
//        },1000 ,1000, TimeUnit.MILLISECONDS);
    }

    public void contactLastMessage () {
        System.out.println("IN LAST MESSAGE METHOD \n");
        Label lblname = new Label();
        lblname.getStyleClass().add("online-label-details");

        for ( Node child : clientListBox.getChildren() ){
            String id = child.getId();
            ArrayList<ArrayList<String>> messageList = UserAction.readMessage(id);
            messageList.forEach(message -> HistoryBuilder.write(id, message.get(0), message.get(1)));
            String child_messageInfo = ((String) child.getUserData());
            String new_messageInfo = HistoryBuilder.lastMessage(id);
            System.out.println(id+" : "+ new_messageInfo);
            try {
                JSONObject json = (JSONObject) new JSONParser().parse(new_messageInfo);
                if(json.containsKey("from") && json.containsKey("message")) {
                    if( id.equals(contactId)) {
                        lblname.setText("");
                        Platform.runLater(() -> ((VBox)((HBox)child).getChildren().get(1)).getChildren().set(1,lblname));
                    } else if(!new_messageInfo.equals(child_messageInfo)){
                        String expediteur = (String) json.get("from");
                        if (!expediteur.equals(this.userName.getText())) {
                            lblname.setText("Nouveau message !");
                            child.setUserData(new_messageInfo);
                            Platform.runLater(() -> ((VBox) ((HBox) child).getChildren().get(1)).getChildren().set(1, lblname));
                        }
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
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

    public HBox containerBuilder (String id, String name){
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);
        container.setSpacing(20);
        container.setPrefWidth(clientListBoxSearch.getPrefWidth());
        container.setPadding(new Insets(10));
        container.getStyleClass().add("online-user-container");
        //Photo de profile
        Circle img = new Circle(60, 60, 30);
        try {
            URL imgUrl =  ClassLoader.getSystemResource(String.format("%s.png", name));
            if (imgUrl == null) imgUrl =  ClassLoader.getSystemResource("default.png");
            img.setFill(new ImagePattern(new Image(imgUrl.toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        container.getChildren().add(img);

        //Nom contact
        VBox userDetailContainer = new VBox();
        userDetailContainer.setPrefWidth(clientListBoxSearch.getPrefWidth() / 1.5);
        Label lblUsername = new Label(name);
        lblUsername.getStyleClass().add("online-label");
        userDetailContainer.getChildren().add(lblUsername);

        //Dernier message
        Label lblname = new Label("");
        lblname.getStyleClass().add("online-label-details");
        userDetailContainer.getChildren().add(lblname);
        container.getChildren().add(userDetailContainer);

        container.setId(id);
        String messageInfo = HistoryBuilder.lastMessage(id);
        container.setUserData(messageInfo);
        return container;
    }

    private Map<String, String> updateContactList() {
        Map<String, String> contactList = UserAction.getListRelation();
        if (!contactList.equals(initContactList)) {
            Platform.runLater(() -> clientListBox.getChildren().clear());
            for ( Map.Entry<String, String> entry : contactList.entrySet() ) {
                String id = entry.getKey(), name = entry.getValue();
                if (name.equals(this.userName.getText())) continue;
                HBox container = containerBuilder(id,name);
                container.setOnMouseClicked(e -> {
                    if(!id.equals(contactId)) {
                        startPane.setVisible(false);
                        startPane.setDisable(true);
                        clientListBox.getChildren().forEach(client -> client.getStyleClass().remove("online-user-container-active"));
                        container.getStyleClass().add("online-user-container-active");
                        changeChat(name, id);
                    }
                });
                Platform.runLater(() -> clientListBox.getChildren().add(container));
            }
            initContactList = contactList;
        }
        return contactList;
    }

    public void handleEnterSearchKeyReleased(Event event) {
        clientListScrollSearch.setVisible(true);
        Platform.runLater(() -> clientListBoxSearch.getChildren().clear());
        if (event.getEventType() == KeyEvent.KEY_PRESSED && ((KeyEvent)event).getCode() == KeyCode.ESCAPE) {
            searchInput.clear();
            clientListBoxSearch.setVisible(false);
        }
        else {
            String search = searchInput.getText().trim();
            Map<String, String> contactList = UserAction.getListRelation();
            if(search.length() > 0 ) contactList.values().removeIf(value -> !value.toLowerCase().contains(search.toLowerCase()));

            for ( Map.Entry<String, String> entry : contactList.entrySet() ) {
                String id = entry.getKey();
                String name = entry.getValue();
                HBox container = containerBuilder(id,name);
                container.setOnMouseClicked(e -> {
                    searchInput.clear();
                    clientListBox.getChildren().forEach(client -> client.getStyleClass().remove("online-user-container-active"));
                    clientListBox.lookup("#"+id).getStyleClass().add("online-user-container-active");
                    clientListScrollSearch.setVisible(false);
                    startPane.setVisible(false);
                    startPane.setDisable(true);
                    changeChat(name, id);
                });
                Platform.runLater(() -> clientListBoxSearch.getChildren().add(container));
            }
        }
    }

    public void changeChat(String to, String id){
        Platform.runLater(()-> chatBox.getChildren().removeAll(chatBox.getChildren()));
        contactName.setText(to);
        contactId = id;
        messageInput.clear();
        try {
            URL imgUrl =  ClassLoader.getSystemResource(String.format("%s.png", contactName.getText()));
            if (imgUrl == null) imgUrl =  ClassLoader.getSystemResource("default.png");
            contactImageProfile.setImage(new Image(imgUrl.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<ArrayList<String>> history = new  ArrayList<>(HistoryBuilder.read(contactId));
        if(!history.isEmpty()) history.forEach(line -> updateMSG(line.get(1), line.get(2)));
    }

    public void heandleSearchOptionLeave() {
        searchInput.clear();
        clientListScrollSearch.setVisible(false);
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
                File file = new File(appPath.getMessageDIRECTORY()+contactId+".json");
                if (file.delete()) System.out.println("History successfuly delete");
                else System.out.println("History failed to delete");
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

    public void closeBtn(){
        pool.shutdown();
        Runtime.getRuntime().exit(0);
        ((Stage)closeButton.getScene().getWindow()).close();
    }

    public void handleReduceButton() {
        ((Stage)reduceButton.getScene().getWindow()).setIconified(true);
    }
}

