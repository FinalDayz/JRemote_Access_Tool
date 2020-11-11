import RAT.client.Client;
import RAT.server.Server;

import java.io.IOException;

public class Main {
    public static void Main(String[] args) {
        Server server = new Server();
        try {
            server.startServer(1991);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Client client = new Client();
    }
}
