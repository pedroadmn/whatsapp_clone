package activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;

import config.FirebaseConfig;
import models.User;
import pedroadmn.whatsappclone.com.R;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText tieSignupName;
    private TextInputEditText tieSignupEmail;
    private TextInputEditText tieSignupPassword;
    private Button btnRegister;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseConfig.getFirebaseAuth();
        firebaseDatabase = FirebaseConfig.getFirebaseDatabase();

        tieSignupName = findViewById(R.id.tieSignupName);
        tieSignupEmail = findViewById(R.id.tieSignupEmail);
        tieSignupPassword = findViewById(R.id.tieSignupPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = tieSignupName.getText().toString();
        String email = tieSignupEmail.getText().toString();
        String password = tieSignupPassword.getText().toString();

        if (!name.isEmpty()) {
            if(!email.isEmpty()) {
                if (!password.isEmpty()) {
                    User user = new User();
                    user.setName(name);
                    user.setEmail(email);
                    user.setPassword(password);
                    saveUserOnFirebase(user);
                } else {
                    Toast.makeText(this, "Fill your password", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Fill your email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Fill your name", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserOnFirebase(User user) {
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()) {
                        Toast.makeText(this, "Successfully registered", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String exceptionMessage = "";
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException exception) {
                            exceptionMessage = "Type a stronger password";
                        } catch (FirebaseAuthInvalidCredentialsException exception) {
                            exceptionMessage = "Please, type a valid email";
                        } catch (FirebaseAuthUserCollisionException exception) {
                            exceptionMessage = "This account is already registered";
                        } catch (Exception exception) {
                            exceptionMessage = "Error on register user";
                            exception.printStackTrace();
                        }

                        Toast.makeText(this, exceptionMessage, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {

                });
    }
}