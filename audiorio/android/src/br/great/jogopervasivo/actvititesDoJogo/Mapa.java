package br.great.jogopervasivo.actvititesDoJogo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.List;

import br.great.jogopervasivo.beans.Ponto;
import br.great.jogopervasivo.util.Armazenamento;
import br.great.jogopervasivo.util.Fontes;
import br.great.jogopervasivo.util.GPSListenerManager;
import br.great.jogopervasivo.util.TTSManager;
import br.ufc.great.arviewer.pajeu.R;


public class Mapa extends Activity {
    public static final int LIMIAR_DE_PROXIMIDADE = 20;

    public static final String TITLE_KEY = "title";
    public static final String INFO_KEY = "info";

    private GoogleMap mapa;
    private Marker marcadorJogador = null;
    private MarkerOptions markerOptions;
    private Circle circle;
    private Location previousLocation = null;
    private List<Marker> listMarcadores = new ArrayList<>();

    private String[] titles;
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    private Polyline polylineToFirstPoint;
    private PolylineOptions polylineOptions;

    public static TTSManager ttsManager;

    public boolean tocarAudio = true;

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

        for (Marker marker : listMarcadores) {
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), marker.getPosition().latitude,
                    marker.getPosition().longitude, distance);
            if (distance[0] < LIMIAR_DE_PROXIMIDADE) {
                showPolyLine= false;
                removePolyLineToFirstPoint();
                onMarkerProximity(marker);
                return;
            }
        }

        tocarAudio = true;
    }

    private void verificarProximidadeDoMarcador(Marker marcadorJogador) {
        float[] distance = new float[2];

        for (Marker marker : listMarcadores) {
            Location.distanceBetween(marcadorJogador.getPosition().latitude, marcadorJogador.getPosition().longitude, marker.getPosition().latitude,
                            marker.getPosition().longitude, distance);
            if (distance[0] < LIMIAR_DE_PROXIMIDADE) {
                showPolyLine= false;
                removePolyLineToFirstPoint();
                return;
            }
        }
    }

    private void onMarkerProximity(Marker marker){
        //ttsManager.speakOut(((Vtextos) mecanica).getTexto());
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_audio_ouvido));
        Toast.makeText(getApplicationContext(), marker.getTitle(), Toast.LENGTH_LONG).show();

        ttsManager.speakOut(marker.getTitle());
    }

    private Marker verificarMarcadorMaisProximo(Marker marcadorJogador) {
        //List<Marker> markers = new ArrayList<>(listMarcadores.values());
        float[] distance = new float[2];
        float resultado = Float.MAX_VALUE;
        Marker marcadorProximo = null;

        for (int i = 0; i < listMarcadores.size(); i++) {
                Location.distanceBetween(marcadorJogador.getPosition().latitude, marcadorJogador.getPosition().longitude, listMarcadores.get(i).getPosition().latitude,
                        listMarcadores.get(i).getPosition().longitude, distance);
                if (distance[0] < resultado) {
                    resultado = distance[0];
                    marcadorProximo = listMarcadores.get(i);
                }
        }

        return marcadorProximo;
    }

    private void adicionarMarcadorJogador(Location location) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

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

        mostrarPontos();
    }

    private void updateMarkerWithCircle(LatLng position) {
        circle.setCenter(position);
    }

    private void drawMarkerWithCircle(LatLng position) {
        CircleOptions circleOptions = new CircleOptions().center(position)
                .radius(LIMIAR_DE_PROXIMIDADE)
                .strokeColor(getResources().getColor(R.color.gray_circle))
                .strokeWidth(5);
        circle = mapa.addCircle(circleOptions);
    }

//    public void transicaoMarcador(final String nome) {
//        final float[] markerAlpha = new float[1];
//
//        for (int i = 0; i < 5; i++) {
//            new Handler(getMainLooper()).
//                    post(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            final Marker marcador = listMarcadores.get(nome);
//                            if (marcador != null) {
//                                Location location = new Location(LocationManager.GPS_PROVIDER);
//                                location.setLatitude(marcador.getPosition().latitude);
//                                location.setLongitude(marcador.getPosition().longitude);
//                                moverCamera(location);
//                                markerAlpha[0] = marcador.getAlpha();
//                                marcador.setAlpha(marcador.getAlpha() / 1.5f);
//                            }
//
//                        }
//                    });
//
//            try {
//                Thread.sleep(250);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }


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
     * Adiciona os marcadores
     */
    public void mostrarPontos() {
        adicionarMarcador(new Ponto("Teste", new LatLng(-4.9795009, -39.0584624)));

        adicionarMarcador(new Ponto("Teste 2", new LatLng(-4.978674, -39.056564)));
        adicionarMarcador(new Ponto("Teste 3", new LatLng(-4.978674, -39.057096)));

        verificarProximidadeDoMarcador(marcadorJogador);

        if(showPolyLine) {
            removePolyLineToFirstPoint();
            addPolyLineToFirstPoint(marcadorJogador.getPosition(), verificarMarcadorMaisProximo(marcadorJogador));
        }
    }

    private void adicionarMarcador(Ponto ponto) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(ponto.getNome());
            markerOptions.position(ponto.getLocalizacao());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_audio_nao_ouvido));

            Marker marker = mapa.addMarker(markerOptions);
            listMarcadores.add(marker);
            //Marker marker = mapa.addMarker(markerOptions);
            //listMarcadores.put(ponto.getNome(), marker);
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
}