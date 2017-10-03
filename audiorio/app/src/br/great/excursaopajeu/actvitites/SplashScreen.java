package br.great.excursaopajeu.actvitites;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;

import br.great.excursaopajeu.util.SobrescreverFonte;
import br.ufc.great.arviewer.pajeu.R;

public class SplashScreen extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //SobrescreverFonte.setDefaultFont(this, "SANS_SERIF", "fonts/GeosansLight.ttf");
        SobrescreverFonte.setDefaultFont(this, "SANS_SERIF", "fonts/Roboto-Regular.ttf");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * Isso só esta aqui pra animação aparecer por um tempo*/
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep( 4 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                startActivity(new Intent(SplashScreen.this, IniciarCaminhadaActivity.class));
                finish();
            }
        }.execute();
    }
}
