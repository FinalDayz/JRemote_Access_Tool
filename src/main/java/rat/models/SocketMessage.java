package main.java.rat.models;

import java.io.Serializable;

public abstract class SocketMessage<T> implements Serializable {

    private T content;

    public void setContent(T content) {
        this.content = content;
    }
    public T getContent() {
        return this.content;
    }

}
