package RAT.client;

import java.io.IOException;

public interface SocketClientInterface {
    void connect(String ip, int port) throws IOException;
    void receiveMessage(Object message);
}
