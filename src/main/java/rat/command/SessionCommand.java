package main.java.rat.command;

public interface SessionCommand {

    String getExitString();
    void receivedInput(String command);
    boolean isInSession();

    void exit();
}
