package com.ifsphto.vlp_info2_2017.minichat.connection;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by vinibrenobr11 on 01/05/2017 at 21:26
 */
public class UpdateConnection {

    /**
     * Essa classe serve para realizar a conexão com o
     * banco de dados de atualizações do app, obtendo
     * a versão mais atual do app e sendo seu download
     * realizado pela classe
     * @see DownloadService
     *
     * Quando a gente implementar o app na rede ad-hoc
     * essa será a única classe de conexão ao banco de dados
     * que ficará ativa no App
     *
     * A arquitetura dessa classe é igual as outras.
     * @see ConnectionClass para mais detalhes
     *
     * @deprecated DownloadService quase funfando
     */

    private String exception;

    public String getException() {
        return exception;
    }

    @SuppressLint("NewApi")
    public Connection conn() {

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitAll().build());

        Connection conn = null;

        try {

            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection("jdbc:mysql://192.168.0.254/updates",
                    "App", "123456");

        } catch (Exception se) {
            Log.e("ERRO", se.getMessage());
            exception = se.getMessage();
            se.printStackTrace();
        }
        return conn;
    }
}
