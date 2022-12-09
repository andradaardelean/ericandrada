package com.example.socialnetworkgui.controllers;

import com.example.socialnetworkgui.Application;
import com.example.socialnetworkgui.service.Network;
import com.example.socialnetworkgui.utils.events.FriendshipEntityChangeEvent;
import com.example.socialnetworkgui.utils.observer.Observable;
import com.example.socialnetworkgui.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import com.example.socialnetworkgui.domain.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserAccountController implements Observer<FriendshipEntityChangeEvent>{
    Network network = new Network();

    ObservableList<User> model = FXCollections.observableArrayList();
    @FXML
    public TableView<User> tableViewFriends;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private Label labelErrors;

    public void setNetwork(Network network){
        this.network = network;
        initModel();
        network.addObserver(this);
    }

    public void initialize(){
        usernameColumn.setCellValueFactory(new PropertyValueFactory<User,String>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        //System.out.println("final users" + model);
        tableViewFriends.setItems(model);
    }

    private void initModel(){
        Iterable<User> allUsers = network.getAllFriends();
        //System.out.println("before streamsupporrt" + allUsers);
        List<User> users = StreamSupport.stream(allUsers.spliterator(), false)
                .collect(Collectors.toList());
        //System.out.println("after streamsupporrt" + users);
        model.setAll(users);
    }

    @Override
    public void update(FriendshipEntityChangeEvent friendshipEntityChangeEvent){ initModel();}

    @FXML
    public void onRemoveFriend(){
        try{
            User user1 = network.getCurrentUser();
            User selectedUser = tableViewFriends.getSelectionModel().getSelectedItem();
            network.removeFriendship(user1.getUsername(), selectedUser.getUsername());
            System.out.println(network.getAllFriendships());
        }catch (Exception e){
            labelErrors.setText(e.getMessage());
        }
    }

    public void onAddFriend(){
        try {
            FXMLLoader addLoader = new FXMLLoader(Application.class.getResource("AddFriend.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(addLoader.load()));
            stage.setTitle("Add Friend Page");
            AddFriendController addFriendController = addLoader.getController();
            addFriendController.setNetwork(network);
            stage.show();
        } catch(Exception e){
            labelErrors.setText(e.getMessage());
        }
    }
}
