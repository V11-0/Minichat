package com.ifsphto.vlp_info2_2017.minichat.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.net.nsd.NsdServiceInfo;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.ifsphto.vlp_info2_2017.minichat.BuildConfig;
import com.ifsphto.vlp_info2_2017.minichat.R;
import com.ifsphto.vlp_info2_2017.minichat.database.DbManager;
import com.ifsphto.vlp_info2_2017.minichat.page.ChatActivity;
import com.ifsphto.vlp_info2_2017.minichat.utils.Tags;
import com.ifsphto.vlp_info2_2017.minichat.utils.notification.Channels;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by vinibrenobr11 on 11/11/2017 at 17:21:40
 */
public class MessageService extends IntentService {

    public NsdServiceInfo thisHost;
    private ServerSocket serverSocket;

    public MessageService() {
        super("Serviço de Mensagens");
    }

    @Override
    public void onDestroy() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        assert intent != null;
        thisHost = intent.getParcelableExtra("ThisHost");

        try {
            serverSocket = new ServerSocket(intent.getIntExtra("Port", 0));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (serverSocket != null) {
            try {
                while (true) {
                    Socket client = serverSocket.accept();

                    new Thread(() -> {
                        Log.i(Tags.LOG_TAG, "Recebido");

                        String name = null;
                        String msg = null;
                        int port = 0;

                        try {
                            DataInputStream in = new DataInputStream(client.getInputStream());

                            name = in.readUTF();
                            msg = in.readUTF();
                            port = in.readInt();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        DbManager manager = new DbManager(getApplicationContext(), "'" + name
                         + "'");

                        NsdServiceInfo client_nsd = new NsdServiceInfo();
                        client_nsd.setServiceName(name);
                        client_nsd.setServiceType(Tags.Nsd.TYPE);
                        client_nsd.setPort(port);
                        client_nsd.setHost(client.getInetAddress());

                        if (manager.insert(name, msg))
                            sendNotification(name, msg, client_nsd);
                        else
                            Log.e(Tags.LOG_TAG, "Erro ao salvar msg recebida no sqlite");

                        manager.close();
                    }).start();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        } else
            throw new NullPointerException("Socket é nulo");
    }

    private void sendNotification(String name, String msg, NsdServiceInfo who) {

        Uri sound = Uri.parse(getApplicationContext().getSharedPreferences(Tags.PREFERENCES
                , 0).getString("pref_sound", ""));

        PendingIntent resultIntent;
        TaskStackBuilder mTask = TaskStackBuilder.create(getBaseContext());
        Intent it = new Intent(this, ChatActivity.class);

        it.putExtra("ServiceInfo", who);

        mTask.addNextIntent(it);
        resultIntent = mTask.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.i("Notificacao", "Enviou Notificaçaõ");

        NotificationManager ntm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            ntm.createNotificationChannel(Channels.getMessagesChannel());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this
                , Tags.Notification.CHANNEL_MESSAGE_ID);

        builder.setContentTitle("Nova Mensagem de " + name)
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_chat_white_24dp)
                .setSound(sound)
                .setVibrate(new long[] {1000, 1000, 1000, 1000})
                .setAutoCancel(true)
                .setContentIntent(resultIntent);

        ntm.notify(2, builder.build());
    }
}