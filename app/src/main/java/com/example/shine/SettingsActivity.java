package com.example.shine;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends AppCompatActivity {

    private EditText emailEditText, nameEditText;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private AlertDialog dialog;
    private String password;

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

        //back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

        popup();
        dialog.setOnDismissListener(dialogInterface -> {
            if (password.equals(""))
                return;
            mAuth.signInWithEmailAndPassword(sharedPreferences.getString(MainActivity.emailKey, ""), password).addOnSuccessListener(authResult -> {


                if (!nameBool) {
                    String name =  nameEditText.getText().toString().trim();
                    user.setName(name);
                    sharedPreferences.edit().putString(MainActivity.nameKey, name).apply();
                    Toast.makeText(SettingsActivity.this, "Name has been changed", Toast.LENGTH_LONG).show();
                    db.collection("users").document(uid).set(user);
                }

                if (!emailBool) {
                    String email = emailEditText.getText().toString().trim();
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailEditText.setError("Please provide valid email!");
                        emailEditText.requestFocus();
                        return;
                    }

                    firebaseUser.updateEmail(email).addOnSuccessListener(task -> {
                        user.setEmail(email);
                        sharedPreferences.edit().putString(MainActivity.emailKey, email).apply();
                        firebaseUser.sendEmailVerification();
                        Toast.makeText(SettingsActivity.this, "Check new email to verify address", Toast.LENGTH_LONG).show();
                        db.collection("users").document(uid).set(user);
                    }).addOnFailureListener(e -> {
                        emailEditText.setError("An error occurred please try again!");
                        emailEditText.requestFocus();
                    });
                }
            }).addOnFailureListener(e -> Toast.makeText(SettingsActivity.this, "Password incorrect!", Toast.LENGTH_LONG).show());
        });
}

    private void popup() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this).setNeutralButton("Confirm", null);

        final View passwordPopupView = getLayoutInflater().inflate(R.layout.password_popup, null);

        dialogBuilder.setView(passwordPopupView);
        dialog = dialogBuilder.create();

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Dismiss",
                ((dialogInterface, i1) -> {
                    password = "";
                    dialogInterface.dismiss();}));

        dialog.setOnShowListener(dialogInterface -> {
            Button confirm = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            confirm.setOnClickListener(v -> {
                EditText passwordEditText = passwordPopupView.findViewById(R.id.editTextPopupPassword);
                password = passwordEditText.getText().toString();
                if (password.isEmpty()) {
                    passwordEditText.setError("Password is required");
                    passwordEditText.requestFocus();
                    return;
                }

                dialog.dismiss();
            });
        });

        dialog.show();
    }

    public void changePassword(View view) {
        popup();
        dialog.setOnDismissListener(dialogInterface -> {
            if (password.equals(""))
                return;
            mAuth.signInWithEmailAndPassword(sharedPreferences.getString(MainActivity.emailKey, ""), password).addOnSuccessListener(authResult -> {
            Toast.makeText(SettingsActivity.this, "Check email to reset password", Toast.LENGTH_LONG).show();
            mAuth.sendPasswordResetEmail(sharedPreferences.getString(MainActivity.emailKey, ""));
            }).addOnFailureListener(e -> Toast.makeText(SettingsActivity.this, "Password incorrect!", Toast.LENGTH_LONG).show());
        });
    }

}