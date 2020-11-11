package main.java.rat.listeners;

public interface InputOutputListener {

    void disconnected();
    void receivedMessage(Object message);
}
