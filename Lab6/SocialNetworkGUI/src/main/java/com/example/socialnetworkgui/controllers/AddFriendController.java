package com.example.socialnetworkgui.controllers;

import com.example.socialnetworkgui.service.Network;
import com.example.socialnetworkgui.utils.events.FriendshipEntityChangeEvent;
import com.example.socialnetworkgui.utils.observer.Observable;
import com.example.socialnetworkgui.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import com.example.socialnetworkgui.domain.User;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AddFriendController implements Observer<FriendshipEntityChangeEvent> {
    Network network;
    @FXML
    public TableView<User> tableViewAddFriend;
    @FXML
    public TableColumn<User, String> usernameColumn;
    @FXML
    public TableColumn<User, String> emailColumn;

    ObservableList<User> model = FXCollections.observableArrayList();

    public void setNetwork(Network network){
        this.network = network;
        initModel();
        network.addObserver(this);
    }

    public void initialize(){
        usernameColumn.setCellValueFactory(new PropertyValueFactory<User,String>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        tableViewAddFriend.setItems(model);
    }

    public void initModel(){
        Iterable<User> allUsers = network.getAllNonFriends();
    }
    @Override
    public void update(FriendshipEntityChangeEvent friendshipEntityChangeEvent) {
        initModel();
    }
}
