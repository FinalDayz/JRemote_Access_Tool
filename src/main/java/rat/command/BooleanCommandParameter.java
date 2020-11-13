package main.java.rat.command;

public class BooleanCommandParameter extends AbstractCommandParameter<Boolean> {

    public BooleanCommandParameter(String name, boolean required, String description) {
        super(name, required, description);
    }

    public BooleanCommandParameter(String name, boolean required, String description, boolean defaultValue) {
        super(name, required, description, defaultValue);
    }

    @Override
    void setValueFromString(String val) {
        this.value = Boolean.parseBoolean(val);
    }
}
