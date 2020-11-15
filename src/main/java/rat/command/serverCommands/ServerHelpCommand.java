package main.java.rat.command.serverCommands;

import main.java.rat.command.Command;
import main.java.rat.command.ServerCommand;
import main.java.rat.command.StringCommandParameter;

import java.util.ArrayList;

public class ServerHelpCommand extends ServerCommand {

    public ServerHelpCommand() {
        this.parameters.add(
                new StringCommandParameter("commandName", false, "Name of the command to get help from, empty for all commands")
        );
    }

    @Override
    public String getMainCommandName() {
        return "help";
    }

    @Override
    public String getHelpText() {
        return "Get help from all commands or extended help from one command";
    }

    @Override
    public String getExtendedHelpText() {
        return getHelpText();
    }

    @Override
    public boolean executeCommand() {
        if(isParameterFilledIn("commandName")) {
            String specificCommand = (String) this.getParameter("commandName").getValue();
            for(Command command : environment.getCommands()) {
                if(command.getMainCommandName().equalsIgnoreCase(specificCommand)) {
                    logger.log("Info for command '"+specificCommand+"':");
                    logger.log(command.getExtendedHelpText());
                    logger.log("\t"+String.join("\n\t",command.getParametersInfo()));
                    return true;
                }
            }
            this.error = "Command '"+specificCommand+"' doesn't exist";
        } else {
            logger.log("All commands:");
            for(Command command : environment.getCommands()) {
                logger.log(command.getMainCommandName()+" - " + command.getHelpText());
                logger.log("\tParameters: "+
                        String.join(", ", command.getParametersInfo(false))
                );
            }
            return true;
        }

        return false;
    }
}
