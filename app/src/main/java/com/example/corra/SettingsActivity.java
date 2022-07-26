package com.example.corra;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    CheckBox intervalCb;
    TextView tempoandando;
    TextView tempocorrendo;
    TextView repeticoes;
    EditText andandoEdit;
    EditText correndoEdit;
    EditText repeticoesEdit;
    ActionBar barra;

    //NavHostFragment navHostFragment;
    //NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //navController = Navigation.findNavController(this, R.id.nav_host_fragment_settings);
        barra = getSupportActionBar();
        barra.setDisplayHomeAsUpEnabled(true);
        tempoandando = findViewById(R.id.tempoandandostring);
        tempocorrendo = findViewById(R.id.tempocorrendostring);
        repeticoes = findViewById(R.id.repeticoesstring);
        andandoEdit = findViewById(R.id.tempoAndandoInput);
        correndoEdit = findViewById(R.id.tempoCorrendoInput);
        repeticoesEdit = findViewById(R.id.repeticoesInput);
        intervalCb = findViewById(R.id.intervalcb);
        intervalCb.setOnClickListener(v -> {
            if (intervalCb.isChecked()) {
                tempoandando.setVisibility(View.VISIBLE);
                tempocorrendo.setVisibility(View.VISIBLE);
                repeticoes.setVisibility(View.VISIBLE);
                andandoEdit.setVisibility(View.VISIBLE);
                correndoEdit.setVisibility(View.VISIBLE);
                repeticoesEdit.setVisibility(View.VISIBLE);
            } else {
                tempoandando.setVisibility(View.INVISIBLE);
                tempocorrendo.setVisibility(View.INVISIBLE);
                repeticoes.setVisibility(View.INVISIBLE);
                andandoEdit.setVisibility(View.INVISIBLE);
                correndoEdit.setVisibility(View.INVISIBLE);
                repeticoesEdit.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}