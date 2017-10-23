package com.ifsphto.vlp_info2_2017.minichat.connection;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by vinibrenobr11 on 26/02/2017 at 21:17<br><br>
 *
 * Essa Classe distribui conexões com o servidor MySQL
 */
public class ConnectionClass {

    private String exception;

    /**
     * Obtém uma excecão que occoreu durante a realização da conexão
     * @return Mensagem da Exceção
     */
    public String getException() {
        return exception;
    }

    /**
     * Aqui é realizada conexão com o MySQL
     * @param schema define em qual database será feita a conexão 1 para schema de Mensagens e 0
     * para schema padrão
     * @return conexão ao database, null em caso de erro
     */
    @SuppressLint("NewApi")
    public Connection conn(boolean schema) {

        // Essa linha faz com que qualquer tipo de conexão
        // Seja permitida, sendo segura ou não
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;

        try {

            // Diz qual a classe do driver que será usada para realizar a conexão com o banco
            Class.forName("com.mysql.jdbc.Driver");

            // Define o limite de tempo antes da conexão ser encerrada por timeout
            DriverManager.setLoginTimeout(10);

            String base;

            // Dependendo do parâmetro, uma database é escolhida
            if (schema)
                base = "app_messages";
            else
                base = "app";

            //Realiza a conexão e a atribui ao objeto Connection
            conn = DriverManager.getConnection("jdbc:mysql://192.168.0.254/" + base, "App", "123456");

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

        // Retorna a Conexão. null se algum erro ocorreu
        return conn;
    }
}