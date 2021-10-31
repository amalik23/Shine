package com.example.shine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class HomeScreenActivity extends AppCompatActivity {
    private TextView welcomeText;

    /**
     * This is the onCreate method that sets the welcome text to the email in sharedPreferences
     * More functionality to be added in the future...
     *
     * @param savedInstanceState saved instance state of the app
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Display welcome message, can be removed or edited later if we want, just wanted something
        // on the screen for now
        welcomeText = (TextView) findViewById(R.id.textViewHomeScreenWelcome);
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.shine", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(MainActivity.emailKey, "");
        welcomeText.setHint("Welcome " + email);
    }

    /**
     * Overriding method that inflates the given menu with the home_screen_menu in res/menu
     *
     * @param menu to be inflated
     * @return true after menu is inflated
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_screen_menu, menu);    // home screen menu can be found in /res/menu
        return true;
    }

    /**
     * Overriding method that checks what menu item was selected and does the corresponding actions
     *
     * @param item that was selected
     * @return true if actions executed correctly, otherwise false
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.homeScreenLogout) {
            logout();
        } else if (item.getItemId() == R.id.homeScreenTransaction) {
            // eventually add functionality to switch to transaction screen
            // could have a dialog to enter in transaction amount and category
            return true;
        } else {
            return false;
        }
        return true;
    }

    /**
     * Private helper method to log the user out and return them to the login screen aka MainActivity
     */
    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.shine", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(MainActivity.emailKey, "").apply();  // wipe out the email
        startActivity(new Intent(this, MainActivity.class));
    }
}