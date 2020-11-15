package main.java.rat.models;

import java.io.Serializable;

public interface SocketMessage<T> extends Serializable {

    void setContent(T content);
    T getContent();

    boolean isCommand();
    void setIsCommand();

    boolean isInfo();
    void setIsInfo();

//    boolean isFromServer();
//    boolean setFromServer();
//
//    boolean isFromClient();
//    boolean setFromClient();

}
