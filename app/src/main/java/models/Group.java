package models;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

import config.FirebaseConfig;
import helper.Base64Custom;

public class Group implements Serializable {

    private String id;
    private String name;
    private String photo;
    private List<User> members;

    public Group() {
        DatabaseReference database = FirebaseConfig.getFirebaseDatabase();
        DatabaseReference groupRef = database.child("groups");

        String idGroupFirebase = groupRef.push().getKey();
        setId(idGroupFirebase);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public void save() {
        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase();
        DatabaseReference groupRef = firebaseRef.child("groups").child(getId());
        groupRef.setValue(this);

        for (User member : getMembers()) {

            String senderId = Base64Custom.encodeBase64(member.getEmail());
            String recipientId = getId();

            Talk talk = new Talk();
            talk.setSenderId(senderId);
            talk.setRecipientId(recipientId);
            talk.setLastMessage("");
            talk.setIsGroup("true");
            talk.setGroup(this);

            talk.save();
        }
    }
}
