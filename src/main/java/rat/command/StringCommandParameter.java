package main.java.rat.command;

public class StringCommandParameter extends AbstractCommandParameter<String> {

    public StringCommandParameter(String name, boolean required, String description) {
        super(name, required, description);
    }

    public StringCommandParameter(String name, boolean required, String description, String defaultValue) {
        super(name, required, description, defaultValue);
    }

    @Override
    void setValueFromString(String val) {
        this.value = val;
    }

}
