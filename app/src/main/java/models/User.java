package models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import config.FirebaseConfig;
import helper.FirebaseUserHelper;

public class User implements Serializable {

    private String uid;
    private String name;
    private String email;
    private String password;
    private String photo;

    public User() {

    }

    @Exclude
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void save() {
        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase();
        DatabaseReference userRef = firebaseRef.child("users").child(getUid());
        userRef.setValue(this);
    }

    public void update() {
        String userId = FirebaseUserHelper.getUserId();
        DatabaseReference databaseRef = FirebaseConfig.getFirebaseDatabase();

        DatabaseReference userRef = databaseRef.child("users")
                .child(userId);

        userRef.updateChildren(convertToMap());
    }

    @Exclude
    public Map<String, Object> convertToMap() {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("email", getEmail());
        userMap.put("name", getName());
        userMap.put("photo", getPhoto());

        return userMap;
    }
}
