package helper;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import config.FirebaseConfig;
import models.User;

public class FirebaseUserHelper {

    public static String getUserId() {
        FirebaseAuth user = FirebaseConfig.getFirebaseAuth();
        String email = user.getCurrentUser().getEmail();
        return Base64Custom.encodeBase64(email);
    }

    public static FirebaseUser getCurrentUser() {
        FirebaseAuth user = FirebaseConfig.getFirebaseAuth();
        return user.getCurrentUser();
    }

    public static boolean updateUserPhoto(Uri url) {
        try {
            FirebaseUser user = getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url)
                    .build();

            user.updateProfile(profile).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.d("Profile", "Error on update profile image");
                }
            });
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public static boolean updateUsername(String username) {
        try {
            FirebaseUser user = getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build();

            user.updateProfile(profile).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.d("Profile", "Error on update username");
                }
            });
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public static User getLoggeduserInfo() {
        FirebaseUser firebaseUser = getCurrentUser();

        User user = new User();
        user.setEmail(firebaseUser.getEmail());
        user.setName(firebaseUser.getDisplayName());

        if (firebaseUser.getPhotoUrl() == null) {
            user.setPhoto("");
        } else {
            user.setPhoto(firebaseUser.getPhotoUrl().toString());
        }

        return user;
    }
}
