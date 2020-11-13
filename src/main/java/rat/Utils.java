package main.java.rat;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Locale;

public class Utils {

    public static String executeCMDCommand(String command, boolean waitForCommand) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", command);
        builder.redirectErrorStream(true);
        Process p = builder.start();
        if(!waitForCommand)
            return null;
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder totalReturn = new StringBuilder();
        String line;
        while (true) {
            line = r.readLine();
            if (line == null) { break; }
            totalReturn.append(line).append("\r\n");
        }
        return totalReturn.toString();
    }

    public static boolean isHeadless() {
        return GraphicsEnvironment.isHeadless();
    }

    public static String readableByteSize(long bytes) {
        if (bytes <= 0) return "";
        if (bytes < 1000) return bytes + " B";
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes >= 999_999) {
            bytes /= 1024;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1024.0, ci.current());
    }
}
