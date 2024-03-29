package com.example.corra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    AlertDialog.Builder builder;
    private static final int PERMISSION_REQUEST_CODE = 99;
    NavigationBarView bottomNavigationView;
    NavHostFragment navHostFragment;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Checa permissão de localização caso não tenha faz requisição
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    99);
        }

        //Obtem navController
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        //Controla bottomNav
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() != R.id.corridabottomnav) {
                navController.navigate(R.id.nav_home);
            } else {
                navController.navigate(R.id.nav_corrida);
            }
            return true;
        });
    }

    //Trata resposta da requisição de permissão
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permissao concedida");
            } else {
                builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.dialog_message).setTitle(R.string.dialog_title).setNeutralButton(R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
                bottomNavigationView.getMenu().getItem(1).setEnabled(false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (navController.getCurrentDestination().getId() != R.id.nav_corrida) {
            super.onBackPressed();
        }
    }
}