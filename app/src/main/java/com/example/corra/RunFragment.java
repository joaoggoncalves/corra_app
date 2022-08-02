package com.example.corra;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.corra.Database.CorridaViewmodel;
import com.example.corra.Model.Corrida;
import com.example.corra.Velocidade.Intervalada;
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

public class RunFragment extends Fragment {

    TextView speedtv;
    TextView distanciatv;
    TextView intervalotv;
    TextView pacetv;
    FloatingActionButton btnPause;
    FloatingActionButton btnStop;
    FloatingActionButton btnSettings;
    private int segundos = 0;
    private boolean rodando = false;
    private boolean rodandoprimeiravez = true;
    Queue<Float> velocidades = new LinkedList<>();
    private static final String TAG = "RunFragment";
    long elapsedMillis;
    AlertDialog.Builder builder;
    NavigationBarView navBar;
    Intervalada interObj;
    boolean startIntervalda;
    Vibrator holdVib;
    SharedPreferences sharedPref;

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
        btnSettings = rootView.findViewById(R.id.settings_fab);
        intervalotv = rootView.findViewById(R.id.intervalotv);

        chronometer = rootView.findViewById(R.id.chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime());

        navBar = getActivity().findViewById(R.id.bottomNavigationView);
        ActionBar barra = ((AppCompatActivity)getActivity()).getSupportActionBar();
        barra.setTitle(R.string.actionbartitlecorrida);

        btnPause.setOnClickListener(v -> {
            if(rodandoprimeiravez) {
                //Intervalado Shared Preferences
                sharedPref = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
                if (sharedPref != null) {
                    startIntervalda = sharedPref.getBoolean(getString(R.string.intervalado), false);
                    int walkTime;
                    int runTime;
                    int reps;
                    if (startIntervalda) {
                        intervalotv.setText(R.string.walkinterval);
                        holdVib = initializeVibType();
                        runTime = sharedPref.getInt(getString(R.string.correndo), 0);
                        walkTime = sharedPref.getInt(getString(R.string.andando), 0);
                        reps = sharedPref.getInt(getString(R.string.reps), 0);
                        if (reps == 0) {
                            reps = -1;
                        }
                        interObj = new Intervalada(reps, walkTime, runTime, holdVib);
                    }
                }
                //Primeira vez que vai rodar o timer
                rodandoprimeiravez = false;
                btnStop.setVisibility(View.VISIBLE);
                btnPause.setImageResource(android.R.drawable.ic_media_pause);
                //Trava nav bar
                navBar.getMenu().getItem(0).setEnabled(false);
                navBar.getMenu().getItem(1).setEnabled(false);
                btnSettings.setEnabled(false);
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

        btnSettings.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(this.getContext(), SettingsActivity.class);
            startActivity(settingsIntent);
        });

        //Botão para/salva
        btnStop.setOnClickListener(v -> {
            if (rodando)
                pauseChronometer();
            //Destrava nav bar
            navBar.getMenu().getItem(0).setEnabled(true);
            navBar.getMenu().getItem(1).setEnabled(true);
            btnSettings.setEnabled(true);
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
            if (sharedPref != null) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.andando), 0);
                editor.putInt(getString(R.string.correndo), 0);
                editor.putInt(getString(R.string.reps), 0);
                editor.putBoolean(getString(R.string.intervalado), false);
                editor.apply();
            }
            navBar.setSelectedItemId(R.id.homebottomnav);
        });
        return rootView;
    }

    // Inicializa o vibrador de forma correta
    private Vibrator initializeVibType() {
        Vibrator holdVib;
        if (Build.VERSION.SDK_INT >=31) {
            holdVib = ((VibratorManager) this.getContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE)).getDefaultVibrator();
        }
        else  {
            holdVib = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        }
        return holdVib;
    }

    private void startSpeed() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            navBar.setSelectedItemId(R.id.homebottomnav);
            return;
        }
        final Handler handler = new Handler();
        speedtv = getView().findViewById(R.id.speedtv);
        distanciatv = getView().findViewById(R.id.distanciatv);
        pacetv = getView().findViewById(R.id.pacetv);
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
                    //Fornece locations para listener
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, speedListener);

                    //Listener retorna médias
                    String vel = String.format(Locale.getDefault(), "%.2f", speedListener.avg);
                    speedtv.setText(vel);
                    //Armazena velocidades a cada 10 segundos (idealmente)
                    if (segundos%5 == 0) {
                        velocidades.add(Float.parseFloat(speedtv.getText().toString().replace(",", ".")));
                        long tempodist = Duration.ofMillis(SystemClock.elapsedRealtime() - chronometer.getBase()).getSeconds();
                        double dist = (velocidades.stream().mapToDouble(d -> d).average().orElse(0.0)) * (tempodist/3600.0);
                        if (segundos > 0 && dist != 0.1) {
                            double pace = (segundos/60.0)/dist;
                            int mins = (int) pace;
                            String pacedisplay =  mins + ":" + (int) (60 *  (pace - mins)) + " min/Km";
                            //String velconversao = String.format(Locale.ENGLISH ,"%.2f min/Km", pace).replace(".", ":");
                            pacetv.setText(pacedisplay);
                        }
                        distanciatv.setText(String.format(Locale.getDefault(), "%.2f", dist));
                    }
                    segundos++;
                    if(startIntervalda) {
                        interObj.addHoldTime();
                        interObj.handleRepetition();
                        if(interObj.getRepeat() == 0) {
                            startIntervalda = false;
                            btnPause.callOnClick();
                        }
                        if (interObj.getLoop()) {
                            intervalotv.setText(R.string.walkinterval);
                        } else {
                            intervalotv.setText(R.string.runninginterval);
                        }
                    } else {
                        intervalotv.setText(R.string.intervaldisabled);
                    }
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
    private void pauseChronometer() {
            chronometer.stop();
            elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            rodando = false;
    }

    @Override
    public void onDestroy() {
        if (sharedPref != null) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.andando), 0);
            editor.putInt(getString(R.string.correndo), 0);
            editor.putInt(getString(R.string.reps), 0);
            editor.putBoolean(getString(R.string.intervalado), false);
            editor.apply();
        }
        super.onDestroy();
    }
}