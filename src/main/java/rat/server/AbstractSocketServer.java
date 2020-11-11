package main.java.rat.server;

import main.java.rat.server.connectedClients.RATConnectedClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

abstract class AbstractSocketServer {

    private final int port;
    private ServerSocket socket;
    private ClientHandler clientHandler;

    private boolean started = false;

    public AbstractSocketServer(int port) {
        this.port = port;
    }

    public void startServer() throws IOException {
        socket = new ServerSocket(port);
        started = true;
        waitForConnections();
    }

    private void waitForConnections() {
        System.out.println("Started server at port "+port
                +" and waiting for conenctions...");

        new Thread(() -> {
            while (started) {
                try {
                    Socket acceptedSocket = socket.accept();

                    ObjectOutputStream outputStream = new ObjectOutputStream(acceptedSocket.getOutputStream());
                    ObjectInputStream inputStream = new ObjectInputStream(acceptedSocket.getInputStream());

                    newConnection(acceptedSocket, outputStream, inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    abstract void newConnection(Socket acceptedSocket, ObjectOutputStream outputStream, ObjectInputStream inputStream);

    void stopServer() throws IOException {
        this.socket.close();
        started = false;
    }

}
