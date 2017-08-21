package com.ifsphto.vlp_info2_2017.minichat.page.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vinibrenobr11 on 27/04/2017 at 23:01
 */
public class Database extends SQLiteOpenHelper {

    /**
     * Eu tive a ideia de colocar um banco de dados SQLite no próprio
     * App, para que a pessoa tenha acesso as mensagens e Postagens
     * Mesmo quando ela estiver off-line como um Whats da vida
     *
     * Não consegui implementar isso ainda, quando o App começar a
     * funcionar mesmo, a gente implementa isso
     */
    private String name;

    /**
     * Construtor recebendo dois parametros
     *
     * @param context o contexto da aplicação
     *                @see Context
     * @param name nome do banco
     */
    public Database (Context context, String name) {
        super(context, "Mensagens", null, 1);
        this.name = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Esse StringBuilder concatena o script sem desperdiçar muita memória
        StringBuilder sqlbuilder = new StringBuilder();

        // Script para criar a tabela
        sqlbuilder.append("CREATE TABLE IF NOT EXISTS ").append(name).append(" ( ");
        sqlbuilder.append("CONTENT                VARCHAR (5000), ");
        sqlbuilder.append("WHO                    INT ");
        sqlbuilder.append("); ");

        // Executa o script para criar a tabela
        db.execSQL(sqlbuilder.toString());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }
}

