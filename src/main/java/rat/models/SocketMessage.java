package main.java.rat.models;

public interface SocketMessage<T> {

    void setContent(T content);
    T getContent();

    boolean isCommand();
    void setIsCommand();

//    boolean isFromServer();
//    boolean setFromServer();
//
//    boolean isFromClient();
//    boolean setFromClient();

}
