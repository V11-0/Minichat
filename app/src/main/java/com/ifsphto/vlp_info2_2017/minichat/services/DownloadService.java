package com.ifsphto.vlp_info2_2017.minichat.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.FileProvider;

import com.ifsphto.vlp_info2_2017.minichat.R;
import com.ifsphto.vlp_info2_2017.minichat.connection.FTPConnection;
import com.ifsphto.vlp_info2_2017.minichat.settings.SettingsActivity;
import com.ifsphto.vlp_info2_2017.minichat.utils.Tags;
import com.ifsphto.vlp_info2_2017.minichat.utils.notification.Channels;

import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by vinibrenobr11 on 19/05/2017 at 23:34<br></br>
 *
 * Essa classe será usada para realizar o download de uma atualização do app
 * @see android.app.Service para mais detalhes
 */
public class DownloadService extends IntentService {

    // Construtor com o nome do Serviço
    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        // Cria um gerenciador de notificações
        final NotificationManager mNotifierManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        // Cria um NotificationChannel para sdk >= 26 ou 8.0.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            mNotifierManager.createNotificationChannel(Channels.getDownloadChannel());

        // Construtor de notificações
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,
                Tags.Notification.CHANNEL_DOWNLOAD_ID);

        // Define textos e ícones que a notificação terá
        mBuilder.setContentTitle(getString(R.string.updating)).setSmallIcon(android.R.drawable.stat_sys_download)
        .setOngoing(true).setProgress(0, 0, true);

        mNotifierManager.notify(0, mBuilder.build());

        /*
        É criada uma Thread para conectar e baixar atualizações
        de um servidor FTPConnection (File Transfer Protocol)
         */

        boolean success;
        FTPClient ftp = null;

        Thread mProgress = null;

        try {

            ftp = FTPConnection.getConnection();

            // Obtém o tamanho do arquivo, porém esse comando retorna um
            // status e o tamanho
            ftp.sendCommand("SIZE", "app-release.apk");
            String reply = ftp.getReplyString().split(" ")[1];

            // O Tamanho é retornado com 2 caracteres não numéricos no final
            // para retirar esses caracteres, obtemos uma substring do index 0 até o fim - 2
            final long all = Long.parseLong(reply.substring(0, reply.length() - 2));

            // Define o arquivo e onde ele será salvo
            File file = new File(getExternalCacheDir(), "app-release.apk");

            // Cria um buffer para escrever os dados
            OutputStream out = new BufferedOutputStream(new FileOutputStream(file));

            // Thread que gerencia o progresso de download na notificação
            mProgress = new Thread(() -> {

                double now = 0;

                while (!Thread.currentThread().getName().equals("f")) {

                    double prog = (now / all) * 100;

                    mBuilder.setProgress(100, (int) prog, false);
                    mBuilder.setContentText(getString(R.string.downloading, prog));

                    mNotifierManager.notify(0, mBuilder.build());

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {
                    }

                    now = file.length();
                }
            });

            mProgress.start();

            // Baixa o arquivo do servidor
            success = ftp.retrieveFile("app-release.apk", out);

            out.flush();
            out.close();

            mProgress.setName("f");
            Thread.sleep(3000);

            // Se tudo ocorreu bem, a notificação diz que o arquivo foi baixado
            if (success) {

                // Seta um pending intent na notificação, para que quando o usuário clique
                // na notificação, ela abra a tela de instalação
                PendingIntent resultIntent;
                TaskStackBuilder mTask = TaskStackBuilder.create(getBaseContext());
                Intent promptInstall = new Intent(Intent.ACTION_VIEW);

                // Para inferior a versão 7.0 do Android
                if (Build.VERSION.SDK_INT < 24) {

                    promptInstall.setDataAndType(Uri.fromFile(file),
                            "application/vnd.android.package-archive");

                } else {
                    // Para Versão 7.0 ou superior do Android
                    Uri uri = FileProvider.getUriForFile(getBaseContext(),
                            getPackageName() + ".provider", file);

                    promptInstall.setDataAndType(uri, "application/vnd.android.package-archive");
                    promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                mTask.addNextIntent(promptInstall);
                resultIntent = mTask.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                // Seta textos e dados na notificação
                mBuilder.setContentText(getString(R.string.touch_here))
                        .setContentTitle(getString(R.string.down_success))
                        .setProgress(0, 0, false)
                        .setSmallIcon(android.R.drawable.stat_sys_download_done)
                        .setContentIntent(resultIntent)
                        .setOngoing(false)
                        .setAutoCancel(true);

                // Exibe ela
                mNotifierManager.notify(0, mBuilder.build());
            }
        } catch (Exception e) {

            Intent rIntent = new Intent(this, SettingsActivity.class);
            rIntent.putExtra("message", e.getMessage());

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(SettingsActivity.class);
            stackBuilder.addNextIntent(rIntent);

            if (mProgress != null && mProgress.isAlive()) {
                try {
                    mProgress.setName("f");
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {
                }
            }

            mBuilder.setContentText("Toque para mais detalhes")
                    .setContentTitle(getString(R.string.error_lit))
                    .setProgress(0, 0, false)
                    .setSmallIcon(android.R.drawable.stat_notify_error)
                    .setContentIntent(stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT))
                    .setOngoing(false)
                    .setAutoCancel(true);

            mNotifierManager.notify(0, mBuilder.build());

            e.printStackTrace();

        }finally {

            if (ftp != null)
                FTPConnection.closeConnection(ftp);
        }
    }
}