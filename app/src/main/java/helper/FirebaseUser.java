package helper;

import com.google.firebase.auth.FirebaseAuth;

import config.FirebaseConfig;

public class FirebaseUser {

    public static String getUserId() {
        FirebaseAuth user = FirebaseConfig.getFirebaseAuth();
        String email = user.getCurrentUser().getEmail();
        return Base64Custom.encodeBase64(email);
    }
}
