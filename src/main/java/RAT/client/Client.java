package RAT.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Client implements SocketClientInterface {

    private Socket socket;
    private int port;
    private String ip;
    private InetAddress inetAddress;
    boolean connected = false;

    public void connect(String ip, int port) throws IOException {
        this.port = port;
        this.ip = ip;
        socket = new Socket();
        InetAddress inetAddress=InetAddress.getByName("localhost");

        SocketAddress socketAddress=new InetSocketAddress(inetAddress, port);

        socket.bind(socketAddress);
        socket.connect(socketAddress);
        System.out.println("Inet address: "+socket.getInetAddress());
        System.out.println("Port number: "+socket.getLocalPort());
        connected = true;
    }

    public void receiveMessage(Object message) {

    }
}
