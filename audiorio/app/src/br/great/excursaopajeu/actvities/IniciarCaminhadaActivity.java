package br.great.excursaopajeu.actvities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import br.great.excursaopajeu.util.EfeitoClique;
import br.ufc.great.arviewer.pajeu.R;

public class IniciarCaminhadaActivity extends Activity {
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static IniciarCaminhadaActivity instancia;

    private RelativeLayout botaoIniciarExcursao;

    public static IniciarCaminhadaActivity getInstancia(){
        return instancia;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_caminhadas);
        iniciarComponentes();
        instancia = this;

        // Se não possui permissão
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Verifica se já mostramos o alerta e o usuário negou na 1ª vez.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Caso o usuário tenha negado a permissão anteriormente, e não tenha marcado o check "nunca mais mostre este alerta"
                // Podemos mostrar um alerta explicando para o usuário porque a permissão é importante.
                //Alerta.mandarAlerta(this, getString(R.string.falta_permissao_location));
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);

            } else {
                // Solicita a permissão
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        verificarPlayServices();
    }

    private void iniciarComponentes() {
        botaoIniciarExcursao = (RelativeLayout) findViewById(R.id.menu_iniciar_excursao);
        botaoIniciarExcursao.setOnTouchListener(new EfeitoClique(this));
        botaoIniciarExcursao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(IniciarCaminhadaActivity.this, Mapa.class));
                finish();
            }
        });

    }

    private boolean verificarPlayServices() {
        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST);
                dialog.show();
            } else {
                Toast.makeText(getApplicationContext(), "Play services sem suporte", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}