package br.great.jogopervasivo.util;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

import br.great.jogopervasivo.actvititesDoJogo.Mapa;

public class TTSManager {
    public TextToSpeech tts;
    private String TAG = "TTS MANAGER";

    public TTSManager(Context context) {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.getDefault());

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts.setLanguage(Locale.US);
                    }
                }
            }
        });
    }

    public void speakOut(String text) {
        try {
            if (tts.isLanguageAvailable(Locale.getDefault()) == TextToSpeech.LANG_AVAILABLE) {
                tts.setLanguage(Locale.getDefault());
            } else if (tts.isLanguageAvailable(Locale.getDefault()) == TextToSpeech.LANG_MISSING_DATA
                    || tts.isLanguageAvailable(Locale.getDefault()) == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts.setLanguage(Locale.US);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(Mapa.getInstancia().tocarAudio && !tts.isSpeaking()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            Mapa.getInstancia().tocarAudio = false;
        }
    }

    public void stopTalking(){
        if(tts.isSpeaking()){
            tts.stop();
        }
    }
}