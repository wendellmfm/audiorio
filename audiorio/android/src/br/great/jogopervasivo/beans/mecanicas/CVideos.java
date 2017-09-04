package br.great.jogopervasivo.beans.mecanicas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.io.File;

import br.great.jogopervasivo.actvititesDoJogo.Mapa;
import br.great.jogopervasivo.beans.Mecanica;
import br.great.jogopervasivo.util.InformacoesTemporarias;
import br.ufc.great.arviewer.pajeu.R;

/**
 * Created by messiaslima on 18/03/2015.
 *
 * @author messiaslima
 */
public class CVideos extends Mecanica implements Imecanica {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
               /* JSONObject acao = new JSONObject();
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
                if (aBoolean) {


                    InformacoesTemporarias.jogoOcupado = true;
                    File video = InformacoesTemporarias.criarVideoTemporario();
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    ((Activity)context).startActivityForResult(intent, Mapa.REQUEST_CODE_VIDEO);

                } else {
                    mostarToastFeedback(context);
                }
            }
        }.execute();

    }
}
