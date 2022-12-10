package com.example.socialnetworkgui.controllers;

import com.example.socialnetworkgui.Application;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.service.Network;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;

    @FXML
    Label labelLogInErrors;

    Network network;

    public void setNetwork(Network network){this.network = network;}
    @FXML
    protected void logIn(ActionEvent actionEvent){
        try{
            String username = usernameField.getText();
            String password = passwordField.getText();
            //network = new Network();
            User loggedUser = network.verifyUser(username, password);
            network.setCurrentUser(loggedUser);

            usernameField.clear();
            passwordField.clear();
            labelLogInErrors.setText("");

            /* intra in pagina userului */
            FXMLLoader userAccPage = new FXMLLoader(Application.class.getResource("UserAccountPage.fxml"));
            Scene scene = new Scene(userAccPage.load());
            Stage stage = new Stage();
            stage.setTitle("User:" + loggedUser.getUsername());
            stage.setScene(scene);

            UserAccountController userAccountController = userAccPage.getController();
            userAccountController.setNetwork(network);

            stage.show();
            ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
        } catch (Exception e){
            labelLogInErrors.setText(e.getMessage());
            usernameField.clear();
            passwordField.clear();
        }
    }

    @FXML
    protected void onSignUpLabel(){
        try{
            FXMLLoader signUpLoader = new FXMLLoader(Application.class.getResource("SignUp.fxml"));
            Scene scene = new Scene(signUpLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Sign up");
            stage.setScene(scene);
            SignUpController signUpController = signUpLoader.getController();
            signUpController.setNetwork(network);
            stage.show();
        }catch (Exception e){
            System.out.println(e.getMessage());
            //labelLogInErrors.setText(e.getMessage());
        }
    }
}
