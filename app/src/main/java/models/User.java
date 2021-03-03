package models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import config.FirebaseConfig;

public class User {

    private String uid;
    private String name;
    private String email;
    private String password;

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

    public void save() {
        DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase();
        DatabaseReference userRef = firebaseRef.child("users").child(getUid());
        userRef.setValue(this);
    }
}
