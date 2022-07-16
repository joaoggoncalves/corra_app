package com.example.corra;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
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
import java.util.List;
import java.util.Locale;

public class RunFragment extends Fragment {

    TextView timertv;
    TextView speedtv;
    FloatingActionButton btnPause;
    FloatingActionButton btnStop;
    private int segundos = 0;
    private boolean rodando = false;
    private boolean rodandoprimeiravez = true;
    List<Float> velocidades = new ArrayList<>();

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
        // Inflate the layout for this fragment
        btnPause = rootView.findViewById(R.id.pause_fab);
        btnStop = rootView.findViewById(R.id.stop_fab);
        //Botão pausa/despausa
        btnPause.setOnClickListener(v -> {
            if (!rodando && rodandoprimeiravez) {
                rodando = true;
                rodandoprimeiravez = false;
                startTimer();
                btnStop.setVisibility(View.VISIBLE);
            } else rodando = !rodando;
            if (!rodando) {
                btnPause.setImageResource(android.R.drawable.ic_media_pause);
            } else {
                btnPause.setImageResource(android.R.drawable.ic_media_play);
            }
        });

        //Botão para/salva
        btnStop.setOnClickListener(v -> {
            //Média velocidades armazenadas
            double velFinal = velocidades.stream().mapToDouble(d -> d).average().orElse(0.0);

            //Cria modelo com valores da corrida atual
            Corrida corrida = new Corrida();
            corrida.tempo = segundos;
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

    //Função/Handler timer
    //TODO:
    //Utilizar classe Chronometer para resolver precisão
    private void startTimer() {
        final Handler handler = new Handler();
        timertv = getView().findViewById(R.id.timertv);
        speedtv = getView().findViewById(R.id.speedtv);
        SpeedListener speedListener = new SpeedListener();
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //Thread cronometro
        handler.post(new Runnable() {
            @Override

            public void run() {
                int horas = segundos / 3600;
                int minutos = (segundos % 3600) / 60;
                int segundosdisplay = segundos % 60;
                String tempo = String.format(Locale.getDefault(), "%d:%02d:%02d", horas, minutos, segundosdisplay);

                //Checa permissão de location
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                //Fornece locations para listener
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, speedListener);

                //Listener retorna médias
                String vel = String.format(Locale.getDefault(), "%.2f", speedListener.avg);
                speedtv.setText(vel);
                timertv.setText(tempo);
                if (rodando) {
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
}