package main.java.rat.models;

public interface Subscriber<T> {

    void update(T object);
}
