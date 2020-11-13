package main.java.rat.server;

import main.java.rat.Environment;
import main.java.rat.command.Command;

import java.util.ArrayList;

public class CommandHandler {

    private ArrayList<Command> commands = new ArrayList<Command>();

    public CommandHandler(Environment environment, Class<? extends Command>... commands) {
        for (Class<? extends Command> commandClass : commands) {
            try {
                Command newCommandObj = commandClass.getDeclaredConstructor().newInstance();
                newCommandObj.setEnv(environment);
                this.commands.add(newCommandObj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getMainCommandFromFull(String completeCommand) {
        int firstSpaceIndex = completeCommand.indexOf(" ");
        firstSpaceIndex = firstSpaceIndex == -1 ? completeCommand.length() : firstSpaceIndex;
        return completeCommand.substring(0, firstSpaceIndex).toLowerCase();
    }

    private String getArgumentStr(String completeCommand, String mainCommand) {
        String args = completeCommand.replace(mainCommand, "");
        if (args.isBlank())
            args = "";
        return args;
    }

    public boolean commandExists(String completeCommand) {
        String mainCommand = getMainCommandFromFull(completeCommand);

        for (Command commandObj : commands) {
            if (commandObj.getMainCommandName().equalsIgnoreCase(mainCommand)) {
                return true;
            }
        }

        return false;
    }

    public void handleCommand(String completeCommand) {
        String mainCommand = getMainCommandFromFull(completeCommand);
        String argumentStr = getArgumentStr(completeCommand, mainCommand);

        ArrayList<String> arguments = parseArguments(argumentStr);

        for (Command commandObj : commands) {
            if (commandObj.getMainCommandName().equalsIgnoreCase(mainCommand)) {
                try {

                    if (!commandObj.parseArguments(arguments) || !commandObj.executeCommand())
                        throw new Exception("Invalid command handling");
                } catch (Exception e) {

                    if (commandObj.getError() != null) {
                        System.out.println("Error while processing command:");
                        System.out.println(commandObj.getError());
                    } else {
                        System.out.println("Invalid usage:");
                        System.out.println(commandObj.getExtendedHelpText());
                    }
                }
                return;
            }
        }

        System.out.println("Command '" + completeCommand + "' not found, please use help to get help");
    }

    private ArrayList<String> parseArguments(String argumentStr) {
        ArrayList<String> arguments = new ArrayList<>();

        boolean isInQuote = false;
        char inQuoteChar = 0;
        StringBuilder currentArgument = new StringBuilder();
        for (char argChar : argumentStr.toCharArray()) {
            //end or 1 argument
            if (argChar == ' ' && !isInQuote) {
                if (!currentArgument.toString().isBlank())
                    arguments.add(currentArgument.toString());
                currentArgument = new StringBuilder();
                continue;
            }
            //start of argument quote
            if (!isInQuote && (argChar == '\'' || argChar == '"')) {
                isInQuote = true;
                inQuoteChar = argChar;
                continue;
            }
            //end of argument quote
            if (isInQuote && argChar == inQuoteChar) {
                isInQuote = false;
                continue;
            }
            currentArgument.append(argChar);
        }

        String lastArg = currentArgument.toString();
        if (!lastArg.isBlank()) {
            arguments.add(lastArg);
        }

        return arguments;
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }
}
