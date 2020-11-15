package main.java.rat.handlers;

import main.java.rat.listeners.InputOutputListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class InputOutputHandler {

    private InputOutputListener listener;
    private final Socket socket;
    protected ObjectOutputStream outputStream;
    protected ObjectInputStream inputStream;
    private boolean connected;

    public InputOutputHandler(
            Socket socket,
            ObjectOutputStream outputStream,
            ObjectInputStream inputStream
    ) {
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.socket = socket;
        connected = true;

        listenToMessages();
    }

    public InputOutputHandler(
            InputOutputListener listener,
            Socket socket,
            ObjectOutputStream outputStream,
            ObjectInputStream inputStream
    ) {
        this(socket, outputStream, inputStream);
        this.listener = listener;
    }

    public InputOutputListener getListener() {
        return listener;
    }

    public void setListener(InputOutputListener listener) {
        this.listener = listener;
    }

    public void sendMessage(Object message) {
        System.out.println("SEND MESSAGE " + message);
        try {
            outputStream.writeObject(message);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            this.connected = false;
            this.listener.disconnected();
        }
    }

    public void disconnect() throws IOException {
        this.socket.close();
        connected = false;
    }

    private void listenToMessages() {
        new Thread(() -> {
            while (connected) {
                try {
                    Object messageObject = inputStream.readObject();
                    if(listener != null)
                        listener.receivedMessage(messageObject);
                } catch (IOException e) {
                    if (connected) {
                        this.connected = false;
                        if(listener != null)
                            listener.disconnected();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public Object readNextMessage() throws IOException, ClassNotFoundException {
        return inputStream.readObject();
    }

    public boolean isConnected() {
        return this.connected;
    }
}
