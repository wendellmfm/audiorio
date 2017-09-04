package br.great.jogopervasivo.download;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.great.jogopervasivo.actvititesDoJogo.LoginActivity;
import br.great.jogopervasivo.util.Armazenamento;
import br.great.jogopervasivo.util.Constantes;
import br.great.jogopervasivo.util.InformacoesTemporarias;

/**
 * Created by messiaslima on 07/07/2015.
 *
 * @author messiaslima
 */
public class DownloadObj extends AsyncTask<Void, Void, File> {
    private String arquivo;
    File obj;

    public DownloadObj(String arquivo) {
        this.arquivo = arquivo;
    }

    public File downloadObjSincrono() throws IOException {

        obj = InformacoesTemporarias.criarObjTemporario();
        URL url = new URL(Armazenamento.resgatarIPArquivos(LoginActivity.getInstancia()) + "objeto3d/" + arquivo);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream input = connection.getInputStream();
        FileOutputStream fos = new FileOutputStream(obj);
        int bytes;
        while ((bytes = input.read()) != -1) {
            fos.write(bytes);
        }
        input.close();
        fos.close();
        connection.disconnect();

        return obj;
    }

    @Override
    protected File doInBackground(Void... params) {
        try {
            return downloadObjSincrono();
        } catch (IOException ioe) {
            if (obj != null) {
                obj.delete();
            }
            return null;
        }
    }
}