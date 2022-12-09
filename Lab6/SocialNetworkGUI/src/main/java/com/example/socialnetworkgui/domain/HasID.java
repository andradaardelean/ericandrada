package com.example.socialnetworkgui.domain;

import java.io.Serializable;

public interface HasID<ID> {
    ID getID();
    void setID(ID id);
}
