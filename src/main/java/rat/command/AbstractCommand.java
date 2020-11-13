package main.java.rat.command;

import java.util.ArrayList;

public abstract class AbstractCommand implements Command {

    protected String error;
    public ArrayList<AbstractCommandParameter> parameters = new ArrayList<>();

    public boolean parseArguments(Iterable<String> args) {
        int index = 0;
        for (String argument : args) {
            if (index < parameters.size()) {
                parameters.get(index).setValueFromString(argument);
            }
            index++;
        }
        for (AbstractCommandParameter parameter : this.parameters) {
            if (parameter.isRequired() && parameter.emptyValue()) {
                this.error = parameter.getName() + " is empty, but is required";
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
            return new String[]{"Command required no parameters"};
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
