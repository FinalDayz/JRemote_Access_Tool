package main.java.rat.client;

import main.java.rat.handlers.InputOutputHandler;
import main.java.rat.listeners.InputOutputListener;

import java.io.*;
import java.net.*;

public abstract class AbstractSocketClient implements InputOutputListener {

    private String serverIp;
    private int port;

    private InetAddress inetAddress;
    protected InputOutputHandler inputOutputHandler;

    public AbstractSocketClient(String ip, int port) {
        this.serverIp = ip;
        this.port = port;
    }

    public void connectToServer() throws IOException {
        if(inputOutputHandler != null && inputOutputHandler.isConnected()) {
            throw new IllegalStateException("Already connected to server");
        }
        System.out.println("Connecting to server on port " + port+" and ip " + serverIp+" ...");
        Socket socket = new Socket();
        InetAddress inetAddress = InetAddress.getByName(serverIp);
        SocketAddress socketAddress = new InetSocketAddress(inetAddress, port);

        socket.connect(socketAddress);

        socket.getTcpNoDelay();
        System.out.println("Connected to server");
        inputOutputHandler = new InputOutputHandler(
                this,
                socket,
                new ObjectOutputStream(socket.getOutputStream()),
                new ObjectInputStream(socket.getInputStream())
        );

        connectedClient(inputOutputHandler);
    }

    abstract void connectedClient(InputOutputHandler handler);

    public String getServerIp() {
        return serverIp;
    }

    public int getPort() {
        return port;
    }
}
