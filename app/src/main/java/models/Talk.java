package models;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import config.FirebaseConfig;

public class Talk implements Serializable {
    private String senderId;
    private String recipientId;
    private String lastMessage;
    private User user;

    public Talk() {
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void save() {
        DatabaseReference database = FirebaseConfig.getFirebaseDatabase();
        DatabaseReference talkRef = database.child("talks");

        talkRef.child(this.getSenderId())
                .child(this.getRecipientId())
                .setValue(this);
    }
}
