package com.ifsphto.vlp_info2_2017.minichat.connection;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by vinibrenobr11 on 29/04/2017 at 00:40
 */
public class MessagesConnection {

    /**
     * Essa classe é igual a Connection Class
     * A única diferença é que ela é usada para
     * Obter apenas as mensagens.
     * @see ConnectionClass
     */

    private String exception;

    public String getException() {
        return exception;
    }

    @SuppressLint("NewApi")
    public Connection conn() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;

        try {

            Class.forName("com.mysql.jdbc.Driver");

            DriverManager.setLoginTimeout(10);

            conn = DriverManager.getConnection("jdbc:mysql://192.168.0.254/app_messages",
                    "App", "123456");

        } catch (Exception se) {
            Log.e("ERRO", se.getMessage());
            exception = se.getMessage();
            se.printStackTrace();
        }
        return conn;
    }
}
