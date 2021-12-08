package com.example.shine;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends AppCompatActivity {

    private EditText emailEditText, nameEditText;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("com.example.shine", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(MainActivity.emailKey, "");
        String name = sharedPreferences.getString(MainActivity.nameKey, "");

        emailEditText = findViewById(R.id.editTextSettingsEmail);
        nameEditText = findViewById(R.id.editTextSettingsName);

        emailEditText.setText(email);
        nameEditText.setText(name);
    }

    public void saveButton(View view) {
        boolean emailBool = emailEditText.getText().toString().trim().equals(sharedPreferences.getString(MainActivity.emailKey, ""));
        boolean nameBool = nameEditText.getText().toString().trim().equals(sharedPreferences.getString(MainActivity.nameKey, ""));

        if (emailBool && nameBool) {
            Toast.makeText(SettingsActivity.this, "Name and Email are the same", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = sharedPreferences.getString(MainActivity.uidKey, "");
        User user = new User(sharedPreferences.getString(MainActivity.nameKey, ""), sharedPreferences.getString(MainActivity.emailKey, ""));

        if (!emailBool) {
            String email = emailEditText.getText().toString().trim();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Please provide valid email!");
                emailEditText.requestFocus();
                return;
            }
            firebaseUser.updateEmail(email).addOnCompleteListener(task -> {
                Drawable checkmark = ContextCompat.getDrawable(SettingsActivity.this, R.drawable.ic_baseline_check_24);
                checkmark.setBounds(0, 0, checkmark.getIntrinsicWidth(), checkmark.getIntrinsicHeight());
                emailEditText.setError("Please validate email address", checkmark);
                emailEditText.requestFocus();
                firebaseUser.sendEmailVerification();
                user.setEmail(email);
                sharedPreferences.edit().putString(MainActivity.emailKey, email).apply();
            }).addOnFailureListener(e -> {
                emailEditText.setError("An error occurred please try again!");
                emailEditText.requestFocus();
            });
        }

        if (!nameBool) {
            String name =  nameEditText.getText().toString().trim();
            user.setName(name);
            sharedPreferences.edit().putString(MainActivity.nameKey, name).apply();
        }

        db.collection("users").document(uid).set(user);
    }

    public void changePassword(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this).setNeutralButton("Confirm", null);

        final View passwordPopupView = getLayoutInflater().inflate(R.layout.password_popup, null);

        dialogBuilder.setView(passwordPopupView);
        AlertDialog dialog = dialogBuilder.create();

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Dismiss",
                ((dialogInterface, i1) -> dialogInterface.dismiss()));

        dialog.setOnShowListener(dialogInterface -> {
            Button confirm = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            confirm.setOnClickListener(v -> {
                EditText passwordEditText = passwordPopupView.findViewById(R.id.editTextPopupPassword);
                String password = passwordEditText.getText().toString();

                if (password.isEmpty()) {
                    passwordEditText.setError("Password is required");
                    passwordEditText.requestFocus();
                    return;
                }

                mAuth.signInWithEmailAndPassword(sharedPreferences.getString(MainActivity.emailKey, ""), password).addOnSuccessListener(authResult -> {
                    mAuth.sendPasswordResetEmail(sharedPreferences.getString(MainActivity.emailKey,""));
                    Drawable checkmark = ContextCompat.getDrawable(SettingsActivity.this, R.drawable.ic_baseline_check_24);
                    checkmark.setBounds(0, 0, checkmark.getIntrinsicWidth(), checkmark.getIntrinsicHeight());
                    passwordEditText.setError("Check email to reset password", checkmark);
                    passwordEditText.requestFocus();
                }).addOnFailureListener(e -> {
                    passwordEditText.setError("Password incorrect!");
                    passwordEditText.requestFocus();
                });
                // dialog.dismiss();
            });
        });

        dialog.show();
    }
}