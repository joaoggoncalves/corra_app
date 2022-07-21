package com.example.corra.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_corrida")
public class Corrida {

    public Corrida() {
    }

    public Corrida(int uid, long tempo, double velocidade, String data) {
        this.uid = uid;
        this.tempo = tempo;
        this.velocidade = velocidade;
        this.data = data;
    }

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "tempo")
    public long tempo;

    @ColumnInfo(name = "velocidade")
    public double velocidade;

    @ColumnInfo(name = "data")
    public String data;

    public String getData() {
        return data;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setTempo(long tempo) {
        this.tempo = tempo;
    }

    public void setVelocidade(double velocidade) {
        this.velocidade = velocidade;
    }

    public void setData(String data) {
        this.data = data;
    }

    public double getVelocidade() {
        return velocidade;
    }

    public long getTempo() {
        return tempo;
    }

    public int getUid() {
        return uid;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Corrida)) {
            return false;
        }

        Corrida c = (Corrida) o;
        return c.uid == this.uid;
    }
}
