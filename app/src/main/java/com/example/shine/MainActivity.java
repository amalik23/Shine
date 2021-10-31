package com.example.shine;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    static final String emailKey = "emailKey";        // key for shared preferences

    /**
     * Method checks if an email already exists within SharedPreferences and goes to the home screen
     * for that user if it does, otherwise stays on a login screen
     *
     * @param savedInstanceState the saved instance state of the app
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.shine", Context.MODE_PRIVATE);

        // if sharedPreferences doesn't contain an email key initialize it
        // in the future this could also tell if the user has just installed the app
        // and we can give them tips
        if (!sharedPreferences.contains(emailKey))
            sharedPreferences.edit().putString(emailKey, "").apply();

        // if a username exists, go to home screen, otherwise just have login screen
        if (!sharedPreferences.getString(emailKey, "").equals("")) {
            startHomeScreenActivity();
        } else {
            setContentView(R.layout.activity_main);
        }
    }

    /**
     * Simple login method for now that just checks if any email and password were entered
     *
     * @param view the view for the app
     */
    public void login(View view) {
        // Get email and password
        EditText emailField = (EditText) findViewById(R.id.editTextTextEmailAddress);
        EditText passwordField = (EditText) findViewById(R.id.editTextTextPassword);
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

        // add email to SharedPreferences object
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.shine", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(emailKey, email).apply();

        // Start the home screen
        startHomeScreenActivity();
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
     * Private helper method to go to the home screen
     */
    private void startHomeScreenActivity() {
        Intent intent = new Intent(this, HomeScreenActivity.class);
        startActivity(intent);
    }
}