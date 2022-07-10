package com.example.corra.Velocidade;

import android.location.Location;
import android.location.LocationListener;
import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;

public class SpeedListener implements LocationListener {

    private static final String TAG = "SpeedListener";
    private static final int BUFFER_SIZE = 4;

    private final LinkedBlockingQueue<Float> lastSpeeds = new LinkedBlockingQueue<>();

    private float lastAvg = 0.0f;
    public float avg = 0.0f;

    @Override
    public void onLocationChanged(Location location) {

        float speed = location.getSpeed() * 3.6f;
        lastSpeeds.add(speed);

        float sum = 0.0f;
        for(float f : lastSpeeds) {
            sum += f;
        }
        avg = sum / lastSpeeds.size();

        Log.d(TAG,"Speed:" + speed + " avg:" + avg + " => " + lastSpeeds);

        while (lastSpeeds.size() > BUFFER_SIZE) {
            //Logger.d(TAG, "Removing : " + lastSpeeds.size());
            lastSpeeds.poll();
        }
    }
}
