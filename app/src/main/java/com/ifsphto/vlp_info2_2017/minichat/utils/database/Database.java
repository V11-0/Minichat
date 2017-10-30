package com.ifsphto.vlp_info2_2017.minichat.utils.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vinibrenobr11 on 27/04/2017 at 23:01<br></br>
 *
 * Eu tive a ideia de colocar um banco de dados SQLite no próprio App, para que a pessoa
 * tenha acesso as mensagens e Postagens mesmo quando ela estiver off-line como um Whats da vida
 * Não consegui implementar isso ainda, quando o App começar a funcionar mesmo, a gente implementa
 */
public class Database extends SQLiteOpenHelper {

    private String name;

    /**
     * Construtor recebendo dois parametros
     *
     * @param context o contexto da aplicação.
     * @param name nome do banco.
     */
    public Database (Context context, String name) {
        super(context, "Mensagens", null, 1);
        this.name = name;
    }

    /**
     * Cria a uma tabela no banco SQLite
     * @param db database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // query para criar tabela
        String sqlbuilder = "CREATE TABLE IF NOT EXISTS " + name + " ( " +
                "CONTENT                VARCHAR (5000), " +
                "WHO                    INT " +
                "); ";

        // Executa o script para criar a tabela
        db.execSQL(sqlbuilder);
    }

    /**
     * Ultilizado para atualizar o banco
     * @param db Database
     * @param oldVersion versão antiga
     * @param newVersion nova versão
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

