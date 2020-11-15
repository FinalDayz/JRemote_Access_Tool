package main.java.rat.command.serverCommands;

import main.java.rat.command.ServerCommand;
import main.java.rat.server.connectedClients.RATConnectedClient;

public class ServerClientsCommand extends ServerCommand {

    @Override
    public String getMainCommandName() {
        return "clients";
    }

    @Override
    public String getHelpText() {
        return "Get a list of the connected clients";
    }

    @Override
    public String getExtendedHelpText() {
        return getHelpText();
    }

    @Override
    public boolean executeCommand() {
        logger.log("There are/is "+environment.getClientList().size()+" clients connected");
        for(RATConnectedClient client : environment.getClientList()) {
            logger.log("Client " + client.getFullName());
        }

        return true;
    }
}
