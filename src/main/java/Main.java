package main.java;

import main.java.rat.client.RATClient;
import main.java.rat.server.RATServer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static String VERSION = "0.1.1";
    public static String SECRET = "2c9af0a9b4ac2c9d6ba971ae8f596fe0ea46fed7";

    public static void main(String[] args) throws IOException {

        RATServer server = new RATServer(5560);
        try {
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RATClient client = new RATClient("localhost", 5560);
        try {
            client.connectToServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true) {

        }

    }
}
