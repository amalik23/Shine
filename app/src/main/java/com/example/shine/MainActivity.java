package com.example.shine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    static final String uidKey = "uidKey";        // key for shared preferences
    static final String nameKey = "nameKey";
    private EditText emailField, passwordField;
    private FirebaseAuth mAuth;

    //animate
    ImageView i;

    /**
     * Method checks if an email already exists within SharedPreferences and goes to the home screen
     * for that user if it does, otherwise stays on a login screen
     *
     * @param savedInstanceState the saved instance state of the app
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.shine", Context.MODE_PRIVATE);

        // if sharedPreferences doesn't contain keys initialize it
        if (!sharedPreferences.contains(uidKey)) {
            sharedPreferences.edit().putString(uidKey, "").apply();
            sharedPreferences.edit().putString(nameKey, "").apply();
        }

        // if a username exists, go to home screen, otherwise just have login screen
        if (!sharedPreferences.getString(uidKey, "").equals("")) {
            startHomeScreenActivity();
        } else {
            setContentView(R.layout.activity_main);
            emailField = findViewById(R.id.editTextTextEmailAddress);
            passwordField = findViewById(R.id.editTextTextPassword);
        }
    }

    //rotate logo
    @Override
    public void onStart() {
        super.onStart();
        i = (ImageView) findViewById(R.id.imageView3);
        Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotation);
        i.startAnimation(a);
    }

    /**
     * Simple login method for now that just checks if any email and password were entered
     *
     * @param view the view for the app
     */
    public void login(View view) {
        // Get email and password
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        // Check for an email
        if (email.isEmpty()) {
            showAlertDialog("No Email", "Please enter an email!");
            return;
        }

        // Check for a password
        if (password.isEmpty()) {
            showAlertDialog("No Password", "Please enter a password");
            return;
        }

        // Call on firebase to log the user in
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    // Check if the user has email verified onSuccess
                    if (firebaseUser.isEmailVerified()) {
                        String uid = firebaseUser.getUid();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        // Get the user's name and ID from firebase and them to shared preferences
                        DocumentReference docRef = db.collection("users").document(uid);
                        docRef.get().addOnSuccessListener(documentSnapshot -> {
                            User user = documentSnapshot.toObject(User.class);
                            SharedPreferences sharedPreferences = getSharedPreferences("com.example.shine", Context.MODE_PRIVATE);
                            sharedPreferences.edit().putString(nameKey, user.getName()).apply();
                            sharedPreferences.edit().putString(uidKey, uid).apply();

                            // Finally, go to home screen
                            startHomeScreenActivity();
                        }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "An error occurred while logging in. Try again.", Toast.LENGTH_LONG).show());
                    } else {
                        // If the user's email is not verified, resend an email and tell them to go and check it out
                        firebaseUser.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Check email to verify your account.", Toast.LENGTH_LONG).show();
                    }
                })
                // If they could not login, make a Toast telling them
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Failed to login!", Toast.LENGTH_SHORT).show());
    }

    /**
     * Helper method to quickly show an alert dialog, in this case used for if no email/password
     * were entered
     *
     * @param title what to set the title of the dialog to
     * @param message what to set the message of the dialog to
     */
    private void showAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialogInterface, i) -> dialogInterface.dismiss());
        alertDialog.show();
    }


    /**
     * Private helper methods to go to different activities
     */
    private void startHomeScreenActivity() {
        Intent intent = new Intent(this, HomeScreenActivity.class);
        startActivity(intent);
    }

    public void startRegisterActivity(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void startForgotPasswordActivity(View view) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }



}