package br.great.excursaopajeu.actvititesDoJogo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import br.ufc.great.arviewer.pajeu.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
        TextView infoTextView = (TextView) findViewById(R.id.infoTextView);

        titleTextView.setText(getIntent().getStringExtra(Mapa.TITLE_KEY));

        if(titleTextView.getText().equals(getResources().getString(R.string.titulo_creditos))){
            infoTextView.setText(Html.fromHtml(getIntent().getStringExtra(Mapa.INFO_KEY)));
        }else{
            infoTextView.setText(getIntent().getStringExtra(Mapa.INFO_KEY));
        }

        if(titleTextView.getText().equals(getResources().getString(R.string.titulo_sobre_o_aplicativo))){
            findViewById(R.id.siteTextView).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.siteTextView).setVisibility(View.INVISIBLE);
        }
    }
}
