package com.ifsphto.vlp_info2_2017.minichat.utils.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.widget.Toast;

import com.ifsphto.vlp_info2_2017.minichat.BuildConfig;
import com.ifsphto.vlp_info2_2017.minichat.R;
import com.ifsphto.vlp_info2_2017.minichat.connection.FTPConnection;
import com.ifsphto.vlp_info2_2017.minichat.services.DownloadService;

import org.apache.commons.net.ftp.FTPClient;

/**
 * Created by vinibrenobr11 on 24/10/2017 at 15:24:28<br></br>
 * <p>
 * Essa classe realiza o processo para verificar se há uma atualização disponível para o App
 */
public abstract class VerifyUpdate {

    /**
     * Verifica se há uma versão mais recente do app disponivel, e se sim, exibe um
     * dialog perguntando se o usuário deseja baixa-lá
     */
    public static Object verify(final Activity a) {

        final Object[] view = new Object[1];

        // Inicia a thread para verificar
        final Thread v = new Thread(() -> {

            try {

                // Conecta ao FTP e obtém um nome de um arquivo que contém a versão mais
                // Recente do app
                FTPClient ftp = FTPConnection.getConnection();
                final String nVersion = ftp.listNames()[0];
                final short newVersion = Short.parseShort(nVersion.replace(".", ""));

                // Obtém a versão do app instalado
                final short thisVersion = Short.parseShort(BuildConfig.VERSION_NAME
                        .replace(".", ""));

                FTPConnection.closeConnection(ftp);

                // Se existir uma versão mais nova, um dialogo é feito, se não
                // um toast avisa que o usuário está na versão mais recente

                view[0] = makeSomeWarning(newVersion > thisVersion
                        , BuildConfig.VERSION_NAME, nVersion, a);

            } catch (Exception e) {
                view[0] = e.getMessage();
                // TODO: 29/10/2017 Traduzir mensagens de erro
            }
        });

        v.start();

        try {
            v.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return view[0];
    }

    /**
     * Produz um {@link AlertDialog.Builder} ou {@link Toast} com textos setados
     * dependendo dos parametros passados
     *
     * @param hasUpdate   Se há uma versão mais recente do app disponivel, caso true, é retornado o
     *                    Dialog
     * @param thisVersion Versão atual do app
     * @param newVersion  Versão nova do app
     * @param act         Activity
     * @return Object podendo ser {@link AlertDialog.Builder} ou {@link Toast}.
     */
    private static Object makeSomeWarning(boolean hasUpdate, String thisVersion
            , String newVersion, final Activity act) {

        if (hasUpdate) {

            AlertDialog.Builder builder = new AlertDialog.Builder(act);

            builder.setTitle(act.getString(R.string.update_av_title))

                    .setMessage(act.getString(R.string.update_av_msg)
                            + act.getString(R.string.update_this_ver) + " " + thisVersion
                            + act.getString(R.string.update_new_ver) + " " + newVersion)

                    // Se o usuário clicar em sim, o serviço para baixar é iniciado
                    .setPositiveButton(act.getString(R.string.ofcourse),
                            (dialogInterface, i) -> {
                                act.startService(new Intent(act.getApplicationContext(),
                                        DownloadService.class));

                                Toast.makeText(act.getApplicationContext(),
                                        act.getString(R.string.downloading_literaly)
                                        , Toast.LENGTH_LONG).show();
                            })

                    .setNegativeButton(act.getString(R.string.not_now), null);

            return builder;

        } else {
            return "0";
        }
    }
}
