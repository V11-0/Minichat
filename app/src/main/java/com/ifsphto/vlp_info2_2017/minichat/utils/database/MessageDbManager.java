package com.ifsphto.vlp_info2_2017.minichat.utils.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by vinibrenobr11 on 27/04/2017 at 23:55<br></br>
 *
 * Essea classe é a classe que usaremos para gerenciar o banco de dados SQLite local, por enquanto
 * ela não está sendo usada
 */
public class MessageDbManager {

    // Conexão do banco
    private SQLiteDatabase conn;

    /**
     * Construtor
     *
     * @param conn conexão com o banco de dados SQLite
     */
    public MessageDbManager(SQLiteDatabase conn) {
        this.conn = conn;
    }

    /**
     * Um objeto do tipo {@link ContentValues} é necessário para conseguirmos adicionar dados
     * no banco SQLite. Esse método preenche o ContentValues e o retorna
     *
     * @param content Conteúdo da mensagem.
     * @param who Autor da mensagem.
     * @return CV para executar no banco.
     */
    private ContentValues fillCV(String content, int who) {

        ContentValues values = new ContentValues();

        values.put("CONTENT", content);
        values.put("WHO", who);

        return values;
    }

    /**
     * O Método 'insertOrThrow' insere os dados do ContentValues
     * no banco. Esse método é a mesma coisa que um
     * comando SQL "INSERT INTO"
     *
     * @param table tabela que receberá os dados
     * @param content conteúdo da mensagem
     * @param who autor da mensagem
     */
    public void inserir(String table, String content, int who) {
        conn.insertOrThrow(table, null, fillCV(content, who));
    }
}
