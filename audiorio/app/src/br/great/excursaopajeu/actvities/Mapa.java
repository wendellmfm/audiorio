package br.great.excursaopajeu.actvities;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.great.excursaopajeu.beans.Ponto;
import br.great.excursaopajeu.util.Armazenamento;
import br.great.excursaopajeu.util.Fontes;
import br.great.excursaopajeu.util.TTSManager;
import br.great.excursaopajeu.util.Textos;
import br.ufc.great.arviewer.pajeu.R;


public class Mapa extends Activity implements LocationListener {
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
    private boolean beginMarkerTransition = true;

    private static Mapa instancia;

    private boolean showPolyLine = true;
    private boolean pontosAdicionados = false;
    private boolean marcadorJogadorAdicionado = false;
    private boolean firstLocation = true;
    private LocationManager locationManager;
    private ProgressDialog progressDialog;

    private static final int CAMERA_REQUEST = 1888;
    private File file;

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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 5, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.ativando_gps));
        progressDialog.setCancelable(false);
        if (Armazenamento.resgatarUltimaLocalizacao(this) == null) {
            progressDialog.show();
        }

        instancia = this;
    }

    public void novaLocalizacao(Location location) {
        adicionarMarcadorJogador(location);
        verificarProximidadeDoMarcador(location);
    }

    private void verificarProximidadeDoMarcador(Location location) {
        if(marcadorJogadorAdicionado && pontosAdicionados){
            mostrarPontoMaisProximo();
        }

        float[] distance = new float[2];

        for (Marker marker : listMarcadores) {
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), marker.getPosition().latitude,
                    marker.getPosition().longitude, distance);
            if (distance[0] < LIMIAR_DE_PROXIMIDADE) {
                showPolyLine = false;
                removePolyLineToFirstPoint();
                onMarkerProximity(marker);
                return;
            }
        }

        tocarAudio = true;
        beginMarkerTransition = true;
    }

    private void verificarProximidadeDoMarcador(Marker marcadorJogador) {
        float[] distance = new float[2];

        for (Marker marker : listMarcadores) {
            Location.distanceBetween(marcadorJogador.getPosition().latitude, marcadorJogador.getPosition().longitude, marker.getPosition().latitude,
                    marker.getPosition().longitude, distance);
            if (distance[0] < LIMIAR_DE_PROXIMIDADE) {
                showPolyLine = false;
                removePolyLineToFirstPoint();
                return;
            }
        }
    }

    private void onMarkerProximity(final Marker marker) {
        if (beginMarkerTransition && !ttsManager.isSpeaking()) {
            transicaoMarcador(marker);
            beginMarkerTransition = false;
        }

        ttsManager.speakOut(Textos.getTexto(marker.getTitle()));
    }

    private void transicaoMarcador(final Marker marker) {
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_audio_ouvido));
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1); //change for (0,1) if you want a fade in
        valueAnimator.setDuration(3000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                marker.setAlpha((float) animation.getAnimatedValue());
            }
        });
        valueAnimator.start();
    }

    private Marker verificarMarcadorMaisProximo(Marker marcadorJogador) {
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
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_jogador))
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

        if(!marcadorJogadorAdicionado) {
            marcadorJogadorAdicionado = true;
        }

        if (showPolyLine) {
            removePolyLineToFirstPoint();
            addPolyLineToFirstPoint(marcadorJogador.getPosition(), verificarMarcadorMaisProximo(marcadorJogador));
        }
    }

    private void updateMarkerWithCircle(LatLng position) {
        circle.setCenter(position);
    }

    private void drawMarkerWithCircle(LatLng position) {
        CircleOptions circleOptions = new CircleOptions().center(position)
                .radius(LIMIAR_DE_PROXIMIDADE)
                .strokeColor(ContextCompat.getColor(Mapa.this, R.color.gray_circle))
                .strokeWidth(5);
        circle = mapa.addCircle(circleOptions);
    }

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

        Location location = Armazenamento.resgatarUltimaLocalizacao(this);
        if (location != null) {
            novaLocalizacao(location);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
    }

    private void configurarMapa() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapa = mapFragment.getMap();

        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapa.setBuildingsEnabled(true);

        CameraUpdate camera = CameraUpdateFactory.zoomTo(25);
        mapa.moveCamera(camera);

        mapa.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if (listMarcadores.isEmpty()) {
                    mostrarPontos();
                }
            }
        });

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

    private void goToFonteURL(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
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

        adicionarMarcador(new Ponto(Textos.RESERVATÓRIO_D_ÁGUA_DO_PAJEÚ, new LatLng(-3.731055, -38.523792)));
        adicionarMarcador(new Ponto(Textos.BUEIRO_DA_ASSEMBLEIA, new LatLng(-3.726865, -38.525080)));
        adicionarMarcador(new Ponto(Textos.CHAFARIZ_DO_PALÁCIO, new LatLng(-3.726640, -38.525745)));
        adicionarMarcador(new Ponto(Textos.COLLECÇÃO_D_ÁGUAS, new LatLng(-3.731218, -38.523375)));
        adicionarMarcador(new Ponto(Textos.PARNASO, new LatLng(-3.727874, -38.525082)));
        adicionarMarcador(new Ponto(Textos.PARQUE_J_DA_PENHA, new LatLng(-3.734400, -38.519165)));
        adicionarMarcador(new Ponto(Textos.EXALAÇÕES_MEPHETICAS, new LatLng(-3.726040, -38.525030)));
        adicionarMarcador(new Ponto(Textos.RUÍNA_DO_AÇUDE, new LatLng(-3.730465, -38.524288)));
        adicionarMarcador(new Ponto(Textos.PARQUE_DAS_ESCULTURAS, new LatLng(-3.731416, -38.522443)));
        adicionarMarcador(new Ponto(Textos.POSTURAS_CAMARAIS_DE_1835, new LatLng(-3.729369, -38.524890)));
        adicionarMarcador(new Ponto(Textos.POSTURAS_CAMARAIS_DE_1844, new LatLng(-3.725806, -38.524830)));
        adicionarMarcador(new Ponto(Textos.PROIBIDO_APRESENTAR_SE_NU, new LatLng(-3.728295, -38.525033)));
        adicionarMarcador(new Ponto(Textos.PROJETO_CENTRO_BELO, new LatLng(-3.725545, -38.524972)));
        adicionarMarcador(new Ponto(Textos.FORTE_DE_AREIA, new LatLng(-3.722657, -38.524890)));
        adicionarMarcador(new Ponto(Textos.OBRA, new LatLng(-3.730758, -38.524026)));
        adicionarMarcador(new Ponto(Textos.PARQUE_PAJEÚ, new LatLng(-3.723882, -38.523778)));
        adicionarMarcador(new Ponto(Textos.TERCEIRO_PLANO, new LatLng(-3.721542, -38.526187)));
        adicionarMarcador(new Ponto(Textos.BELLO_RIO_DE_ÁGUA_DOCE, new LatLng(-3.722628, -38.523973)));
        adicionarMarcador(new Ponto(Textos.RELATORIO_MANSOUR, new LatLng(-3.740938, -38.505251)));
        adicionarMarcador(new Ponto(Textos.RELATORIO_INSPECAO, new LatLng(-3.720197, -38.522739)));

        if(!pontosAdicionados) {
            pontosAdicionados = true;
        }

        if(marcadorJogadorAdicionado){
            mostrarPontoMaisProximo();
        }

    }

    private void adicionarMarcador(Ponto ponto) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(ponto.getNome());
        markerOptions.position(ponto.getLocalizacao());
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_audio_nao_ouvido));

        Marker marker = mapa.addMarker(markerOptions);
        listMarcadores.add(marker);
    }

    private void mostrarPontoMaisProximo(){
        verificarProximidadeDoMarcador(marcadorJogador);

        if (showPolyLine) {
            removePolyLineToFirstPoint();
            addPolyLineToFirstPoint(marcadorJogador.getPosition(), verificarMarcadorMaisProximo(marcadorJogador));
        }
    }

    private void addPolyLineToFirstPoint(LatLng userPosition, Marker firstPointMarker) {
        if (firstPointMarker == null) {
            return;
        }

        polylineOptions = new PolylineOptions().add(new LatLng(userPosition.latitude, userPosition.longitude), firstPointMarker.getPosition())
                .width(5).color(Color.CYAN);

        polylineToFirstPoint = mapa.addPolyline(polylineOptions);
    }

    private void removePolyLineToFirstPoint() {
        if (polylineToFirstPoint != null) {
            polylineToFirstPoint.remove();
        }
    }

    public void takePicture(View view) {
        try {
            file = createImageFile();

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getAlbumDir() {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Excursao Pajeu/");

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = getAlbumDir().toString() + "/" + timeStamp + ".jpg";
        File image = new File(imageFileName);

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

            String mailto = "mailto:excursaopajeu@gmail.com" +
                    "?subject=" + Uri.encode("Foto Excursão Pajeú") +
                    "&body=" + Uri.encode("Fui ao Pajeú e lembrei de você.");

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse(mailto));

            Uri uri = Uri.fromFile(file);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            try {
                startActivity(Intent.createChooser(intent, "Enviar foto:"));
            } catch (android.content.ActivityNotFoundException ex) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog);
                builder.setTitle(R.string.sem_aplicativo_de_email).setMessage(getString(R.string.app_name) + " " + getString(R.string.nao_funciona_sem_gps));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
                builder.setCancelable(false).create().show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Armazenamento.salvarLocalizacao(location, this);
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (mapa != null) {
            if (location.getAccuracy() <= 10) {
                novaLocalizacao(location);
            }
        }

        if(firstLocation){
            novaLocalizacao(location);
            moverCamera(location);
            firstLocation = false;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog);
            builder.setTitle(R.string.gps_desativado).setMessage(getString(R.string.app_name) + " " + getString(R.string.nao_funciona_sem_gps));

            builder.setPositiveButton(R.string.ativar, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            builder.setNegativeButton(R.string.sair, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });

            builder.setCancelable(false).create().show();
        }
    }
}