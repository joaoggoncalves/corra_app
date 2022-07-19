package com.example.corra;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.corra.Database.CorridaViewmodel;
import com.example.corra.Model.Corrida;
import com.example.corra.Velocidade.SpeedListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

// Teste cronometro
import android.widget.Chronometer;

public class RunFragment extends Fragment {

    TextView speedtv;
    FloatingActionButton btnPause;
    FloatingActionButton btnStop;
    private int segundos = 0;
    private boolean rodando = false;
    private boolean rodandoprimeiravez = true;
    //TODO:
    //Usar queue (?)
    Queue<Float> velocidades = new LinkedList<>();
    private static final String TAG = "RunFragment";
    long elapsedMillis;

    //Chronometer Variables
    private Chronometer chronometer;
    private long pauseOffset;

    public RunFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_run, container, false);
        btnPause = rootView.findViewById(R.id.pause_fab);
        btnStop = rootView.findViewById(R.id.stop_fab);

        chronometer = rootView.findViewById(R.id.chronometer);
        chronometer.setFormat("Time: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());

        btnPause.setOnClickListener(v -> {
            // Primeira vez que vai rodar o timer
            if(rodandoprimeiravez) {
                rodandoprimeiravez = false;
                btnStop.setVisibility(View.VISIBLE);
                btnPause.setImageResource(android.R.drawable.ic_media_pause);
                startTimer();
                startSpeed();
            } else if (!rodando) {
                // Usuário sai do estado pausado para o estado de corrida novamente
                // Timer  continua de onde parou
                btnPause.setImageResource(android.R.drawable.ic_media_pause);
                startTimer();
            } else {
                // Pausa o timer
                pauseChronometer();
                btnPause.setImageResource(android.R.drawable.ic_media_play);
            }
        });

        //Botão para/salva
        btnStop.setOnClickListener(v -> {
            pauseChronometer();
            //Média velocidades armazenadas
            double velFinal = velocidades.stream().mapToDouble(d -> d).average().orElse(0.0);
            //Cria modelo com valores da corrida atual
            Corrida corrida = new Corrida();
            corrida.tempo = elapsedMillis;
            corrida.velocidade = velFinal;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            corrida.data = dtf.format(now);
            //Salva corrida no database
            CorridaViewmodel viewmodel = new ViewModelProvider(this).get(CorridaViewmodel.class);
            viewmodel.insereCorrida(corrida);
            //Reseta
            rodando = false;
            segundos = 0;
            velocidades.clear();
            btnStop.setVisibility(View.INVISIBLE);
            rodandoprimeiravez = true;
            NavigationBarView navBar = getActivity().findViewById(R.id.bottomNavigationView);
            navBar.setSelectedItemId(R.id.homebottomnav);
        });
        return rootView;
    }


    //TODO:
    //Display distância
    private void startSpeed() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final Handler handler = new Handler();
        speedtv = getView().findViewById(R.id.speedtv);
        SpeedListener speedListener = new SpeedListener();
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //Thread velocidade
        handler.post(new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                if (rodando) {
                    //Checa permissão de location
                    //Fornece locations para listener
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, speedListener);

                    //Listener retorna médias
                    String vel = String.format(Locale.getDefault(), "%.2f", speedListener.avg);
                    speedtv.setText(vel);
                    //Armazena velocidades a cada 10 segundos (idealmente)
                    //TODO:
                    //Calibrar
                    if (segundos%10 == 0) {
                        velocidades.add(Float.parseFloat(speedtv.getText().toString()));
                    }
                    segundos++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void startTimer() {
        if (!rodando) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            rodando = true;
        }
    }
    public void pauseChronometer() {
            chronometer.stop();
            elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            rodando = false;
    }
}