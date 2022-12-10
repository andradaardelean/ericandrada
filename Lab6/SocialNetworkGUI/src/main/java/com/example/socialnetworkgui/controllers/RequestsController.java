package com.example.socialnetworkgui.controllers;

import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.exceptions.RepositoryException;
import com.example.socialnetworkgui.service.Network;
import com.example.socialnetworkgui.utils.events.FriendshipEntityChangeEvent;
import com.example.socialnetworkgui.utils.observer.Observable;
import com.example.socialnetworkgui.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RequestsController implements Observer<FriendshipEntityChangeEvent> {
    Network network;
    @FXML
    public TableView<User> tableViewRequests;
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
        System.out.println("dadasdsd");
        tableViewRequests.setItems(model);
    }

    public void initModel(){
        Iterable<User> allUsers = network.getAllRequests();
        List<User> users = StreamSupport.stream(allUsers.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(users);
    }
    @Override
    public void update(FriendshipEntityChangeEvent friendshipEntityChangeEvent) {initModel();}

    public void onAcceptRequest(){
        User selected = tableViewRequests.getSelectionModel().getSelectedItem();
        network.acceptRequest(selected);
    }

    public void onDeclineRequest() throws RepositoryException {
        User selected = tableViewRequests.getSelectionModel().getSelectedItem();
        network.declineRequest(selected);
    }

}
