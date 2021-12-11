package com.example.shine;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
        db = FirebaseFirestore.getInstance();
        pieChart();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void pieChart(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        LocalDate past = LocalDate.now().minusMonths(1);
        Month month = past.getMonth();
        int year = past.getYear();
        ArrayList<PieEntry> entries = new ArrayList<>();
        db.collection("users").document(user.getUid()).collection(month.toString()+"-"+year)
            .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                entries.add(new PieEntry( (float) document.get("amount"), document.get("category")));
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}