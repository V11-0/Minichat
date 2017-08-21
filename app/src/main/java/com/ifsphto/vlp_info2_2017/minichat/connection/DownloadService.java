package com.ifsphto.vlp_info2_2017.minichat.connection;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by
 * @author vinibrenobr11 on 19/05/2017 at 23:34
 *
 * Essa classe será usada para realizar o download de uma atualização do app
 * @see android.app.Service para mais detalhes
 */
public abstract class DownloadService extends Service {

    @Override
    public void onCreate() {

        //// TODO: 20/05/2017 Fazer notificação e download de atualizações por essa classe

        Connection conn = new UpdateConnection().conn();
        NotificationManager mNotifierManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Atualizando").setContentText("Baixando arquivo de atualização");
        //// TODO: 15/08/2017 Repassar strings para R.String;

        try {

            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT Archive FROM att");

            rs.first();

            BufferedInputStream is = new BufferedInputStream(rs.getBinaryStream(1));
            FileOutputStream fos = new FileOutputStream(getExternalCacheDir() + "/app-debug.apk");

            int data;

            // Faz o download e escreve em um arquivo.apk
            while ((data = is.read()) != -1)
                fos.write(data);

            fos.flush();
            is.close();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn = null;
        }
    }

    @Override
    public void onDestroy() {

        // Ao fim do serviço, esse método é executado
    }
}
