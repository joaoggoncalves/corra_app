package com.example.corra.Velocidade;

import android.os.VibrationEffect;
import android.os.Vibrator;

public class Intervalada {
    private int repeat;
    private int walkTime = 0;
    private int runTime = 0;
    private Boolean nextVibrationType = true;  // true para walkTime VIB, falso para runTime VIB
    private final long[] waveForm = {0, 1000, 300, 1000};
    private final Vibrator v;
    private int holdTime = 0;

    public Intervalada(int repeat, int walkTime, int runTime, Vibrator v) {
        this.repeat = repeat;
        this.walkTime = walkTime;
        this.runTime = runTime;
        this.v = v;
    }

    private void setRepeat(int x) {
        this.repeat = x;
    }

    private void setWalkTime(int x) {
        this.walkTime = x;
    }
    private void setRunTime(int x) {
        this.walkTime = x;
    }

    private void setNextVibrationType() {
        this.nextVibrationType = !nextVibrationType;
    }
    // Uma vibração longa representa o loop andar (loop externo)
    private void walkLoop() {
        v.vibrate(VibrationEffect.createOneShot(1400, VibrationEffect.DEFAULT_AMPLITUDE));
        setNextVibrationType();
    }
    // Duas vibrações representa o loop correr (loop interno) no formato waveForm
    // o -1 serve pra vibrar apenas uma vez
    private void runLoop() {
        v.vibrate(VibrationEffect.createWaveform(waveForm, -1));
        setNextVibrationType();
    }
    public void addHoldTime() {
        this.holdTime += 1;
    }
    public int getHoldTime() {
        return this.holdTime;
    }
    public int getRepeat() {
        return this.repeat;
    }

    // Trata a ordem das vibrações
    public void handleRepetition() {
        // Loop externo (andar)
        if(this.nextVibrationType && this.holdTime % (this.walkTime) == 0) {
            runLoop();
            this.holdTime = 0;
        }
        //Loop interno (correr)
        //Sempre que rodar o loop de corrida, acabou um ciclo da repetição
        // Logo, repeat -= 1
        else if(!this.nextVibrationType  && this.holdTime % (runTime) == 0) {
            walkLoop();
            this.holdTime = 0;
            this.repeat -=1;
        }
    }


}
