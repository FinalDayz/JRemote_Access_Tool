package main.java.rat.command;

import main.java.rat.Environment;
import main.java.rat.client.RATClient;
import main.java.rat.command.clientCommands.ClientOpenURLCommand;

public abstract class ClientCommand extends AbstractCommand {
    protected RATClient environment = null;

    @Override
    public void setEnv(Environment e) {
        environment = (RATClient) e;
    }

    public static Class<? extends Command>[] getAllClientCommands() {
        return new Class[]{
                ClientOpenURLCommand.class
        };
    }
}