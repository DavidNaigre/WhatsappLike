package ui.auth.create;

import function.user.UserAction;
import function.user.UserTemp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccountController implements Initializable {
    @FXML private TextField mailField, pseudoField;
    @FXML private Text errorMessageMail, errorMessagePseudo;
    @FXML private Button transmitButton, closeButton, reduceButton;
    private Boolean mailCheck, PseudoCheck;
    @FXML private Pane titleBar;
    private double xOffset,yOffset;

    public static final Pattern VALIDEMAIL = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALIDEPSEUDO = Pattern.compile("^[a-zA-Z0-9]{3,10}$", Pattern.CASE_INSENSITIVE);

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
        errorMessageMail.setVisible(false);
        errorMessagePseudo.setVisible(false);
        transmitButton.setDisable(true);
        mailCheck = false;
        PseudoCheck = false;

        mailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(mailField.getText().isEmpty()) {
                errorMessageMail.setText("Veuillez compléter ce champ");
                errorMessageMail.setVisible(true);
                mailCheck = false;
            } else {
                if (checkMail(mailField.getText())){
                    errorMessageMail.setText("Veuillez renseigner une adresse email valide");
                    errorMessageMail.setVisible(true);
                    mailCheck = false;
                } else {
                    errorMessageMail.setText("");
                    errorMessageMail.setVisible(false);
                    mailCheck = true;
                }
            }
        });
        pseudoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(pseudoField.getText().isEmpty()){
                errorMessagePseudo.setText("Veuillez compléter ce champ");
                errorMessagePseudo.setVisible(true);
                PseudoCheck = false;
            } else {
                if(!checkPseudo(pseudoField.getText())){
                    errorMessagePseudo.setText("Veuillez renseigner un pseudonyme valide :"
                            +"\n-Pas de caractères spéciaux"
                            +"\n-Pseudonyme d'au moins 3 lettres, au plus 10 lettres");
                    errorMessagePseudo.setVisible(true);
                    PseudoCheck = false;
                } else{
                    errorMessagePseudo.setText("");
                    errorMessagePseudo.setVisible(false);
                    PseudoCheck = true;
                }
            }
        });
    }

    @FXML
    public void handleBackButtonAction(ActionEvent actionEvent) throws Exception {
        Parent home = FXMLLoader.load(getClass().getResource("../home/HomeView.fxml"));
        Scene homeScene = new Scene(home);
        Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(homeScene);
        window.centerOnScreen();
        window.show();
    }

    @FXML
    public void handleCreateAccountButtonAction(ActionEvent actionEvent) throws IOException {
        if(mailField.getText().isEmpty() || pseudoField.getText().isEmpty() || checkMail(mailField.getText()) || pseudoField.getLength() < 3)
            System.out.println("Observable failed");
        else {
            UserTemp.setMail(mailField.getText());
            UserTemp.setIdentifiant(pseudoField.getText());
            if (UserAction.createAccount(pseudoField.getText(), mailField.getText())) {
                System.out.println("Compte créé");
            } else System.out.println("Carotte la tchuite");
            Parent login = FXMLLoader.load(getClass().getResource("../login/LoginView.fxml"));
            Scene homeScene = new Scene(login);
            Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            window.setScene(homeScene);
            window.centerOnScreen();
            window.show();
        }
    }

    public boolean checkMail(String emailStr) {
        Matcher matcher = VALIDEMAIL.matcher(emailStr);
        return !matcher.find();
    }

    public boolean checkPseudo(String pseudoStr){
        Matcher matcher = VALIDEPSEUDO.matcher(pseudoStr);
        return matcher.find();
    }

    public void disableButton() {
        transmitButton.setDisable(!mailCheck || !PseudoCheck);
    }

    public void closeBtn(){
        Runtime.getRuntime().exit(0);
        ((Stage)closeButton.getScene().getWindow()).close();
    }
    public void handleReduceButton() {
        ((Stage)reduceButton.getScene().getWindow()).setIconified(true);
    }
}
