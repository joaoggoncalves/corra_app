package com.example.corra.Database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.corra.Model.Corrida;

import java.util.List;

public class CorridaRepo {
    private static final String TAG = "Repo";
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
        CorridaDatabase.databaseWriteExecutor.execute(() -> corridaDao.insereCorrida(corrida));
    }

    void delete(int uid) {
        CorridaDatabase.databaseWriteExecutor.execute(() -> corridaDao.deleteCorrida(uid));
    }

    LiveData<Corrida> selectById(int uid) {
        return corridaDao.getCorridaById(uid);
    }
}
