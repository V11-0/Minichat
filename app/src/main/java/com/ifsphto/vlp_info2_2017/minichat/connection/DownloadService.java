package com.ifsphto.vlp_info2_2017.minichat.connection;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.ifsphto.vlp_info2_2017.minichat.R;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by
 * @author vinibrenobr11 on 19/05/2017 at 23:34
 *
 * Essa classe será usada para realizar o download de uma atualização do app
 * @see android.app.Service para mais detalhes
 */
public class DownloadService extends IntentService {

    // Constrtor com o nome do Serviço
    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        // Cria um gerenciador de notificações
        final NotificationManager mNotifierManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        // Construtor de notificações
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        // Define textos e ícones que a notificação terá
        mBuilder.setContentTitle(getString(R.string.updating))
                .setContentText(getString(R.string.downloading))
        .setSmallIcon(android.R.drawable.stat_sys_download);

        /*
        É criada uma Thread para conectar e baixar atualizações
        de um servidor FTP (File Transfer Protocol)
         */
        new Thread(new Runnable() {
            @Override
            public void run() {

                FTPClient ftp = null;
                OutputStream out = null;
                boolean success = false;

                try {

                    // Cria um cliente FTP e define um login
                    ftp = new FTPClient();
                    ftp.connect("192.168.0.254", 21);
                    ftp.login("usuario", "teste");

                    // Define o tipo de arquivo que será baixado
                    ftp.setFileType(FTP.BINARY_FILE_TYPE);

                    /*
                    Em uma conexão FTP existem os modos ativo
                    e passivo, aqui é definida a conexão por modo passivo
                     */
                    ftp.enterLocalPassiveMode();

                    // Define o arquivo e onde ele será salvo
                    File file = new File(getFilesDir(), "app.apk");
                    // Cria um buffer para escrever os dados
                    out = new BufferedOutputStream(new FileOutputStream(file));

                    // Thread que gerencia o progresso de download na notificação
                    Thread mProgress = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            for (int i = 0; i <= 100; i+=25) {

                                mBuilder.setProgress(100, i, false);
                                mNotifierManager.notify(0, mBuilder.build());

                                try {
                                    Thread.sleep(500);
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                    mProgress.start();

                    // Baixa o arquivo do servidor
                    success = ftp.retrieveFile("app.apk", out);

                    // Se tudo ocorreu bem, a notificação diz que o arquivo foi baixado
                    if (success) {
                        mBuilder.setContentText("").setContentTitle(getString(R.string.down_success))
                                .setProgress(0, 0, false)
                                .setSmallIcon(android.R.drawable.stat_sys_download_done);

                        mNotifierManager.notify(0, mBuilder.build());

                        // TODO: 23/08/2017 Exibir tela para instalação
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    // Libera memória
                    // E desconecta do servidor FTP
                    try {
                        if (out != null)
                            out.close();

                        if (ftp != null) {
                            ftp.logout();
                            ftp.disconnect();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
