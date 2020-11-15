package main.java.rat.command;

public abstract class AbstractCommandParameter<T> {

    String name;
    boolean required;
    T defaultValue = null;
    T value = null;
    String description;

    public AbstractCommandParameter(String name, boolean required, String description) {
        this.name = name;
        this.required = required;
        this.description = description;
    }

    public AbstractCommandParameter(String name, boolean required, String description, T defaultValue) {
        this(name, required, description);
        this.defaultValue = defaultValue;
    }

    abstract void setValueFromString(String val);

    public void setValue(T value) {
        this.value = value;
    }

    public boolean isRequired() {
        return required;
    }

    public T getValue() {
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    boolean emptyValue() {
        return this.value == null;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public boolean hasDefault() {
        return defaultValue != null;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public void clear() {
        this.value = null;
    }
}
