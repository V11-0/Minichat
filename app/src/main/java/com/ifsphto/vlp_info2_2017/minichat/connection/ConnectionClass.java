package com.ifsphto.vlp_info2_2017.minichat.connection;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by vinibrenobr11 on 26/02/2017 at 21:17<br><br>
 *
 * Essa Classe distribui conexões com o servidor MySQL
 */
public abstract class ConnectionClass {

    /**
     * Aqui é realizada conexão com o MySQL
     * @param schema define em qual database será feita a conexão 1 para schema de Mensagens e 0
     * para schema padrão
     * @return conexão ao database, null em caso de erro
     * @throws Exception se ocorrer algum erro
     */
    public static Connection conn(boolean schema) throws Exception {

        // Essa linha faz com que qualquer tipo de conexão
        // Seja permitida, sendo segura ou não
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Define o limite de tempo antes da conexão ser encerrada por timeout
        DriverManager.setLoginTimeout(15);

        String base;

        // Dependendo do parâmetro, uma database é escolhida
        if (schema)
            base = "app_messages";
        else
            base = "app";

        // Retorna a Conexão.
        return DriverManager.getConnection("jdbc:mysql://192.168.0.254/" + base, "App", "123456");
    }
}