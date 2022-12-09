package com.example.socialnetworkgui.utils.events;

import com.example.socialnetworkgui.domain.Friendship;

public class FriendshipEntityChangeEvent implements Event{
    private ChangeEventType type;
    private Friendship oldData, data;

    public FriendshipEntityChangeEvent(ChangeEventType type, Friendship data){
        this.type = type;
        this.data = data;
    }

    public FriendshipEntityChangeEvent(ChangeEventType type, Friendship oldData, Friendship data){
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType(){return this.type;}

    public Friendship getOldData(){return  this.oldData;}

    public Friendship getData() {return this.data;}
}
