package br.great.jogopervasivo.beans.mecanicas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.File;

import br.great.jogopervasivo.actvititesDoJogo.Mapa;
import br.great.jogopervasivo.beans.Mecanica;
import br.great.jogopervasivo.util.Constantes;
import br.ufc.great.arviewer.pajeu.R;

/**
 * Created by messiaslima on 05/05/2015.
 *
 * @author messiaslima
 * @version 1.0
 */
public class VVideos extends Mecanica implements Imecanica {

    private String arqVideo;

    public String getArqVideo() {
        return arqVideo;
    }

    public void setArqVideo(String arqVideo) {
        this.arqVideo = arqVideo;
    }

    @Override
    public void realizarMecanica(final Context context) {

        if (getEstado()==2){
            return;
        }

        new AsyncTask<Void, Void, Boolean>() {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(context);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(context.getString(R.string.obtendo_informacoes));

                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
              /*  JSONObject acao = new JSONObject();
                JSONObject mecanica = new JSONObject();
                JSONArray requisiscao = new JSONArray();
                try {
                    acao.put("acao", 107);
                    mecanica.put("jogo_id", InformacoesTemporarias.jogoAtual.getId());
                    mecanica.put("grupo_id", InformacoesTemporarias.grupoAtual.getId());
                    mecanica.put("mecanica_id", getId());
                    mecanica.put("jogador_id", InformacoesTemporarias.idJogador);
                    requisiscao.put(0, acao);
                    requisiscao.put(1, mecanica);
                    JSONObject resposta = new JSONArray(Servidor.fazerGet(requisiscao.toString())).getJSONObject(0);
                    return resposta.getInt("result") != 0;
                } catch (JSONException je) {
                    Log.e(Constantes.TAG, "erro no json " + je.getMessage());
                    je.printStackTrace();
                    return false;
                }*/
                return verificarAutorizacaoDaMecanica(context);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                progressDialog.dismiss();
                if (aBoolean) {
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(Constantes.PASTA_DE_ARQUIVOS, getArqVideo())), "video/*");
                    Mapa.mecanicaVVideosAtual = VVideos.this;
                    ((Activity) context).startActivityForResult(intent, Mapa.REQUEST_CODE_VER_VIDEO);
                } else {
                    mostarToastFeedback(context);
                }
            }
        }.execute();
    }
}
