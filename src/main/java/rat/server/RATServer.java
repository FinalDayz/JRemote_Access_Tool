package main.java.rat.server;

import main.java.rat.handlers.InputOutputHandler;
import main.java.rat.server.connectedClients.RATConnectedClient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class RATServer extends AbstractSocketServer {

    ArrayList<InputOutputHandler> clientList = new ArrayList<>();

    public RATServer(int port) {
        super(port);
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
        RATConnectedClient client = new RATConnectedClient(handler, this);
        clientList.add(handler);
    }

    public void clientDisconnected(RATConnectedClient ratConnectedClient) {
        System.out.println("[server] client disconnected");
    }

    public void incorrectSecret(RATConnectedClient ratConnectedClient) {
        System.out.println("Client tried to connect with incorrect secret, disconnecting the client...");
        ratConnectedClient.disconnected();
    }

    public void unknownInitError(RATConnectedClient ratConnectedClient) {
        System.out.println("Unknown error occurred while performing init, disconnecting the client...");
        ratConnectedClient.disconnected();
    }

    public void successInit(RATConnectedClient ratConnectedClient) {
        System.out.println("Client connected, computer info:");
        System.out.println(ratConnectedClient.getFormattedComputerInfo());
    }
}
