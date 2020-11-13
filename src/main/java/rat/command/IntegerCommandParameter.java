package main.java.rat.command;

public class IntegerCommandParameter extends AbstractCommandParameter<Integer> {

    public IntegerCommandParameter(String name, boolean required, String description) {
        super(name, required, description);
    }

    public IntegerCommandParameter(String name, boolean required, String description, Integer defaultValue) {
        super(name, required, description, defaultValue);
    }

    @Override
    void setValueFromString(String val) throws NumberFormatException {
        this.value = Integer.parseInt(val);
    }
}
