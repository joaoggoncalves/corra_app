package com.example.corra.Velocidade;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.LinkedBlockingQueue;

public class SpeedListener implements LocationListener {

    private static final String TAG = "SpeedListener";
    private static final int BUFFER_SIZE = 3;

    private final LinkedBlockingQueue<Float> lastSpeeds = new LinkedBlockingQueue<>();

    public float avg = 0.0f;

    @Override
    public void onLocationChanged(Location location) {

        //Obtem velocidade + conversão de m/s para km/h
        float speed = location.getSpeed() * 3.6f;

        //Adiciona speed atual a queue
        lastSpeeds.add(speed);

        //Cria media utilizando BUFFER_SIZE velocidades
        float sum = 0.0f;
        for(float f : lastSpeeds) {
            sum += f;
        }
        avg = sum / lastSpeeds.size();

        Log.d(TAG,"Speed:" + speed + " avg:" + avg + " => " + lastSpeeds + " accuracy: " + location.getSpeedAccuracyMetersPerSecond());

        while (lastSpeeds.size() > BUFFER_SIZE) {
            //Log.d(TAG, "Removendo : " + lastSpeeds.size());
            lastSpeeds.poll();
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.d(TAG, "Provider Enabled");
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.d(TAG, "Provider Disabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "Status changed");
    }
}
