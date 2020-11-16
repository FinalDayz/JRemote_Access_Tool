package main.java.rat.command;

import main.java.rat.logger.StringLogger;

import java.util.ArrayList;

public abstract class AbstractCommand implements Command {

    protected StringLogger logger;
    protected String error;
    public ArrayList<AbstractCommandParameter> parameters = new ArrayList<>();

    public void setLogger(StringLogger logger) {
        this.logger = logger;
    }

    public StringLogger getLogger() {
        return this.logger;
    }

    public boolean parseArguments(Iterable<String> args) {
        for (AbstractCommandParameter parameter : this.parameters) {
            parameter.clear();
        }

        int index = 0;
        for (String argument : args) {
            if (index < parameters.size()) {
                parameters.get(index).setValueFromString(argument);
            }
            index++;
        }
        for (AbstractCommandParameter parameter : this.parameters) {
            if (parameter.isRequired() && parameter.emptyValue()) {
                this.error = "parameter '"+parameter.getName() + "' is empty, but it is required";
                return false;
            }
        }

        return true;
    }

    public boolean isParameterFilledIn(String name) {
        return !getParameter(name).emptyValue();
    }

    public AbstractCommandParameter getParameter(String name) {
        for (AbstractCommandParameter parameter : this.parameters) {
            if (parameter.getName().equalsIgnoreCase(name)) {
                return parameter;
            }
        }
        throw new IllegalArgumentException("Name not found");
    }

    public String[] getParametersInfo() {
        return this.getParametersInfo(true);
    }

    public String[] getParametersInfo(boolean withDescription) {

        if (parameters.size() == 0) {
            return new String[]{"Command requires no parameters"};
        } else {
            ArrayList<String> parameterInfo = new ArrayList<>();
            for (AbstractCommandParameter parameter : this.parameters) {
                parameterInfo.add(
                        "[" + parameter.getName() + "] " +
                        (parameter.isRequired() ? "required" : "not required") +
                        (parameter.hasDefault() ? " default: " + parameter.getDefaultValue().toString() : "") +
                        (withDescription ? " - " + parameter.getDescription() : "")
                );
            }

            return parameterInfo.toArray(new String[]{});
        }
    }

    public String getError() {
        return this.error;
    }


}
