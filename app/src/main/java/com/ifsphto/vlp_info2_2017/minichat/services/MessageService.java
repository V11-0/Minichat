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

    public MessageService() {
        super("Serviço de Mensagens");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        assert intent != null;
        thisHost = intent.getParcelableExtra("Host");

        ServerSocket serverSocket = null;

        Log.d("Host", thisHost.toString());

        try {
            serverSocket = new ServerSocket(thisHost.getPort()+1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (serverSocket != null) {
            try {
                while (true) {
                    Socket client = serverSocket.accept();

                    new Thread(() -> {
                        Log.i(Tags.LOG_TAG, "Recebido");

                        Socket client_clone = client;

                        String name = null;
                        String msg = null;
                        try {
                            DataInputStream in = new DataInputStream(client_clone.getInputStream());

                            name = in.readUTF();
                            msg = in.readUTF();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        DbManager manager = new DbManager(getApplicationContext()
                                , name.replace(" ", ""));

                        NsdServiceInfo client_nsd = new NsdServiceInfo();
                        client_nsd.setPort(client_clone.getPort());
                        client_nsd.setServiceName(name);
                        client_nsd.setHost(client_clone.getInetAddress());
                        client_nsd.setServiceType(Tags.Nsd.TYPE);

                        // TODO: 20/11/2017 Notificação com a porta correta, ou descobrir na tela de chat

                        if (manager.insert(name, msg))
                            sendNotification(name, msg, client_nsd);
                        else
                            Log.e(Tags.LOG_TAG, "Erro ao salvar msg recebida no sqlite");
                    }).start();
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        } else
            throw new NullPointerException("Socket é nulo");
    }

    private void sendNotification(String name, String msg, NsdServiceInfo who) {

        Uri sound = Uri.parse(getApplicationContext().getSharedPreferences(BuildConfig.APPLICATION_ID + "_preferences", 0)
                .getString("pref_sound", ""));

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
                .setContentIntent(resultIntent);

        ntm.notify(2, builder.build());
    }
}