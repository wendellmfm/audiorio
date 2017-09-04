package br.great.jogopervasivo.beans.mecanicas;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import br.ufc.great.arviewer.pajeu.R;
import br.great.jogopervasivo.beans.Mecanica;
import br.great.jogopervasivo.util.Constantes;

/**
 * Created by messiaslima on 03/03/2015.
 *
 * @author messiaslima
 */
public class Vfotos extends Mecanica implements Imecanica {
    private int idFotos;
    private String arqImage;
    public static final int REQUEST_CODE_VER_IMAGEM = 6;
    private static final String PASTA_IMAGENS = Constantes.PASTA_DE_ARQUIVOS;

    public int getIdFotos() {
        return idFotos;
    }

    public void setIdFotos(int id) {
        this.idFotos = id;
    }

    public String getArqImage() {
        return arqImage;
    }

    public void setArqImage(String arqImage) {
        this.arqImage = arqImage;
    }

    @Override
    public void realizarMecanica(final Context context) {


        if (getEstado() == 2) {
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
       /*         JSONObject acao = new JSONObject();
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
//                    InformacoesTemporarias.jogoOcupado = true;
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    View view = ((Activity) context).getLayoutInflater().inflate(R.layout.vfotos, null);
//                    builder.setView(view);
//                    builder.setTitle(getNome());
//                    if (arqImage.trim().length() > 0) {
//                        ImageView imageView = (ImageView) view.findViewById(R.id.vfotos_imageView);
//                        imageView.setImageBitmap(BitmapFactory.decodeFile(PASTA_IMAGENS + "/" + arqImage));
//                    } else {
//                        TextView textView = (TextView) view.findViewById(R.id.vfotos_textView);
//                        textView.setText(R.string.falha_de_conexao);
//                    }
//                    builder.setNegativeButton("hue", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            confirmarRealizacao(context, null, null, null);
//                        }
//                    });
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
//                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    final Dialog dialog = new Dialog(context);

                    View view = ((Activity) context).getLayoutInflater().inflate(R.layout.vfotos, null);
                    if (arqImage.trim().length() > 0) {
                        ImageView imageView = (ImageView) view.findViewById(R.id.vfotos_imageView);
                        imageView.setImageBitmap(BitmapFactory.decodeFile(PASTA_IMAGENS + "/" + arqImage));
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmarRealizacao(context, null, null, null);
                                dialog.dismiss();
                            }
                        });
                    } else {
                        TextView textView = (TextView) view.findViewById(R.id.vfotos_textView);
                        textView.setText(R.string.falha_de_conexao);
                    }

                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(view);
                    dialog.show();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                } else {
                    mostarToastFeedback(context);
                }
            }
        }.execute();


        //Codigo pra abrir imagem direto na galeria
        /*Intent it = new Intent();
        it.setAction(android.content.Intent.ACTION_VIEW);
        it.setDataAndType(Uri.fromFile(new File(PASTA_IMAGENS + arqImage)), "image/jpg");
        context.startActivityForResult(it, REQUEST_CODE_VER_IMAGEM);
        */
    }
}
