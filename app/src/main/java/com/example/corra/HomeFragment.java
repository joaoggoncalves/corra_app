package com.example.corra;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.corra.Database.CorridaViewmodel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Lê do database (viewmodel)
        CorridaViewmodel viewmodel = new ViewModelProvider(this).get(CorridaViewmodel.class);
        viewmodel.getAllCorridas().observe(getViewLifecycleOwner(), corridas -> {
            for (int i=0; i<corridas.size(); i++) {
                Log.d(TAG, "Corrida " + i + ": Tempo: " + corridas.get(i).tempo + " Distancia: " + corridas.get(i).velocidade * (corridas.get(i).tempo/3600));
            }
        });
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}