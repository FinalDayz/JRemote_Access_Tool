package RAT.server;

import java.io.IOException;

public interface SocketServerInterface {
    void startServer(int port) throws IOException;
    void stopServer() throws IOException;
}
