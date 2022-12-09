package com.example.socialnetworkgui;

import com.example.socialnetworkgui.controllers.LoginController;
import com.example.socialnetworkgui.service.Network;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    Network network = new Network();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Log in");
        stage.setScene(scene);
        LoginController loginController = fxmlLoader.getController();
        loginController.setNetwork(network);
        fasfsaf
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}