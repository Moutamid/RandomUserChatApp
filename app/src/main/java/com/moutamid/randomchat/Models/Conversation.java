package com.moutamid.randomchat.Models;

public class Conversation {

    private String userUid;
    private String chatWithId;
   // private String chatId;
    private long timestamp;

    public Conversation() {
    }

    public Conversation(String userUid, String chatWithId, long timestamp) {
        this.userUid = userUid;
        this.chatWithId = chatWithId;
    //    this.chatId = chatId;
        this.timestamp = timestamp;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getChatWithId() {
        return chatWithId;
    }

    public void setChatWithId(String chatWithId) {
        this.chatWithId = chatWithId;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
