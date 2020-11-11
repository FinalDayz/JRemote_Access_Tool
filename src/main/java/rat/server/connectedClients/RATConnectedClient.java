package main.java.rat.server.connectedClients;

import main.java.rat.handlers.InputOutputHandler;
import main.java.rat.listeners.InputOutputListener;
import main.java.rat.models.ComputerInfo;
import main.java.rat.server.RATServer;

import java.io.IOException;

public class RATConnectedClient implements InputOutputListener {

    private final RATServer server;
    private final InputOutputHandler inputOutput;
    private boolean initStage = false;

    public ComputerInfo clientComputerInfo;

    public RATConnectedClient(InputOutputHandler inputOutputHandler, RATServer ratServer) {
        initStage = true;
        this.server = ratServer;
        this.inputOutput = inputOutputHandler;
        inputOutput.setListener(this);


        inputOutput.sendMessage("Hello world from server");
    }

    @Override
    public void disconnected() {
        server.clientDisconnected(this);
    }

    @Override
    public void receivedMessage(Object message) {
        if(initStage) {


        }

    }
}
