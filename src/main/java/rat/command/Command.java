package main.java.rat.command;

import main.java.rat.Environment;

public interface Command {

    void setEnv(Environment e);

    String getMainCommandName();
    String getHelpText();
    String getExtendedHelpText();
    String getError();
    String[] getParametersInfo();
    String[] getParametersInfo(boolean withDescription);

    boolean parseArguments(Iterable<String> args);
    boolean executeCommand();
}
