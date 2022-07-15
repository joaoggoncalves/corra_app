package com.example.corra.Database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.corra.Model.Corrida;

import java.util.List;

public class CorridaRepo {
    private CorridaDAO corridaDao;
    private LiveData<List<Corrida>> allCorridas;

    CorridaRepo(Application application) {
        CorridaDatabase db = CorridaDatabase.getDatabase(application);
        corridaDao = db.corridaDAO();
        allCorridas = corridaDao.getAll();
    }

    LiveData<List<Corrida>> getTodasCorridas() {
        return allCorridas;
    }

    void insere(Corrida corrida) {
        CorridaDatabase.databaseWriteExecutor.execute(() -> {
            corridaDao.insereCorrida(corrida);
        });
    }
}
