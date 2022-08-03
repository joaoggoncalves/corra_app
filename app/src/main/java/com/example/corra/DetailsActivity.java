package com.example.corra;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.example.corra.Database.CorridaViewmodel;
import com.example.corra.Model.Corrida;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DetailsActivity extends AppCompatActivity {

    private static final String EXTRA_ID = "id";
    private static final String TAG = "DetailsActivity";
    TextView horariotv;
    TextView datatv;
    TextView disttv;
    TextView tempotv;
    TextView pacetv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        horariotv = findViewById(R.id.horario_detalhe_tv);
        datatv = findViewById(R.id.data_detalhes_tv);
        disttv = findViewById(R.id.distancia_detalhes_tv);
        tempotv = findViewById(R.id.tempo_detalhes_tv);
        pacetv = findViewById(R.id.pace_detalhes_tv);
        ActionBar barra = getSupportActionBar();
        barra.setDisplayHomeAsUpEnabled(true);
        CorridaViewmodel viewmodel = new ViewModelProvider(this).get(CorridaViewmodel.class);
        Intent intent = getIntent();
        int uid = intent.getIntExtra(EXTRA_ID, 0);
        viewmodel.selectCorrida(uid).observe(this, corrida -> {
            String data = corrida.getData().substring(0, 10);
            String hora = corrida.getData().substring(11);
            datatv.setText(data);
            String hr = getString(R.string.start_color) + hora + "</font>";
            horariotv.setText(Html.fromHtml(hr));
            disttv.setText(String.format(Locale.getDefault(), "%.2f KM", (corrida.getVelocidade()*(Duration.ofMillis(corrida.getTempo()).getSeconds()/3600.0))));
            if (Duration.ofMillis(corrida.getTempo()).getSeconds() > 3600) {
                String tempo = getDateFromMillis(corrida.getTempo());
                String formatada = tempo.substring(0,2) + "h " + tempo.substring(3,5) + "m" + tempo.substring(6,8);
                tempotv.setText(formatada);
            } else {
                String tempo = getDateFromMillis2(corrida.getTempo());
                String formatada = tempo.substring(0,2) + "m " + tempo.substring(3,5) + "s";
                tempotv.setText(formatada);
            }
            double pace = 1/(corrida.getVelocidade()/60);
            if (pace < 60.0) {
                int mins = (int) pace;
                String pacedisplay = mins + ":" + (int) (60 * (pace - mins)) + " min/Km";
                pacetv.setText(pacedisplay);
            }
        });
    }

    //Mostra horas
    public static String getDateFromMillis(long millis) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(new Date(millis));
    }

    //Não mostra horas
    public static String getDateFromMillis2(long millis) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(new Date(millis));
    }
}