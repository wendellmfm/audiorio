package br.great.jogopervasivo.actvititesDoJogo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.great.jogopervasivo.beans.Jogador;
import br.great.jogopervasivo.beans.Mecanica;
import br.great.jogopervasivo.beans.mecanicas.CFotos;
import br.great.jogopervasivo.beans.mecanicas.CSons;
import br.great.jogopervasivo.beans.mecanicas.CTextos;
import br.great.jogopervasivo.beans.mecanicas.CVideos;
import br.great.jogopervasivo.beans.mecanicas.Deixar;
import br.great.jogopervasivo.beans.mecanicas.IrLocais;
import br.great.jogopervasivo.beans.mecanicas.VObj3d;
import br.great.jogopervasivo.beans.mecanicas.VSons;
import br.great.jogopervasivo.beans.mecanicas.VVideos;
import br.great.jogopervasivo.beans.mecanicas.Vfotos;
import br.great.jogopervasivo.beans.mecanicas.Vtextos;
import br.great.jogopervasivo.util.Armazenamento;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.Fontes;
import br.great.jogopervasivo.util.GPSListenerManager;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.great.jogopervasivo.util.MetodosUteis;
import br.great.jogopervasivo.util.TTSManager;
import br.great.jogopervasivo.webServices.SolicitarMissaoAtual;
import br.great.jogopervasivo.webServices.UploadDeArquivo;
import br.ufc.great.arviewer.pajeu.R;


public class Mapa extends Activity {

    //Constantes locais
    public static final int MARCADOR_LOCAL = 0;
    public static final int MARCADOR_MECANICA_REALIZADA = 4;
    public static final int MARCADOR_MECANICA_BLOQUEADA = 5;
    public static final int MARCADOR_ALIADO = 1;
    public static final int MARCADOR_ADVERSARIO = 2;
    public static final int REQUEST_CODE_FOTO = 3;
    public static final int REQUEST_CODE_VIDEO = 200;
    public static final int REQUEST_CODE_VER_VIDEO = 201;
    public static final int REQUEST_CODE_VER_OBJ_3D = 202;

    public static final String TITLE_KEY = "title";
    public static final String INFO_KEY = "info";

    private GoogleMap mapa;
    private Marker marcadorJogador = null;
    private MarkerOptions markerOptions;
    private Circle circle;
    private Location previousLocation = null;
    private Map<String, Marker> hashMarcadores = new HashMap<>();
    private boolean pediuMecanicas = false;

    private String[] titles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    private Polyline polylineToFirstPoint;
    private PolylineOptions polylineOptions;

    public static TTSManager ttsManager;

    public boolean tocarAudio = true;

    CFotos mecanicaCFotoAtual = null;
    public static CVideos mecanicaCVideosAtual = null;
    public static Deixar mecanicaDeixarAtual = null;
    public static VVideos mecanicaVVideosAtual = null;
    public static VObj3d mecanicaVObj3dAtual = null;

    private static Mapa instancia;
    private boolean showPolyLine = true;

    public static Mapa getInstancia() {
        return instancia;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_externo);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        titles = new String[]{getResources().getString(R.string.titulo_como_usar),
                getResources().getString(R.string.titulo_sobre_o_aplicativo),
                getResources().getString(R.string.titulo_faq),
                getResources().getString(R.string.titulo_creditos)};
        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, titles));

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Mapa.this, AboutActivity.class);
                String title = "";
                String info = "";
                switch (position) {
                    case 0:
                        title = getResources().getString(R.string.titulo_como_usar);
                        info = getResources().getString(R.string.info_como_usar);
                        break;
                    case 1:
                        title = getResources().getString(R.string.titulo_sobre_o_aplicativo);
                        info = getResources().getString(R.string.info_sobre_o_aplicativo);
                        break;
                    case 2:
                        title = getResources().getString(R.string.titulo_faq);
                        info = getResources().getString(R.string.info_faq);
                        break;
                    case 3:
                        title = getResources().getString(R.string.titulo_creditos);
                        info = getResources().getString(R.string.info_creditos);
                        break;
                }

                intent.putExtra(TITLE_KEY, title);
                intent.putExtra(INFO_KEY, info);
                startActivity(intent);
            }
        });

        ttsManager = new TTSManager(Mapa.this);

        GPSListenerManager.getGpsListener(this).setMapa(this);

        instancia = this;
    }

    public void novaLocalizacao(Location location) {
        adicionarMarcadorJogador(location);
        verificarProximidadeDoMarcador(location);
    }

    private void verificarProximidadeDoMarcador(Location location) {
        float[] distance = new float[2];

        for (Map.Entry<String, Marker> marker : hashMarcadores.entrySet()) {
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), marker.getValue().getPosition().latitude,
                    marker.getValue().getPosition().longitude, distance);
            if (distance[0] < Constantes.LIMIAR_DE_PROXIMIDADE) {
                showPolyLine= false;
                removePolyLineToFirstPoint();
                onMarkerProximity(marker.getValue());
                return;
            }
        }

        tocarAudio = true;
    }

    private void verificarProximidadeDoMarcador(Marker marcadorJogador) {
        float[] distance = new float[2];

        for (Map.Entry<String, Marker> marker : hashMarcadores.entrySet()) {
            Location.distanceBetween(marcadorJogador.getPosition().latitude, marcadorJogador.getPosition().longitude, marker.getValue().getPosition().latitude,
                            marker.getValue().getPosition().longitude, distance);
            if (distance[0] < Constantes.LIMIAR_DE_PROXIMIDADE) {
                showPolyLine= false;
                removePolyLineToFirstPoint();
                return;
            }
        }
    }

    private void onMarkerProximity(Marker marker){
        String nome = marker.getTitle();
        Mecanica mecanica;
        mecanica = Mecanica.getMecanica(nome);

        try {
            new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(InformacoesTemporarias.grupoAtual);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mecanica == null && InformacoesTemporarias.grupoAtual != null && ((InformacoesTemporarias.grupoAtual.getTipoJogador() == Jogador.TIPO_CAPTURADOR) || (InformacoesTemporarias.grupoAtual.getTipoJogador() == Jogador.TIPO_HIBRIDO))) {
            Jogador jogador = InformacoesTemporarias.getJogador(nome);
            if (jogador != null) {
                Location localizacaoJogador = MetodosUteis.latLngToLocation(LocationManager.GPS_PROVIDER, jogador.getPosicao());
                if (Armazenamento.resgatarUltimaLocalizacao(Mapa.this).distanceTo(localizacaoJogador) < Constantes.LIMIAR_DE_PROXIMIDADE) {
                    if (jogador.getGrupo().getTipoJogador() == Jogador.TIPO_CAPTURAVEL || jogador.getGrupo().getTipoJogador() == Jogador.TIPO_HIBRIDO) {
                        jogador.capturar(Mapa.this);
                    } else {
                        //Log.e(Constantes.TAG, "Jogador clicado não é capturavel");
                        Toast.makeText(getApplicationContext(), R.string.nao_pode_capturar_jogador, Toast.LENGTH_LONG).show();
                    }
                }
            }

        }
        realizarMecanica(mecanica);
    }

    private Marker verificarMarcadorMaisProximo(Marker marcadorJogador) {
        List<Marker> markers = new ArrayList<>(hashMarcadores.values());
        float[] distance = new float[2];
        float resultado = Float.MAX_VALUE;
        Marker marcadorProximo = null;

        for (int i = 0; i < markers.size(); i++) {
                Location.distanceBetween(marcadorJogador.getPosition().latitude, marcadorJogador.getPosition().longitude, markers.get(i).getPosition().latitude,
                        markers.get(i).getPosition().longitude, distance);
                if (distance[0] < resultado) {
                    resultado = distance[0];
                    marcadorProximo = markers.get(i);
                }
        }

        return marcadorProximo;
    }

    //Adicinar marcador do jogador
    private void adicionarMarcadorJogador(Location location) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

        //Se o marcador do jogador ainda não foi colocado
        if (marcadorJogador == null) {
            if (markerOptions == null) {
                markerOptions = new MarkerOptions();
            }
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_jogador))
                    .title(getString(R.string.eu))
                    .anchor(0.5f, 0.5f)
                    .position(new LatLng(location.getLatitude(), location.getLongitude()));

            marcadorJogador = mapa.addMarker(markerOptions);
            drawMarkerWithCircle(position);
            updateMarkerWithCircle(position);
            moverCamera(location);
        } else {
            //Se o marcador já existe, ele apenas atualiza a posição
            marcadorJogador.setPosition(position);
            updateMarkerWithCircle(position);

            if (previousLocation != null) {
                float bearing = previousLocation.bearingTo(location);
                if (bearing != 0) {
                    marcadorJogador.setRotation(bearing);
                }
            }
            previousLocation = location;
        }

        mostrarMecanicas();
    }

    private void updateMarkerWithCircle(LatLng position) {
        circle.setCenter(position);
    }

    private void drawMarkerWithCircle(LatLng position) {
        CircleOptions circleOptions = new CircleOptions().center(position)
                .radius(Constantes.LIMIAR_DE_PROXIMIDADE)
                .strokeColor(getResources().getColor(R.color.gray_circle))
                .strokeWidth(5);
        circle = mapa.addCircle(circleOptions);
    }

    public void transicaoMarcador(final String nome) {
        final float[] markerAlpha = new float[1];

        for (int i = 0; i < 5; i++) {
            new Handler(getMainLooper()).
                    post(new Runnable() {
                        @Override
                        public void run() {

                            final Marker marcador = hashMarcadores.get(nome);
                            if (marcador != null) {
                                Location location = new Location(LocationManager.GPS_PROVIDER);
                                location.setLatitude(marcador.getPosition().latitude);
                                location.setLongitude(marcador.getPosition().longitude);
                                moverCamera(location);
                                markerAlpha[0] = marcador.getAlpha();
                                marcador.setAlpha(marcador.getAlpha() / 1.5f);
                            }

                        }
                    });

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //SolicitarMissaoAtual solicitarMissaoAtual = new SolicitarMissaoAtual(InformacoesTemporarias.contextoTelaPrincipal);
        //solicitarMissaoAtual.execute();
    }


    //Mover camera
    private void moverCamera(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(20)
                .bearing(mapa.getCameraPosition().bearing)
                .build();
        mapa.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    protected void onPause() {
        super.onPause();

        ttsManager.stopTalking();
    }

    @Override
    protected void onResume() {
        super.onResume();
        configurarMapa();

        Location l = Armazenamento.resgatarUltimaLocalizacao(this);
        if (l != null) {
            novaLocalizacao(l);
        }

        //Elimina erros quando re-compilo o APK antes de fechar o antigo
        if (Armazenamento.resgatarBoolean(Constantes.JOGO_EXECUTANDO, this)) {
            if (InformacoesTemporarias.jogoAtual == null) {
                Armazenamento.salvar(Constantes.JOGO_EXECUTANDO, false, this);
                invalidateOptionsMenu();
            } else {
                if (!pediuMecanicas) {
                    Toast.makeText(getApplicationContext(), R.string.caminhada_iniciada, Toast.LENGTH_SHORT).show();
                    SolicitarMissaoAtual solicitarMissaoAtual = new SolicitarMissaoAtual(this);
                    solicitarMissaoAtual.execute();
                    pediuMecanicas = true;
                }
            }
        }
    }

    private void configurarMapa() {
        //Criando objetos de manipulação dos mapas
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapa = mapFragment.getMap();

        //Tipo de visualização dos mapas
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapa.setBuildingsEnabled(true);

        //Atualiza a posição da camera do mapa
        CameraUpdate camera = CameraUpdateFactory.zoomTo(25);
        mapa.moveCamera(camera);

        mapa.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                moverCamera(Armazenamento.resgatarUltimaLocalizacao(Mapa.this));
            }
        });

        mapa.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                Context context = Mapa.this;

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());
                title.setTextSize(14);

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());
                snippet.setTextSize(14);

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        mapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.setSnippet(Fontes.getFonte(marker.getTitle().trim()));

                return false;
            }
        });

        mapa.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                goToFonteURL(Fontes.getFonteURL(marker.getTitle().trim()));
            }
        });
    }

    private void goToFonteURL(String url){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);

            return;
        }
        ttsManager.stopTalking();
        finish();
        super.onBackPressed();
    }

    public void toggleDrawer(View view) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    /**
     * Verifica de alguma mensagem foi enviada ao jogador via GCM
     */
    public void verificarMensagem() {
        if (InformacoesTemporarias.mensagem != null) {
            Handler h = new Handler(getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Mapa.this);
                    builder.setTitle(R.string.app_name)
                            .setMessage(InformacoesTemporarias.mensagem)
                            .setNeutralButton(R.string.OK, null)
                            .create().show();
                    InformacoesTemporarias.mensagem = null;
                }
            });

        }
    }

    private void realizarMecanica(Mecanica mecanica) {
        if (mecanica != null && !(mecanica.isRealizada())) {
            Location localizacaoJogador = Armazenamento.resgatarUltimaLocalizacao(this);
            Location localizacaoMecanica = new Location(LocationManager.GPS_PROVIDER);
            localizacaoMecanica.setLatitude(mecanica.getLocalizacao().latitude);
            localizacaoMecanica.setLongitude(mecanica.getLocalizacao().longitude);

            float distancia = localizacaoMecanica.distanceTo(localizacaoJogador);

            if (distancia < Constantes.LIMIAR_DE_PROXIMIDADE) {
                String tipoMecanica = mecanica.getTipoSimples();

                switch (tipoMecanica) {
                    case Constantes.TIPO_MECANICA_CVIDEOS:
                        mecanicaCVideosAtual = (CVideos) mecanica;
                        ((CVideos) mecanica).realizarMecanica(this);
                        break;
                    case Constantes.TIPO_MECANICA_VFOTOS:
                        ((Vfotos) mecanica).realizarMecanica(this);
                        break;
                    case Constantes.TIPO_MECANICA_VTEXTOS:
                        ((Vtextos) mecanica).realizarMecanica(this);
                        break;
                    case Constantes.TIPO_MECANICA_CFOTOS:
                        mecanicaCFotoAtual = (CFotos) mecanica;
                        ((CFotos) mecanica).realizarMecanica(this);
                        break;
                    case Constantes.TIPO_MECANICA_IRLOCAIS:
                        ((IrLocais) mecanica).realizarMecanica(this);
                        break;
                    case Constantes.TIPO_MECANICA_CSONS:
                        ((CSons) mecanica).realizarMecanica(this);
                        break;
                    case Constantes.TIPO_MECANICA_VSONS:
                        ((VSons) mecanica).realizarMecanica(this);
                        break;
                    case Constantes.TIPO_MECANICA_CTEXTOS:
                        ((CTextos) mecanica).realizarMecanica(this);
                        break;
                    case Constantes.TIPO_MECANICA_VVIDEOS:
                        ((VVideos) mecanica).realizarMecanica(this);
                        break;
                    case Constantes.TIPO_MECANICA_DFOTOS:
                        ((Deixar) mecanica).realizarMecanica(this);
                        break;
                    case Constantes.TIPO_MECANICA_DOBJETOS3D:
                        ((Deixar) mecanica).realizarMecanica(this);
                        break;
                    case Constantes.TIPO_MECANICA_DSONS:
                        ((Deixar) mecanica).realizarMecanica(this);
                        break;
                    case Constantes.TIPO_MECANICA_DTEXTOS:
                        ((Deixar) mecanica).realizarMecanica(this);
                        break;
                    case Constantes.TIPO_MECANICA_DVIDEOS:
                        ((Deixar) mecanica).realizarMecanica(this);
                        break;
                    case Constantes.TIPO_MECANICA_V_OBJ_3D:
                        ((VObj3d) mecanica).realizarMecanica(this);
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Tipo da mecanica ainda náo implementada: " + mecanica.getTipoSimples(), Toast.LENGTH_LONG).show();
                }


            } else {

            }
        }else{
            ttsManager.speakOut(((Vtextos) mecanica).getTexto());
        }
    }

    /**
     * Adiciona os marcadores das mecanicas
     */
    public void mostrarMecanicas() {
        if (InformacoesTemporarias.mecanicasAtuais != null) {
            List<Mecanica> mecanicasAtuais = InformacoesTemporarias.mecanicasAtuais;
            for (Mecanica m : mecanicasAtuais) {
                Marker marker = hashMarcadores.get(m.getNome());
                if (marker != null) {
                    marker.remove();
                }
                if (m.isVisivel() && m.isMostrar()) {
                    if (m.isRealizada()) {
                        try {
                            hashMarcadores.get(m.getNome()).remove();
                            hashMarcadores.remove(m.getNome());
                        } catch (Exception e) {

                        }
                        adicionarMarcador(m.getNome(), m.getLocalizacao(), MARCADOR_MECANICA_REALIZADA, null);
                    } else if (m.getEstado() == 2) {
                        try {
                            hashMarcadores.get(m.getNome()).remove();
                            hashMarcadores.remove(m.getNome());
                        } catch (Exception e) {

                        }
                        adicionarMarcador(m.getNome(), m.getLocalizacao(), MARCADOR_MECANICA_BLOQUEADA, m.getMsgbloqueio());
                    } else if ((m.getEstado() == 0) || (m.getEstado() == 1)) {
                        try {
                            hashMarcadores.get(m.getNome()).remove();
                            hashMarcadores.remove(m.getNome());
                        } catch (Exception e) {

                        }
                        adicionarMarcador(m.getNome(), m.getLocalizacao(), MARCADOR_LOCAL, null);
                    }
                }
            }
        }

        verificarProximidadeDoMarcador(marcadorJogador);

        if(showPolyLine) {
            removePolyLineToFirstPoint();
            addPolyLineToFirstPoint(marcadorJogador.getPosition(), verificarMarcadorMaisProximo(marcadorJogador));
        }
    }


    /**
     * Adiciona um marcador de qualquer tipo no mapa
     *
     * @param nome    palavra que identifica o marcador
     * @param posicao localização geografica do marcador
     * @param tipo    constante que define o tipo do marcador
     */
    private void adicionarMarcador(String nome, LatLng posicao, int tipo, String mensagemBloqueio) {
        if (hashMarcadores.containsKey(nome)) {
            hashMarcadores.get(nome).setPosition(posicao);
        } else {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(nome);
            markerOptions.position(posicao);
            switch (tipo) {
                case MARCADOR_LOCAL:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_audio_nao_ouvido));
                    break;
                case MARCADOR_ADVERSARIO:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_jogador_rosa));
                    if (mensagemBloqueio.equals("0")) {
                        markerOptions.alpha(markerOptions.getAlpha() / 2);
                    }
                    break;
                case MARCADOR_ALIADO:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_jogador));
                    break;
                case MARCADOR_MECANICA_REALIZADA:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_audio_ouvido));
                    break;
                case MARCADOR_MECANICA_BLOQUEADA:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marcador_irlocais_rosa));
                    markerOptions.snippet(mensagemBloqueio);
                    break;
            }
            Marker marker = mapa.addMarker(markerOptions);
            hashMarcadores.put(nome, marker);
        }
    }

    private void addPolyLineToFirstPoint(LatLng userPosition, Marker firstPointMarker) {
        if(firstPointMarker == null){
            return;
        }

        polylineOptions = new PolylineOptions().add(new LatLng(userPosition.latitude, userPosition.longitude), firstPointMarker.getPosition())
                .width(5).color(Color.CYAN);

        polylineToFirstPoint = mapa.addPolyline(polylineOptions);
    }

    private void removePolyLineToFirstPoint(){
        if(polylineToFirstPoint != null){
            polylineToFirstPoint.remove();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQUEST_CODE_FOTO) {
                File foto = new File(CFotos.pathDeImagem);
                UploadDeArquivo.enviarFoto(this, foto, mecanicaCFotoAtual);
                mecanicaCFotoAtual = null;
            } else if (requestCode == REQUEST_CODE_VIDEO) {
                Uri fileUri = data.getData();
                File file = new File(pegarPahRealDeURI(fileUri));
                UploadDeArquivo.enviarVideo(this, file, mecanicaCVideosAtual);
                mecanicaCVideosAtual = null;
            } else if (requestCode == Deixar.REQUEST_CODE) {
                Bundle extras = data.getExtras();
                int mecSimplesId = extras.getInt(InventarioActivity.ITEM_ID);
                String tipo = extras.getString(InventarioActivity.ITEM_TIPO);
                String arquivo = extras.getString(InventarioActivity.ITEM_ARQUIVO);
                mecanicaDeixarAtual.confirmarRealizacao(this, arquivo, tipo, mecSimplesId);
                mecanicaDeixarAtual = null;
            } else if (requestCode == REQUEST_CODE_VER_OBJ_3D) {
                mecanicaVObj3dAtual.confirmarRealizacao(this, null, null, null);
                mecanicaVObj3dAtual = null;
            }
        } else if (requestCode == Vfotos.REQUEST_CODE_VER_IMAGEM) {
            Toast.makeText(this, "Viu a imagem", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_CODE_VER_VIDEO) {
            mecanicaVVideosAtual.confirmarRealizacao(this, null, null, null);
            mecanicaVVideosAtual = null;
        }
    }

    public String pegarPahRealDeURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
        return cursor.getString(idx);
    }
}
