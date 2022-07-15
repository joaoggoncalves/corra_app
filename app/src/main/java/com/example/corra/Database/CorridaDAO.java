package com.example.corra.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.corra.Model.Corrida;

import java.util.List;

@Dao
interface CorridaDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insereCorrida(Corrida corrida);

    @Query("SELECT * FROM table_corrida")
    LiveData<List<Corrida>> getAll();

    //TODO:
    //Selects Ordenados por data, tempo e velocidade
}
