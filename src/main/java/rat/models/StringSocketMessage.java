package main.java.rat.models;

public class StringSocketMessage implements SocketMessage<String> {

    private boolean isCommand = false;
    private String content;

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public boolean isCommand() {
        return isCommand;
    }

    @Override
    public void setIsCommand() {
        this.isCommand = true;
    }

//    @Override
//    public boolean isFromServer() {
//        return true;
//    }
//
//    @Override
//    public boolean isFromClient() {
//        return false;
//    }
}
