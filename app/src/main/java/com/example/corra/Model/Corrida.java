package com.example.corra.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_corrida")
public class Corrida {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "tempo")
    public float tempo;

    @ColumnInfo(name = "velocidade")
    public double velocidade;

    @ColumnInfo(name = "data")
    public String data;
}
