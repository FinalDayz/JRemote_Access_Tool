package main.java.rat.command;

import main.java.rat.Environment;
import main.java.rat.logger.StringLogger;

public interface Command {

    void setEnv(Environment e);
    void setLogger(StringLogger logger);

    String getMainCommandName();
    String getHelpText();
    String getExtendedHelpText();
    String getError();
    String[] getParametersInfo();
    String[] getParametersInfo(boolean withDescription);

    boolean parseArguments(Iterable<String> args);
    boolean executeCommand();
}
