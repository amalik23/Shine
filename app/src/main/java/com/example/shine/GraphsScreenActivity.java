package com.example.shine;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.Enumeration;
import java.util.Hashtable;

public class GraphsScreenActivity extends AppCompatActivity {
    FirebaseFirestore db;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs_screen);
    }

    private void setupPieChart(){
        PieChart pieChart = findViewById(R.id.pie_chart);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Previous Month's Expense Breakdown");
        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void pieChart(View view){
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        LocalDate past = LocalDate.now().minusMonths(1);
        Month month = past.getMonth();
        int year = past.getYear();

        db.collection("users").document(user.getUid()).collection(month.toString()+"-"+year)
            .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            setupPieChart();
                            Double totalExp = 0.0;
                            ArrayList<PieEntry> entries = new ArrayList<>();
                            Hashtable<String, Double> SpendingByCat = new Hashtable<String, Double>();
                            PieChart pieChart = findViewById(R.id.pie_chart);
                            Log.d("TAG", "HERE");

                            for (DocumentSnapshot document : task.getResult()) {
                                Transaction transaction = document.toObject(Transaction.class);
                                SpendingByCat.put(transaction.getCategory().name(),transaction.getAmount());
                                totalExp = totalExp + transaction.getAmount();
                            }
                            Enumeration<String> e = SpendingByCat.keys();

                            while (e.hasMoreElements()) {

                                // Getting the key of a particular entry
                                String key = e.nextElement();

                                double spending = SpendingByCat.get(key);

                                float perc = ((Double)(spending/totalExp)).floatValue();

                                // Print and display the Rank and Name
                                entries.add(new PieEntry(perc, key));
                            }

                            ArrayList<Integer> colors = new ArrayList<>();
                            for (int color : ColorTemplate.MATERIAL_COLORS){
                                colors.add(color);
                            }
                            for (int color : ColorTemplate.VORDIPLOM_COLORS){
                                colors.add(color);
                            }

                            PieDataSet pieDataSet = new PieDataSet(entries,"Expense Categories");
                            pieDataSet.setColors(colors);

                            PieData pieData = new PieData(pieDataSet);
                            pieData.setDrawValues(true);
                            //pieData.setValueFormatter(new PercentFormatter(pieChart));
                            pieData.setValueTextSize(12f);
                            pieData.setValueTextColor(Color.BLACK);

                            pieChart.setData(pieData);
                            pieChart.invalidate();

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}