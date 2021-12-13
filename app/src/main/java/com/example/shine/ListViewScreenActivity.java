package com.example.shine;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

public class ListViewScreenActivity extends AppCompatActivity {
    FirebaseFirestore db;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_screen);

        Spinner monthSpin = findViewById(R.id.monthCategory);
        Spinner yearSpin = findViewById(R.id.yearCategory);

        String[] monthItems = new String[]{"JANUARY","FEBRUARY","MARCH","APRIL",
                "JUNE","JULY","AUGUST","SEPTEMBER","OCTOBER","NOVEMBER","DECEMBER"};

        int current_year = LocalDate.now().getYear();

        String[] yearItems = new String[11];

        for(int i = 0; i < 11; i ++){
            yearItems[i] = ((Integer)(current_year - i)).toString();
        }

        LocalDate past = LocalDate.now().minusMonths(1);
        Month month = past.getMonth();
        int year = past.getYear();

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, monthItems);
        monthSpin.setAdapter(monthAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, yearItems);
        yearSpin.setAdapter(yearAdapter);

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void generateTable(View view){
        Spinner monthSpin = findViewById(R.id.monthCategory);
        Spinner yearSpin = findViewById(R.id.yearCategory);

        String month = monthSpin.getSelectedItem().toString();
        String year_s = yearSpin.getSelectedItem().toString();
        int year = Integer.parseInt(year_s);

        transacTable(month, year);
    }

    private void setupListView(){
        ListView transacListView = findViewById(R.id.transacListV);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void transacTable(String month, int year){
        //fetch database & user ID
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("users").document(user.getUid()).collection(month.toString()+"-"+year)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ListView transacListView = findViewById(R.id.transacListV);
                            //list to collect transacs intoto & list for plaintexts for display
                            ArrayList<Transaction> uTransacs = new ArrayList<>();
                            ArrayList<String> display = new ArrayList<>();

                            //scan in transacs from document into transaction arraylist
                            for (DocumentSnapshot document : task.getResult()) {
                                Transaction transaction = document.toObject(Transaction.class);
                                uTransacs.add(transaction);
                            }
                            //sort list to be in reverse chronological order
                            Collections.sort(uTransacs, Transaction.dateSorter);
                            //insert top row for empty month
                            if(uTransacs.size() == 0) {
                                display.add("No entries for this month & year");
                            }

                            //pulls relevant data from each transac, adds formatted string to display
                            String ven;
                            double amt;
                            String catag;
                            int dat;
                            double total = 0;
                            for(Transaction T : uTransacs){
                                //abbreviate vendor name if need be
                                ven = T.getVendor().trim();
                                if(ven.length() > 11){
                                    ven = ven.substring(0,11) + "-";
                                }
                                //get amount, add to total
                                amt = T.getAmount();
                                total += amt;
                                //abbreviate catagory if need be
                                catag = T.getCategory().toString();
                                if(catag.equals("RECREATION")){
                                    catag = "RECR.";
                                }
                                dat = T.getDate();
                                display.add(String.format("%-12.12s\t\t|\t%10.2f\t|\t%10s\t|\t%10d", ven, amt, catag, dat));
                            }
                            //update total
                            TextView tBox = findViewById(R.id.totalBox);
                            tBox.setText(String.format("Total: $%.2f", total));
                            //apply adapter to listView
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                    (getApplicationContext(), android.R.layout.simple_list_item_1, display);
                            transacListView.setAdapter(adapter);

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}