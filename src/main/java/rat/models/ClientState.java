package main.java.rat.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ClientState implements Subscribable<ClientState>, Serializable {
    private transient ArrayList<Subscriber<ClientState>> subscribers = new ArrayList<>();
    private boolean isInSession = false;

    public boolean isInSession() {
        return isInSession;
    }

    public void setInSession(boolean inSession) {
        if(this.isInSession != inSession) {
            this.isInSession = inSession;
            notifySubscribers();
        }
    }

    public void subscribe(Subscriber<ClientState> subscriber) {
        this.subscribers.add(subscriber);
    }

    public void unSubscribe(Subscriber<ClientState> subscriber) {
        this.subscribers.remove(subscriber);
    }

    private void notifySubscribers() {
        for(Subscriber<ClientState> subscriber : subscribers) {
            subscriber.update(this);
        }
    }
}

