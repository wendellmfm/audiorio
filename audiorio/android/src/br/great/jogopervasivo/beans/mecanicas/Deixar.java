package br.great.jogopervasivo.beans.mecanicas;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import br.ufc.great.arviewer.pajeu.R;
import br.great.jogopervasivo.beans.Mecanica;

/**
 * Created by messiaslima on 11/05/2015.
 *
 * @author messiaslima
 * @version 1.0
 */
public class Deixar extends Mecanica implements Imecanica {



    private String tipoObjeto;
    public static final int REQUEST_CODE = 352;

    public String getTipoObjeto() {
        return tipoObjeto;
    }

    public void setTipoObjeto(String tipoObjeto) {
        this.tipoObjeto = tipoObjeto;
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
                /*JSONObject acao = new JSONObject();
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
                    Log.e(Constantes.TAG,"Resposta Deixar " + resposta);
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
//                    Intent intent = new Intent(context, InventarioActivity.class);
//                    intent.putExtra("selecao", true);
//                    intent.putExtra("tipo",getTipoObjeto());
//                    TelaPrincipalActivity.mecanicaDeixarAtual = Deixar.this;
//                    context.startActivityForResult(intent, REQUEST_CODE);
                } else {
                    mostarToastFeedback(context);
                }
            }
        }.execute();


    }
}
