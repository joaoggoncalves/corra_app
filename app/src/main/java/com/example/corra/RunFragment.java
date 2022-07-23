package com.example.corra;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.VibratorManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.corra.Database.CorridaViewmodel;
import com.example.corra.Model.Corrida;
import com.example.corra.Velocidade.SpeedListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

import android.widget.Chronometer;
import android.os.Vibrator;


public class RunFragment extends Fragment {

    TextView speedtv;
    TextView distanciatv;
    FloatingActionButton btnPause;
    FloatingActionButton btnStop;
    private int segundos = 0;
    private boolean rodando = false;
    private boolean rodandoprimeiravez = true;
    Queue<Float> velocidades = new LinkedList<>();
    private static final String TAG = "RunFragment";
    long elapsedMillis;
    AlertDialog.Builder builder;
    NavigationBarView navBar;

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

        navBar = getActivity().findViewById(R.id.bottomNavigationView);

        btnPause.setOnClickListener(v -> {
            if(rodandoprimeiravez) {
                // Primeira vez que vai rodar o timer
                rodandoprimeiravez = false;
                btnStop.setVisibility(View.VISIBLE);
                btnPause.setImageResource(android.R.drawable.ic_media_pause);
                startNewFeature();
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
            if (rodando)
                pauseChronometer();
            //Média velocidades armazenadas
            double velFinal = velocidades.stream().mapToDouble(d -> d).average().orElse(0.0);
            //Cria modelo com valores da corrida atual
            Corrida corrida = new Corrida();
            //TODO:
            //Armazenar com precisão, atualmente joga segundos para baixo
            corrida.tempo = Duration.ofMillis(elapsedMillis).getSeconds();
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
            navBar.setSelectedItemId(R.id.homebottomnav);
        });
        return rootView;
    }
    // TESTES JEAN
    int repeat = 3;
    int loopMaior = 1;
    float loopMenor = 0.5f;
    Boolean nextVibType = true;   // True for big loop, False for small loop
    Boolean startFeature = true;
    long[] form = {0, 1000,300, 1000 };
    int testeTime = 0; // TESTE
    Vibrator v;
    private void smallWheel() {
        if (Build.VERSION.SDK_INT >= 26) {
            v.vibrate(VibrationEffect.createWaveform(form, -1));
        }
        else {
            v.vibrate(form, -1);
        }
        repeat--;
        nextVibType = false;
    }
    private void bigWheel() {
        if (Build.VERSION.SDK_INT >= 26) {
            v.vibrate(VibrationEffect.createOneShot(1400, VibrationEffect.DEFAULT_AMPLITUDE));
        }
        else {
            v.vibrate(1400);
        }
        nextVibType = true;
    }
    private void startNewFeature() {
        VibratorManager vibManager;
        if (Build.VERSION.SDK_INT >=31) {
            vibManager = (VibratorManager)this.getContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            v = vibManager.getDefaultVibrator();
        }
        else  {
            v = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        }

    }
    // FIM TESTES JEAN


    //TODO:
    //Display distância
    private void startSpeed() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            navBar.setSelectedItemId(R.id.homebottomnav);
            return;
        }
        final Handler handler = new Handler();
        speedtv = getView().findViewById(R.id.speedtv);
        distanciatv = getView().findViewById(R.id.distanciatv);
        SpeedListener speedListener = new SpeedListener();
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            builder = new AlertDialog.Builder(this.getContext());
            builder.setMessage(getString(R.string.gpsdialogmessage)).setTitle(getString(R.string.gpsdialogtitle)).setNeutralButton(R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
            navBar.setSelectedItemId(R.id.homebottomnav);
            return;
        }

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
                        velocidades.add(Float.parseFloat(speedtv.getText().toString().replace(",", ".")));
                        long tempodist = Duration.ofMillis(SystemClock.elapsedRealtime() - chronometer.getBase()).getSeconds();
                        double dist = (velocidades.stream().mapToDouble(d -> d).average().orElse(0.0)) * (tempodist/3600.0);
                        distanciatv.setText(String.format(Locale.getDefault(), "%.2f", dist));
                    }
                    segundos++;
                    testeTime++;
                    handleRepetion();
                    if (repeat == 0) {
                        btnPause.callOnClick();
                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
    private void handleRepetion() {
        if(nextVibType && startFeature && testeTime % (loopMaior * 60) == 0) {
            smallWheel();
            testeTime = 0;
        }
         else if(!nextVibType &&startFeature && testeTime % (loopMenor * 60) == 0) {
            bigWheel();
            testeTime = 0;
        }
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