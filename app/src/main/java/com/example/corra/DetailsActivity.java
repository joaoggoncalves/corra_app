package com.example.corra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.corra.Database.CorridaViewmodel;
import com.example.corra.Model.Corrida;

public class DetailsActivity extends AppCompatActivity {

    private static final String EXTRA_ID = "id";
    private static final String TAG = "DetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        CorridaViewmodel viewmodel = new ViewModelProvider(this).get(CorridaViewmodel.class);
        Intent intent = getIntent();
        int uid = intent.getIntExtra(EXTRA_ID, 0);
        Log.d(TAG, "ID: " + uid);
        viewmodel.selectCorrida(uid).observe(this, corrida -> {
            Log.d(TAG, "data  " + corrida.getData());
        });
    }
}