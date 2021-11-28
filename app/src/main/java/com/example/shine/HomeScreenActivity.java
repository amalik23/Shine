package com.example.shine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;

public class HomeScreenActivity extends AppCompatActivity {
    private TextView welcomeText;

    // Stuff for transaction popup
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText newTransactionAmount;
    private Spinner newTransactionCategory;
    private Button newTransactionCancel, newTransactionSave;

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
        String name = sharedPreferences.getString(MainActivity.nameKey, "");
        welcomeText.setHint("Welcome " + name);
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
        } else if (item.getItemId() == R.id.homeScreenInfo) {
            // eventually add functionality to switch to info screen
            //startActivity(new Intent(this, MainActivity.class));
            return true;
        } else if (item.getItemId() == R.id.homeScreenSettings) {
            // eventually add functionality to switch to settings screen
            //startActivity(new Intent(this, MainActivity.class));
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
        sharedPreferences.edit().putString(MainActivity.uidKey, "").apply();  // wipe out the email
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * This is a private helper method to send the new transaction that the user entered and send it to
     * the backend
     *
     * @param amountEditText EditText from new transaction popup
     * @param categorySpinner Spinner from new transaction popup
     * @return the new transaction the user is trying to add
     */
    private Transaction saveTransaction(EditText amountEditText, Spinner categorySpinner) {
        // TODO have this send to the backend
        double amount = Double.parseDouble(amountEditText.getText().toString());
        Transaction.TransactionType category = Transaction.TransactionType
                .valueOf(categorySpinner.getSelectedItem().toString().toUpperCase(Locale.ROOT));
        Transaction transaction = new Transaction(amount, category);
        return transaction;
    }


    /**
     * This function creates the popup that allows a user to enter in a new transaction
     */
    public void newTransaction(View view) {
        dialogBuilder = new AlertDialog.Builder(this);

        // transaction_popup.xml in res/layout/
        final View transactionPopupView = getLayoutInflater().inflate(R.layout.transaction_popup, null);

        //Set up spinner for transaction_popup
        Spinner dropdown = transactionPopupView.findViewById(R.id.spinner1);

        // Set up the categories to be from the Transaction class
        String[] items = new String[Transaction.TransactionType.values().length];
        int i = 0;
        for (Transaction.TransactionType type : Transaction.TransactionType.values()) {
            items[i] = type.toString().charAt(0) + type.toString().substring(1).toLowerCase(Locale.ROOT);
            i++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        newTransactionAmount = (EditText) transactionPopupView.findViewById(R.id.editTextTextTransactionAmount);
        newTransactionCategory = (Spinner) transactionPopupView.findViewById(R.id.spinner1);

        dialogBuilder.setView(transactionPopupView);
        dialog = dialogBuilder.create();

        // Save button
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Save",
                (dialogInterface, i1) -> {
            saveTransaction(newTransactionAmount, newTransactionCategory);
            dialogInterface.dismiss();
                });

        // Cancel button
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Cancel",
                ((dialogInterface, i1) -> dialogInterface.dismiss()));

        dialog.show();
    }
}