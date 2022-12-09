package com.example.socialnetworkgui.controllers;

import com.example.socialnetworkgui.service.Network;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignUpController {
    @FXML
    TextField usernameFieldSignUp;
    @FXML
    PasswordField passwordFieldSignUp;
    @FXML
    TextField emailFieldSignUp;
    @FXML
    Label labelErrorSignUp;
    Network network;

    public void setNetwork(Network network){this.network = network;}
    @FXML
    protected void onSignUp(){
        try {
            String username = usernameFieldSignUp.getText();
            String password = passwordFieldSignUp.getText();
            String email = emailFieldSignUp.getText();
            network.addUser(username,password,email);

            usernameFieldSignUp.clear();
            passwordFieldSignUp.clear();
            emailFieldSignUp.clear();
            labelErrorSignUp.setText("You signed up successfuly!");
        }catch (Exception e){
            labelErrorSignUp.setText(e.getMessage());
        }
    }
}
