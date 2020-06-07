package ui.chat;

import function.messages.HistoryBuilder;
import function.user.User;
import function.user.UserAction;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

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
    @FXML private Label userName, contactName;
    @FXML private ImageView userImageProfile, contactImageProfile;
    @FXML private Button sendButton, closeButton, reduceButton, newContactSend;
    @FXML private Pane startPane, titleBar, newRelationPane;
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
            int tempNbrOfContact = updateContactList().size();
            if(tempNbrOfContact > nbrOfContact) System.out.println((tempNbrOfContact - nbrOfContact)+" contact add");
            else if(tempNbrOfContact < nbrOfContact) System.out.println((nbrOfContact - tempNbrOfContact)+" contact removed");
            nbrOfContact = tempNbrOfContact;
        },2000,2000, TimeUnit.MILLISECONDS);
        pool.scheduleAtFixedRate(()->{
            if(!contactId.isEmpty()){
                ArrayList<ArrayList<String>> messageList = UserAction.readMessage(contactId);
                for(ArrayList<String> message : messageList){
                    updateMSG(message.get(0), message.get(1));
                    HistoryBuilder.write(contactId,message.get(0), message.get(1));
                }
            }
        },1000, 1000, TimeUnit.MILLISECONDS);
    }

    public void handleEnterSearchKeyReleased(ActionEvent actionEvent) {
        String message = searchInput.getText().trim();
        updateMSG(User.getIdentifiant(),message);
        searchInput.setText("");
    }

    public void handleSendMessageClick(ActionEvent actionEvent) {
        String message = messageInput.getText().trim();
        UserAction.sendMessage(contactId, message);
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

            if (!User.getIdentifiant().equals(username)) {
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
            Platform.runLater(() -> chatBox.getChildren().addAll(hbox));
            return true;
    }

    private Map<String, String> updateContactList() {
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


                //TODO : refaire le truc de last message
//            ArrayList<ArrayList<String>> history = new ArrayList<>(HistoryBuilder.read(name));
//            String lastMessage = history.size() > 0 ? history.get(history.size() - 1).get(2) : "";

                //Dernier message
                Label lblname = new Label("");
                lblname.getStyleClass().add("online-label-details");
                userDetailContainer.getChildren().add(lblname);
                container.getChildren().add(userDetailContainer);

                //Option
                Label settings = new Label("...");
                settings.getStyleClass().add("online-settings");
                container.getChildren().add(settings);

                container.setOnMouseClicked(e -> {
                    startPane.setVisible(false);
                    startPane.setDisable(true);
                    clientListBox.getChildren().forEach(client -> client.getStyleClass().remove("online-user-container-active"));
                    container.getStyleClass().add("online-user-container-active");
                    changeChat(lblUsername.getText(), id);
                });
                Platform.runLater(() -> clientListBox.getChildren().add(container));
            }
            initContactList = contactList;
        }
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

    public void handleOptionPaneButton(ActionEvent actionEvent) {
    }

    public void handleNewRelationButton(ActionEvent actionEvent) {
        if(!newRelationPane.isVisible()) {
            newRelationPane.setVisible(true);
            searchInput.setDisable(true);
        } else {
            newRelationPane.setVisible(false);
            searchInput.setDisable(false);
            serverReponseBox.setVisible(false);
        }
    }

    public void handleNewContactCancel(ActionEvent actionEvent) {
        newRelationPane.setVisible(false);
        searchInput.setDisable(false);
        serverReponseBox.setVisible(false);
    }

    public void handelNewContactSend(ActionEvent actionEvent) {
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

    public void closeBtn (ActionEvent actionEvent){
        pool.shutdown();
        Runtime.getRuntime().exit(0);
        ((Stage)closeButton.getScene().getWindow()).close();
    }

    public void handleReduceButton(ActionEvent actionEvent) {
        ((Stage)reduceButton.getScene().getWindow()).setIconified(true);
    }

    public boolean checkMail(String emailStr) {
        Matcher matcher = VALIDEMAIL.matcher(emailStr);
        return matcher.find();
    }
}
