package com.example.corra;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.corra.Database.CorridaViewmodel;
import com.example.corra.Model.Corrida;

public class DetailsActivity extends AppCompatActivity {

    private static final String EXTRA_ID = "id";
    private static final String TAG = "DetailsActivity";
    TextView horariotv;
    TextView datatv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        horariotv = findViewById(R.id.horario_detalhe_tv);
        datatv = findViewById(R.id.data_detalhes_tv);
        ActionBar barra = getSupportActionBar();
        barra.setDisplayHomeAsUpEnabled(true);
        CorridaViewmodel viewmodel = new ViewModelProvider(this).get(CorridaViewmodel.class);
        Intent intent = getIntent();
        int uid = intent.getIntExtra(EXTRA_ID, 0);
        Log.d(TAG, "ID: " + uid);
        viewmodel.selectCorrida(uid).observe(this, corrida -> {
            String data = corrida.getData().substring(0, 10);
            String hora = corrida.getData().substring(11);
            datatv.setText(data);
            horariotv.setText(hora);
            Log.d(TAG, "data  " + corrida.getData());
        });
    }
}