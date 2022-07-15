package com.example.corra.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.corra.Model.Corrida;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Corrida.class}, version = 1)
public abstract class CorridaDatabase extends RoomDatabase {
    public abstract CorridaDAO corridaDAO();
    private static volatile CorridaDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static CorridaDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CorridaDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    CorridaDatabase.class, "corrida_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
