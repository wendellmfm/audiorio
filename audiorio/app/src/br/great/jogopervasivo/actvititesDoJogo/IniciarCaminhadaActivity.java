package br.great.jogopervasivo.actvititesDoJogo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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

import br.great.jogopervasivo.GPS.GPSListener;
import br.great.jogopervasivo.util.EfeitoClique;
import br.great.jogopervasivo.util.GPSListenerManager;
import br.ufc.great.arviewer.pajeu.R;

public class IniciarCaminhadaActivity extends Activity {
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private GPSListener gpsListener;
    private static IniciarCaminhadaActivity instancia;

    private RelativeLayout botaoCaminhadaBode;

    public static IniciarCaminhadaActivity getInstancia(){
        return instancia;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_caminhadas);
        iniciarComponentes();
        instancia = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        verificarPlayServices();
        iniciarListenerDeGPS();
        pedirPermissoes();
    }

    private void pedirPermissoes() {
        // Se não possui permissão
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // Verifica se já mostramos o alerta e o usuário negou na 1ª vez.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                // Caso o usuário tenha negado a permissão anteriormente, e não tenha marcado o check "nunca mais mostre este alerta"
                // Podemos mostrar um alerta explicando para o usuário porque a permissão é importante.
                //Alerta.mandarAlerta(this, getString(R.string.falta_permissao_location));
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);

            } else {
                // Solicita a permissão
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
            }
        }

        // Se não possui permissão
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Verifica se já mostramos o alerta e o usuário negou na 1ª vez.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Caso o usuário tenha negado a permissão anteriormente, e não tenha marcado o check "nunca mais mostre este alerta"
                // Podemos mostrar um alerta explicando para o usuário porque a permissão é importante.
                //Alerta.mandarAlerta(this, getString(R.string.falta_permissao_location));
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

            } else {
                // Solicita a permissão
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }

        // Se não possui permissão
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED ) {
            // Verifica se já mostramos o alerta e o usuário negou na 1ª vez.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE) ) {
                // Caso o usuário tenha negado a permissão anteriormente, e não tenha marcado o check "nunca mais mostre este alerta"
                // Podemos mostrar um alerta explicando para o usuário porque a permissão é importante.
                //Alerta.mandarAlerta(this, getString(R.string.falta_permissao_location));
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 0);

            } else {
                // Solicita a permissão
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 0);
            }
        }
    }

    private void iniciarListenerDeGPS() {

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
        } else {
            gpsListener = GPSListenerManager.getGpsListener(this);
        }

    }

    private void iniciarComponentes() {
        //Inicializacao
        botaoCaminhadaBode = (RelativeLayout) findViewById(R.id.menu_iniciar_excursao);
        botaoCaminhadaBode.setOnTouchListener(new EfeitoClique(this));
        botaoCaminhadaBode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(IniciarCaminhadaActivity.this, Mapa.class));
                finish();

//                new AsyncTask<Void, Void, Boolean>() {
//
//                    ProgressDialog progressDialog;
//
//
//                    @Override
//                    protected void onPreExecute() {
//                        progressDialog = new ProgressDialog(IniciarCaminhadaActivity.this);
//                        progressDialog.setMessage(getString(R.string.obtendo_informacoes));
//                        progressDialog.show();
//                    }
//
//                    @Override
//                    protected Boolean doInBackground(Void... params) {
//
//                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//                        String deviceId = telephonyManager.getDeviceId();
//
//                        //primeiro Cria uma instancia de jogo novo
//                        CriarNovaInstanciaDeJogo criarNovaInstanciaDeJogo = new CriarNovaInstanciaDeJogo(IniciarCaminhadaActivity.this);
//                        criarNovaInstanciaDeJogo.criar(Integer.toString(Jogo.CAMINHADA_BODE), deviceId);
//
//                        //Recupera as instancias ja criadas
//                        RecuperarInstanciasDeJogos recuperarInstanciasDeJogos = new RecuperarInstanciasDeJogos(IniciarCaminhadaActivity.this);
//                        List<InstanciaDeJogo> instanciaDeJogoList = recuperarInstanciasDeJogos.recuperar(Jogo.CAMINHADA_BODE, InformacoesTemporarias.idJogador);
//                        for (InstanciaDeJogo i : instanciaDeJogoList) {
//
//                            //Recupera instancia com o  mesmo Device ID
//                            if (i.getNomeFicticio().equals(deviceId)) {
//                                EscolherEquipe escolherEquipe = new EscolherEquipe(IniciarCaminhadaActivity.this);
//                                List<Grupo> grupos = escolherEquipe.recuperarGrupos(i.getId());
//                                InformacoesTemporarias.instanciaDeJogo = i;
//                                InformacoesTemporarias.grupo = grupos.get(0);
//                                Location localizacao = Armazenamento.resgatarUltimaLocalizacao(IniciarCaminhadaActivity.this);
//                                InicializarJogo inicializarJogo = new InicializarJogo(IniciarCaminhadaActivity.this, InformacoesTemporarias.instanciaDeJogo, InformacoesTemporarias.grupo, localizacao.getLatitude(), localizacao.getLongitude());
//                                inicializarJogo.inicializar();
//                                InformacoesTemporarias.jogoAtual = i;
//                                InformacoesTemporarias.grupoAtual = grupos.get(0);
//                                Armazenamento.salvar(Constantes.JOGO_EXECUTANDO, true, IniciarCaminhadaActivity.this);//Diz que tem jogo executando;
//                                RecuperarObjetosInventario.recuperar(IniciarCaminhadaActivity.this);
//                                startActivity(new Intent(IniciarCaminhadaActivity.this, Mapa.class));
//                                finish(); //Faz casting do Context para activity  e fecha a janela.
//                                return true;
//                            }
//                        }
//
//                        return false;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Boolean aBoolean) {
//                        progressDialog.dismiss();
//                    }
//                }.execute();

            }
        });

    }

    private void mostrarAlerta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.em_construcao);
        builder.setNegativeButton(R.string.OK, null);
        builder.create().show();
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
        gpsListener.pararListener();
        super.onDestroy();

    }
}