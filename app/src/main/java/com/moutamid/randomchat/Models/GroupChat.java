package com.moutamid.randomchat.Models;

/**
 * TODO: Add a class header comment!
 */
public class GroupChat {

    private String message;
    private String senderUid;
    private long timestamp;

    public GroupChat(){

    }

    public GroupChat(String message, String senderUid, long timestamp) {
        this.message = message;
        this.senderUid = senderUid;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
