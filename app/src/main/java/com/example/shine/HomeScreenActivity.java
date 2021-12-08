package com.example.shine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeScreenActivity extends AppCompatActivity {

    private AlertDialog dialog;
    private EditText amountEditText, dateEditText, vendorEditText;
    private Spinner categorySpinner, recurringSpinner;
    private LocalDate date;
    private final String emptyCategory = "<Category>";
    private final String emptyRecurring = "<Recurring>";
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT);

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
            startActivity(new Intent(this, SettingsActivity.class));
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
     */
    private void saveTransaction() {

        // Get all the fields for Transaction
        double amount = Double.parseDouble(amountEditText.getText().toString());
        Transaction.TransactionType category = Transaction.TransactionType
                .valueOf(categorySpinner.getSelectedItem().toString().toUpperCase(Locale.ROOT));
        Transaction.Recurring recurring = Transaction.Recurring
                .valueOf(recurringSpinner.getSelectedItem().toString().toUpperCase(Locale.ROOT));
        String vendor = vendorEditText.getText().toString().trim();


        // Make a transaction
        Transaction transaction = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            transaction = new Transaction(amount, category, recurring, date, vendor);
        }

        // TODO have this send to firebase
    }


    /**
     * This function creates the popup that allows a user to enter in a new transaction
     */
    public void newTransaction(View view) {
        // Stuff for transaction popup
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this).setNeutralButton("Save", null);

        // transaction_popup.xml in res/layout/
        final View transactionPopupView = getLayoutInflater().inflate(R.layout.transaction_popup, null);

        //Set up spinners for transaction_popup
        categorySpinner = transactionPopupView.findViewById(R.id.spinnerCategory);
        recurringSpinner = transactionPopupView.findViewById(R.id.spinnerRecurring);

        // Set up the categories to be from the Transaction class
        String[] categoryItems = new String[Transaction.TransactionType.values().length + 1];
        categoryItems[0] = emptyCategory;
        int i = 1;
        for (Transaction.TransactionType type : Transaction.TransactionType.values()) {
            categoryItems[i] = type.toString().charAt(0) + type.toString().substring(1).toLowerCase(Locale.ROOT);
            i++;
        }

        // Similar approach for Recurring
        String[] recurringItems = new String[Transaction.Recurring.values().length + 1];
        recurringItems[0] = emptyRecurring;
        i = 1;
        for (Transaction.Recurring type : Transaction.Recurring.values()) {
            recurringItems[i] = type.toString().charAt(0) + type.toString().substring(1).toLowerCase(Locale.ROOT);
            i++;
        }

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, categoryItems);
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<String> recurringAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, recurringItems);
        recurringSpinner.setAdapter(recurringAdapter);

        // Find all the other fields...
        amountEditText = transactionPopupView.findViewById(R.id.editTextTextTransactionAmount);
        dateEditText = transactionPopupView.findViewById(R.id.editTextDate);
        vendorEditText = transactionPopupView.findViewById(R.id.editTextTextVendor);

        dialogBuilder.setView(transactionPopupView);
        dialog = dialogBuilder.create();

        // Cancel button
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Cancel",
                ((dialogInterface, i1) -> dialogInterface.dismiss()));

        // Workaround solution to have popup, which is an AlertDialog validate fields and not
        // immediately dismiss the dialog...
        dialog.setOnShowListener(dialogInterface -> {
            Button save = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            save.setOnClickListener(view1 -> {
                if (amountEditText.getText().toString().isEmpty()) {
                    amountEditText.setError("Please enter an amount!");
                    amountEditText.requestFocus();
                    return;
                }

                if (dateEditText.getText().toString().isEmpty()) {
                    dateEditText.setError("Please enter a date!");
                    dateEditText.requestFocus();
                    return;
                }

                try {
                    date = LocalDate.parse(dateEditText.getText().toString(), format);
                } catch(DateTimeParseException e) {
                    dateEditText.setError("Please enter a valid date!");
                    dateEditText.requestFocus();
                    return;
                }

                if (vendorEditText.getText().toString().isEmpty()) {
                    vendorEditText.setError("Please enter the name of vendor!");
                    vendorEditText.requestFocus();
                    return;
                }

                if (categorySpinner.getSelectedItem().toString().equals(emptyCategory)) {
                    ((TextView)categorySpinner.getSelectedView()).setError("");
                    return;
                }

                if (recurringSpinner.getSelectedItem().toString().equals(emptyRecurring)) {
                    ((TextView)recurringSpinner.getSelectedView()).setError("");
                    return;
                }
                saveTransaction();
                dialog.dismiss();
            });
        });

        dialog.show();
    }
}