package com.ifsphto.vlp_info2_2017.minichat.connection;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by vinibrenobr11 on 26/02/2017 at 21:17
 */
public class ConnectionClass {

    /**
     * Essa classe Faz a conexão ao bando de dados MySQL
     * E retorna essa conexão como uma Interface Connection
     */
    private String exception;

    // Em caso de uma exception esse método retorna a
    // mensagem dessa Exception
    public String getException() {
        return exception;
    }

    // Início do método que realiza e retorna a Conexão
    @SuppressLint("NewApi")
    public Connection conn() {

        // Essa linha faz com que qualquer tipo de conexão
        // Seja permitida, sendo segura ou não
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;

        try {

            // Diz qual a classe do driver que
            // Será usada para realizar a conexão com o banco
            Class.forName("com.mysql.jdbc.Driver");

            // Define o limite de tempo antes
            // Da conexão ser encerrada por timeout
            DriverManager.setLoginTimeout(10);

            /*
            Realiza a conexão e a atribui
            ao objeto Connection
             */
            conn = DriverManager.getConnection("jdbc:mysql://192.168.0.254/app", "App", "123456");

        } catch (Exception se) {
            /*
            Aqui um log dizendo
            que uma Exception ocorreu é printado no console
             */
            Log.e("ERRO", se.getMessage());

            // Atribui a Exception a uma String
            // Para ser dito ao Usuário o que Aconteceu
            exception = se.getMessage();

            // Imprime tudo sobre o erro no console
            se.printStackTrace();
        }
        // Retorna a Conexão. null, se algum erro ocorreu
        return conn;
    }
}