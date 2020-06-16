package ui.auth.home;

import function.user.AuthUser;
import function.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import ui.auth.login.conServ;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    @FXML private Button closeButton, reduceButton;
    @FXML private Pane titleBar;
    @FXML private VBox menuVbox;
    private double xOffset,yOffset;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleBar.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        titleBar.setOnMouseDragged(e -> {
            titleBar.getScene().getWindow().setX(e.getScreenX() - xOffset);
            titleBar.getScene().getWindow().setY(e.getScreenY() - yOffset);
        });
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        Button continueButton = new Button(""), newConnexionButton = new Button("Se connecter"), createAccountButton = new Button("Créer un compte");
        continueButton.setPrefWidth(250);
        continueButton.setPrefHeight(50);
        continueButton.setCursor(Cursor.HAND);
        continueButton.getStyleClass().add("button-home");
        continueButton.setOnAction(actionEvent -> handleSameAccountButtonClick(actionEvent));

        newConnexionButton.setPrefWidth(250);
        newConnexionButton.setPrefHeight(50);
        newConnexionButton.setCursor(Cursor.HAND);
        newConnexionButton.getStyleClass().add("button-home");
        newConnexionButton.setOnAction(actionEvent -> handleConnectButtonClick(actionEvent));

        createAccountButton.setPrefWidth(250);
        createAccountButton.setPrefHeight(50);
        createAccountButton.setCursor(Cursor.HAND);
        createAccountButton.getStyleClass().add("button-home");
        createAccountButton.setOnAction(actionEvent -> handleNewAccountButtonClick(actionEvent));

        Line l1 = new Line(), l2 = new Line();
        l1.setFill(Color.BLACK);
        l1.setStartX(-254.05349731445312);
        l1.setEndX(-3.7606048583984375);
        l1.setOpacity(0.19);
        vbox.setMargin(l1,new Insets(7,0,7,0));

        l2.setFill(Color.BLACK);
        l2.setStartX(-254.05349731445312);
        l2.setEndX(-3.7606048583984375);
        l2.setOpacity(0.19);
        vbox.setMargin(l2,new Insets(7,0,7,0));

        if(AuthUser.isAuth()){
            continueButton.setText(User.getIdentifiant());
            vbox.getChildren().add(continueButton);
            vbox.getChildren().add(l1);
        }
        vbox.getChildren().add(newConnexionButton);
        vbox.getChildren().add(l2);
        vbox.getChildren().add(createAccountButton);
        menuVbox.getChildren().addAll(vbox);
    }


    public void handleSameAccountButtonClick(ActionEvent actionEvent){
        try {
            Parent chat = FXMLLoader.load(getClass().getResource("../../chat/ChatView.fxml"));
            Scene chatScene = new Scene(chat);
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(chatScene);
            window.centerOnScreen();
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleNewAccountButtonClick(ActionEvent actionEvent){
        try {
            Parent home = FXMLLoader.load(getClass().getResource("../create/CreateAccountView.fxml"));
            Scene homeScene = new Scene(home);
            Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            window.setScene(homeScene);
            window.centerOnScreen();
            window.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleConnectButtonClick(ActionEvent actionEvent){
        try  {
            conServ.setCon(true);
            Parent login = FXMLLoader.load(getClass().getResource("../login/LoginView.fxml"));
            Scene loginScene = new Scene(login);
            Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            window.setScene(loginScene);
            window.centerOnScreen();
            window.show();
        } catch (IOException e){
            e.printStackTrace();
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
