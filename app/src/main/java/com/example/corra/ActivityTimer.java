package com.example.corra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.example.corra.Velocidade.SpeedListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class ActivityTimer extends AppCompatActivity {

    TextView timertv;
    TextView speedtv;
    FloatingActionButton btnPause;
    private int segundos = 0;
    private boolean rodando = false;
    private boolean rodandoprimeiravez = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        btnPause = findViewById(R.id.pause_fab);
        btnPause.setOnClickListener(v -> {
            if (!rodando && rodandoprimeiravez) {
                rodando = true;
                rodandoprimeiravez = false;
                startTimer();
            } else rodando = !rodando;
        });
    }

    private void startTimer() {
        final Handler handler = new Handler();
        timertv = findViewById(R.id.timertv);
        speedtv = findViewById(R.id.speedtv);
        SpeedListener speedListener = new SpeedListener();
        LocationManager locationManager = (LocationManager) ActivityTimer.this.getSystemService(Context.LOCATION_SERVICE);

        handler.post(new Runnable() {
            @Override

            public void run() {
                int horas = segundos / 3600;
                int minutos = (segundos % 3600) / 60;
                int segundosdisplay = segundos % 60;
                String tempo = String.format(Locale.getDefault(), "%d:%02d:%02d", horas, minutos, segundosdisplay);
                if (ActivityCompat.checkSelfPermission(ActivityTimer.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ActivityTimer.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, speedListener);
                String vel = String.format(Locale.getDefault(), "%.2f km/h", speedListener.avg);
                speedtv.setText(vel);
                timertv.setText(tempo);
                if (rodando) {
                    segundos++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
}