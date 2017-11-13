package com.ifsphto.vlp_info2_2017.minichat.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.nsd.NsdServiceInfo;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ifsphto.vlp_info2_2017.minichat.R;
import com.ifsphto.vlp_info2_2017.minichat.database.DbManager;
import com.ifsphto.vlp_info2_2017.minichat.utils.Tags;
import com.ifsphto.vlp_info2_2017.minichat.utils.notification.Channels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by vinibrenobr11 on 11/11/2017 at 17:21:40
 */
public class MessageService extends IntentService {

    public NsdServiceInfo thisHost;

    public MessageService() {
        super("Serviço de Mensagens");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        assert intent != null;
        thisHost = intent.getParcelableExtra("Host");

        ServerSocket serverSocket = null;

        try {
            Log.d("Host", thisHost.toString());
            serverSocket = new ServerSocket(thisHost.getPort()+1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (serverSocket != null) {
            try {
                while (true) {
                    Log.d(Tags.LOG_TAG, "Serviço travado em accept");
                    Socket client = serverSocket.accept();
                    Log.d(Tags.LOG_TAG, "Passou do accept");

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(client.getInputStream()));

                    String all;

                    Log.i(Tags.LOG_TAG, "Criado Buffer");

                    while((all = in.readLine()) != null) {
                        Log.i(Tags.LOG_TAG, "Criado Buffer");

                        DbManager manager = new DbManager(getApplicationContext());
                        SQLiteDatabase db = manager.getWritableDatabase();

                        Log.i(Tags.LOG_TAG, "Database recuperada");
                        ContentValues cv = new ContentValues();

                        String[] data = all.split(Tags.Database.SPLIT_REGEX);
                        db.execSQL(Tags.Database.CREATE.replace("?", data[0]));

                        cv.put(Tags.Database.MSG_COLUMN_AUTHOR, data[0]);
                        cv.put(Tags.Database.MSG_COLUMN_MESSAGE, data[1]);

                        Log.d("é nulo?", data[0]);

                        db.insert(data[0].replace(" ", ""), null, cv);

                        sendNotification(data[0], data[1]);
                    }

                }
            } catch(Exception e){
                e.printStackTrace();
            }
        } else
            throw new NullPointerException("Socket é nulo");
    }

    private void sendNotification(String name, String msg) {

        Log.i("Notificacao", "Chegou aqui");

        NotificationManager ntm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            ntm.createNotificationChannel(Channels.getMessagesChannel());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this
                , Tags.Notification.CHANNEL_MESSAGE_ID);

        builder.setContentTitle("Nova Mensagem de " + name)
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_chat_black_24dp);

        ntm.notify(2, builder.build());
    }
}
