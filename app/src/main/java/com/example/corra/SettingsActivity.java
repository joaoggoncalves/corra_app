package com.example.corra;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        barra = getSupportActionBar();
        barra.setDisplayHomeAsUpEnabled(true);
        tempoandando = findViewById(R.id.tempoandandostring);
        tempocorrendo = findViewById(R.id.tempocorrendostring);
        repeticoes = findViewById(R.id.repeticoesstring);
        andandoEdit = findViewById(R.id.tempoAndandoInput);
        correndoEdit = findViewById(R.id.tempoCorrendoInput);
        repeticoesEdit = findViewById(R.id.repeticoesInput);
        intervalCb = findViewById(R.id.intervalcb);
        btnSave = findViewById(R.id.btn_savesettings);
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
        btnSave.setOnClickListener(v -> {
            SharedPreferences sharedPref = this.getSharedPreferences("settings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            if (intervalCb.isChecked()) {
                if (checkValues(andandoEdit, false) && checkValues(correndoEdit, false) && checkValues(repeticoesEdit, true)) {
                    editor.putInt(getString(R.string.andando), Integer.parseInt(andandoEdit.getText().toString().trim()));
                    editor.putInt(getString(R.string.correndo), Integer.parseInt(correndoEdit.getText().toString().trim()));
                    editor.putInt(getString(R.string.reps), Integer.parseInt(repeticoesEdit.getText().toString().trim()));
                    editor.putBoolean(getString(R.string.intervalado), true);
                    editor.apply();
                    finish();
                } else {
                    andandoEdit.setError("Valores inválidos");
                    correndoEdit.setError("Valores inválidos");
                    repeticoesEdit.setError("Valores inválidos");
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private boolean checkValues(EditText field, boolean zero) {
        int val;
        if (field.getText().toString().trim().length() == 0) {
            return false;
        }
        try {
            val = Integer.parseInt(field.getText().toString().trim());
        } catch (NumberFormatException e) {
            return false;
        }
        if (!zero)
            return val > 0;
        return val >= 0;
    }
}