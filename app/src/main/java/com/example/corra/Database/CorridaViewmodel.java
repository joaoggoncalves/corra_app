package com.example.corra.Database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.corra.Model.Corrida;

import java.util.List;

public class CorridaViewmodel extends AndroidViewModel {
    private CorridaRepo mRepo;
    private final LiveData<List<Corrida>> mAllCorridas;

    public CorridaViewmodel(Application application) {
        super(application);
        mRepo = new CorridaRepo(application);
        mAllCorridas = mRepo.getTodasCorridas();
    }

    public LiveData<List<Corrida>> getAllCorridas() {
        return mAllCorridas;
    }

    public void insereCorrida(Corrida corrida) {
        mRepo.insere(corrida);
    }

    public void deleteCorrida(int uid) {
        mRepo.delete(uid);
    }

    public LiveData<Corrida> selectCorrida(int uid) {
        return mRepo.selectById(uid);
    }
}
