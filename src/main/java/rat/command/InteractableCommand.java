package main.java.rat.command;

public interface InteractableCommand {

    String getExitString();
    void execute(String command);
    boolean isInSession();

    void exit();
}
