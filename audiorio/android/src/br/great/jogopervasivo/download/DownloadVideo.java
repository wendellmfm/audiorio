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
 * Created by messiaslima on 05/05/2015.
 *
 * @author messiaslima
 * @version 1.0
 */
public class DownloadVideo extends AsyncTask<Void, Void, File> {
    private String arquivo;
    File video;

    public DownloadVideo(String arquivo) {
        this.arquivo = arquivo;
    }

    public File downloadVideoSincrono() throws IOException {

        video = InformacoesTemporarias.criarVideoTemporario();
        URL url = new URL(Armazenamento.resgatarIPArquivos(LoginActivity.getInstancia()) + "cvideos/" + arquivo);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream input = connection.getInputStream();
        FileOutputStream fos = new FileOutputStream(video);
        int bytes;
        while ((bytes = input.read()) != -1) {
            fos.write(bytes);
        }
        input.close();
        fos.close();
        connection.disconnect();

        return video;
    }

    @Override
    protected File doInBackground(Void... params) {
        try {
            return downloadVideoSincrono();
        } catch (IOException ioe) {
            if (video != null) {
                video.delete();
            }
            return null;
        }
    }
}
