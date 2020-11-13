package main.java.rat.command;

import main.java.rat.Environment;
import main.java.rat.command.serverCommands.ServerConnectCommand;
import main.java.rat.command.serverCommands.ServerGetInfoCommand;
import main.java.rat.command.serverCommands.ServerHelpCommand;
import main.java.rat.server.RATServer;

public abstract class ServerCommand extends AbstractCommand {

    protected RATServer environment = null;

    @Override
    public void setEnv(Environment e) {
        environment = (RATServer) e;
    }

    public static Class<? extends Command>[] getAllServerCommands() {
        return new Class[]{
                ServerGetInfoCommand.class,
                ServerConnectCommand.class,
                ServerHelpCommand.class
        };
    }

}
