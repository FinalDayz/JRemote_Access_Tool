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
                    System.out.println("Info for command '"+specificCommand+"':");
                    System.out.println(command.getExtendedHelpText());
                    System.out.println("\t"+String.join("\n\t",command.getParametersInfo()));
                    return true;
                }
            }
        } else {
            System.out.println("All commands:");
            for(Command command : environment.getCommands()) {
                System.out.println(command.getMainCommandName()+" - " + command.getHelpText());
                System.out.println("\tParameters: "+
                        String.join(", ", command.getParametersInfo(false))
                );
            }
            return true;
        }

        return false;
    }
}
