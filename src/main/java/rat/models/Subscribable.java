package main.java.rat.models;

public interface Subscribable<T> {

    void subscribe(Subscriber<T> subscriber);

    void unSubscribe(Subscriber<T> subscriber);
}
