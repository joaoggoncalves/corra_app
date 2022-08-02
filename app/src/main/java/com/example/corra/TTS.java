package com.example.corra;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TTS {
    TextToSpeech tts;
    CharSequence phrase;
    Bundle holdBundle;
    Context activityContext;
    public TTS(Bundle aux, Context ctx) {
        holdBundle = aux;
        phrase = ctx.getString(R.string.ttsText);
        activityContext = ctx;
    }

    public void ttsSpeak() {
        tts = new TextToSpeech(activityContext, i -> {
            if(i == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.getDefault());
                tts.setSpeechRate(1.0f);
                //tts.speak(phrase,TextToSpeech.QUEUE_ADD, null);
                tts.speak(phrase,TextToSpeech.QUEUE_ADD, holdBundle, null);
            }
        });
    }

}
