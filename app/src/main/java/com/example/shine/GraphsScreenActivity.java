package com.example.shine;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;

public class GraphsScreenActivity extends AppCompatActivity {
    FirebaseFirestore db;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs_screen);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void pieChart(View view){
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        LocalDate past = LocalDate.now().minusMonths(1);
        Month month = past.getMonth();
        int year = past.getYear();
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Transaction> transactions = new ArrayList<>();
        final QueryDocumentSnapshot[] document1 = new QueryDocumentSnapshot[1];
        final String[] id = new String[1];
        db.collection("users").document(user.getUid()).collection(month.toString()+"-"+year)
            .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Transaction transaction = document.toObject(Transaction.class);
                                transactions.add(transaction);
                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        ArrayList<PieEntry> entries1 = entries;
    }
}