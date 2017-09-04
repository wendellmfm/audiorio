package br.great.jogopervasivo.actvititesDoJogo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import br.great.jogopervasivo.webServices.Servidor;
import br.ufc.great.arviewer.pajeu.R;

public class RegisterActivity extends Activity {

    private EditText loginEditText, senhaEditText, repetirSenhaEditText;
    private static RegisterActivity instancia;

    public static RegisterActivity getInstancia(){
        return instancia;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        iniciarComponentes();
        instancia = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void iniciarComponentes() {
        loginEditText = (EditText) findViewById(R.id.login_edit_text);
        senhaEditText = (EditText) findViewById(R.id.senha_edit_text);
        repetirSenhaEditText = (EditText) findViewById(R.id.repetir_senha_edit_text);
    }

    public void cadastrar(View v) {
        //Verifica se os campos estão digitados corretamente e faz a requisição
        if ((loginEditText.getEditableText().toString().trim().length() > 0) && (senhaEditText.getEditableText().toString().trim().length() > 0) && (senhaEditText.getEditableText().toString().equals(repetirSenhaEditText.getEditableText().toString()))) {
            final String login = loginEditText.getText().toString();
            final String senha = senhaEditText.getText().toString();

            new AsyncTask<Void, Void, JSONObject>() {
                ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);

                @Override
                protected void onPreExecute() {
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage(getString(R.string.enviando_informacoes));
                    progressDialog.show();
                }

                @Override
                protected JSONObject doInBackground(Void... params) {
                    return Servidor.cadastrarNovoUsuário(RegisterActivity.this, login, senha);
                }

                @Override
                protected void onPostExecute(JSONObject jsonObject) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    progressDialog.dismiss();
                    if (jsonObject.optBoolean("resultado")) {
                        builder.setMessage(RegisterActivity.this.getString(R.string.usuario_cadastrado));
                        builder.setCancelable(false);
                        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        builder.create().show();
                    } else {
                        builder.setMessage(RegisterActivity.this.getString(R.string.usuario_ja_cadastrado));
                        builder.create().show();
                    }
                }
            }.execute();
        } else {
            Toast.makeText(getApplicationContext(), R.string.verifique_os_campos, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
