package br.great.jogopervasivo.gcmUtil;


import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.great.jogopervasivo.actvititesDoJogo.Mapa;
import br.great.jogopervasivo.beans.Jogador;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.great.jogopervasivo.webServices.RecuperarObjetosInventario;
import br.great.jogopervasivo.webServices.SolicitarMissaoAtual;

/**
 * Created by messiaslima on 26/02/2015.
 *
 * @author messiaslima
 * @version 1.0
 */
public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(GcmIntentService.this);
        String messageType = gcm.getMessageType(intent);

        if (extras != null) {
            switch (messageType) {
                case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:
                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:
                    break;
                default:
                    String usuario = extras.getString("user"), tipoAcao = extras.getString("tipoacao");
                    if (usuario.equals("root")) {
                        if (tipoAcao.equals("getMecanicaAtual")) {
                            solicitarNovasMecanicas();
                        }
                        if (tipoAcao.equals("apresMensagem")) {
                            InformacoesTemporarias.mensagem = extras.getString("message");
                            //Mapa.getInstancia().verificarMensagem();
                        }
                        if (tipoAcao.equals("atualizaInventario")) {
                            RecuperarObjetosInventario.recuperar(this);
                        }
                        if (tipoAcao.equals("atualizaLocalizacao") && InformacoesTemporarias.grupo != null) {
                            List<Jogador> marcadores = new ArrayList<>();
                            try {
                                JSONArray jsonArray = new JSONArray(extras.getString("localizacao"));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject equipes = jsonArray.getJSONObject(i);
                                    JSONObject grupo = equipes.getJSONObject("grupo");
                                    int tipo;
                                    if (grupo.getInt("grupo_id") == InformacoesTemporarias.grupoAtual.getId()) {
                                        tipo = Mapa.MARCADOR_ALIADO;
                                    } else {
                                        tipo = Mapa.MARCADOR_ADVERSARIO;
                                    }
                                    JSONArray jogadores = equipes.getJSONArray("jogadores");
                                    for (int j = 0; j < jogadores.length(); j++) {
                                        JSONObject jogador = jogadores.getJSONObject(j);
                                        Jogador m = new Jogador();
                                        m.setTipo(tipo);
                                        m.setNome(jogador.getString("nome"));
                                        m.setPosicao(new LatLng(jogador.getDouble("latitude"), jogador.getDouble("longitude")));
                                        if (jogador.getInt("id") != InformacoesTemporarias.idJogador) {
                                            marcadores.add(m);
                                        }
                                    }
                                }
                                InformacoesTemporarias.marcadoresDeJogadores = marcadores;
                            } catch (JSONException je) {
                                je.printStackTrace();
                            }
                        }
                    } else {

//                        String mensagem = extras.getString("message");
//                        ChatActivity.receberMensagem(usuario, mensagem);
//                        if (!ChatActivity.telaAberta) {
//                            NotificationCustomUtil.sendNotification(GcmIntentService.this, usuario, mensagem);
//                        }
                    }

                    break;
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    private void solicitarNovasMecanicas() {
        SolicitarMissaoAtual solicitarMissaoAtual = new SolicitarMissaoAtual(Mapa.getInstancia());
        solicitarMissaoAtual.execute();
    }
}
