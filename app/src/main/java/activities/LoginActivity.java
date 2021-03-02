package activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import config.FirebaseConfig;
import models.User;
import pedroadmn.whatsappclone.com.R;

public class LoginActivity extends AppCompatActivity {

    private TextView tvNoAccount;
    private Button btnLogin;
    private TextInputEditText tieEmail;
    private TextInputEditText tiePassword;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseConfig.getFirebaseAuth();

        tvNoAccount = findViewById(R.id.tvNoAccount);
        btnLogin = findViewById(R.id.btnLogin);
        tieEmail = findViewById(R.id.tieEmail);
        tiePassword = findViewById(R.id.tiePassword);

        tvNoAccount.setOnClickListener(v -> goToSignupScreen());

        btnLogin.setOnClickListener(v -> validateLogin());
    }

    private void goToSignupScreen() {
        Intent i = new Intent(this, SignupActivity.class);
        startActivity(i);
    }

    private void goToMainScreen() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private void validateLogin() {
        String email = tieEmail.getText().toString();
        String password = tiePassword.getText().toString();

        if(!email.isEmpty()) {
            if (!password.isEmpty()) {
                User user = new User();
                user.setEmail(email);
                user.setPassword(password);
                loginUser(user);
            } else {
                Toast.makeText(this, "Fill your password", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Fill your email", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginUser(User user) {
        firebaseAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Toast.makeText(this, "Successfully login", Toast.LENGTH_SHORT).show();
                        goToMainScreen();
                    } else {
                        String exceptionMessage = "";
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException exception) {
                            exceptionMessage = "User is not registered";
                        } catch (FirebaseAuthInvalidCredentialsException exception) {
                            exceptionMessage = "Email or password is invalid";
                        } catch (Exception exception) {
                            exceptionMessage = "Error on login user";
                            exception.printStackTrace();
                        }

                        Toast.makeText(this, exceptionMessage, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {

                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null) {
            goToMainScreen();
        }
    }
}