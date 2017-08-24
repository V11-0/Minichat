package com.ifsphto.vlp_info2_2017.minichat.page;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.ifsphto.vlp_info2_2017.minichat.LoginActivity;
import com.ifsphto.vlp_info2_2017.minichat.R;
import com.ifsphto.vlp_info2_2017.minichat.connection.MessagesConnection;
import com.ifsphto.vlp_info2_2017.minichat.object.Message;
import com.ifsphto.vlp_info2_2017.minichat.page.adapters.MessagesAdapter;

/**
 * Classe da tela de conversa, onde é possível enviar e
 * receber mensagens enviadas diretamente a outro usuário
 */
public class ChatActivity extends AppCompatActivity {

    // Campo onde a mensagem é digitada
    private EditText send_message;

    // Adaptador para posicionar as mensagens corretamente na tela
    private MessagesAdapter ma;

    // Strings dizendo qual o nome dos usuários desta conversa
    private static String thisUser;
    private static String hereUser;

    // Cria a classe que fará a conexão ao banco mensagens
    private MessagesConnection connectionClass = new MessagesConnection();
    private Connection con = null;

    // Mensagem
    private String message;

    // Nome da tabela criada no MySQL
    private String table;

    // ListView para exibir as Mensagens
    private ListView messages_view;

    /*
     ProgressDialog que dizem ao usuário que as mensagens
     estão sendo enviadas ou sendo obtidas
      */
    private ProgressDialog gmpd;
    private ProgressDialog smpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // SharedPreferences aqui é criada, para posteriormente recuperar o nome do usuário atual
        SharedPreferences prefs = getSharedPreferences(LoginActivity.LOGIN_PREFS, MODE_PRIVATE);

        messages_view = (ListView) findViewById(R.id.messages_view);

        // Obtém o nome dos dois usuários desta conversa
        thisUser = getIntent().getStringExtra("SelectedName");
        hereUser = prefs.getString("name", null);

        /*
         Com o nome dos dois usuários, será definido qual o nome da tabela
         que será criada no MySQL para a conversa destes dois usuários

         O nome da tabela é definido pelo primeiro nome em ordem alfabética
         por exemplo, temos um usuário "vintxg" conversando com o usuário "anrty8",
         assim o nome da tabela que ficaria no banco seria:

         anrty8vintxg

         PS: tudo em minúsculo, toLowerCase() faz isso
          */
        if (hereUser.compareToIgnoreCase(thisUser) < 0)
            table = (hereUser + thisUser).toLowerCase();
        else
            table = (thisUser + hereUser).toLowerCase();

        // Criará e setará o nome da pessoa com quem o usuário está conversando na barra superior
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        toolbar.setTitle(thisUser);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Recupera campo para digitar a mensagem, e o botão para envia-lás
        send_message = (EditText)findViewById(R.id.send_message);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab_send_message);

        // Coloca os atributos do dialogo para indicar q a mensagem está sendo enviada
        smpd = new ProgressDialog(this);
        smpd.setMessage(getString(R.string.sending));
        smpd.setCancelable(false);
        smpd.incrementProgressBy(ProgressDialog.STYLE_SPINNER);

        // Define a ação que será realizada quando a pessoa clicar no botão enviar
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        // Coloca os atributos do dialogo para indicar q as mensagens da conversa estão
        // sendo obtidas
        gmpd = new ProgressDialog(this);
        gmpd.setMessage(getString(R.string.get_messages));
        gmpd.setCancelable(false);
        gmpd.incrementProgressBy(ProgressDialog.STYLE_SPINNER);

        // Carrega todas as mensagens do banco externo
        loadMessages();
    }

    private void sendMessage() {

        // Recupera a mensagem digitada
        message = send_message.getText().toString();

        // Verifica se a mensagem está vazia ou contém aspas
        if (TextUtils.isEmpty(message))
            Toast.makeText(this, R.string.msg_empty,
                    Toast.LENGTH_LONG).show();
        else if (message.contains("'"))
            Snackbar.make(messages_view, getString(R.string.post_error),
                    Snackbar.LENGTH_LONG).show();
        else {

            // Exibe "Enviando mensagem"
            smpd.show();

            // Envia
            SendMessage sm = new SendMessage();
            sm.execute("");

            // Carrega todas as mensagens atualizadas
            loadMessages();

            // Deixa o campo para digitar vazio
            send_message.setText("");
        }
    }

    private void loadMessages() {

        // Cria o adaptador
        ma = new MessagesAdapter(getApplicationContext(), R.layout.right);

        // Mostra o dialogo "Carregando mensagens"
        gmpd.show();

        // Obtém as mensagens
        GetMessages gm = new GetMessages();
        gm.execute("");

        // Seta o adapter na ListView
        messages_view.setAdapter(ma);
    }

    private class SendMessage extends AsyncTask<String,String,String> {
        String z = "";
        ResultSet rs;
        boolean isSuccess = false;

        AlertDialog.Builder dlg;

        @Override
        protected void onPostExecute(String s) {

            dlg.setMessage(s);

            try {

                if (!isSuccess) {
                    smpd.dismiss();
                    dlg.show();
                }
                smpd.dismiss();

            } catch (Exception e){
                e.printStackTrace();
                smpd.dismiss();
                dlg.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            dlg = new AlertDialog.Builder(ChatActivity.this);
            dlg.setNeutralButton("OK", null);

            try {

                if (con == null)
                    z = connectionClass.getException();
                else {

                    String query = "INSERT INTO " + table + " values ('" + hereUser + "', '" + message + "');";

                    Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    rs = stmt.executeQuery("show tables like '" + table + "'");

                    if (rs.next()) {
                        stmt.execute(query);
                        isSuccess = true;
                    }
                    else {
                        StringBuilder sb = new StringBuilder();

                        sb.append("CREATE TABLE ").append(table).append(" ( ");
                        sb.append("Who varchar(200) not null, ");
                        sb.append("Content varchar(10000) not null ");
                        sb.append("); ");

                        stmt.execute(sb.toString());

                        stmt.execute(query);
                        isSuccess = true;
                    }
                }

            } catch (Exception ex) {
                z = ex.getMessage();
                ex.printStackTrace();
            }
            return z;
        }
    }

    private class GetMessages extends AsyncTask<String,String,String> {
        String z = "";
        ResultSet rs;
        boolean isSuccess = false;

        AlertDialog.Builder dlg;

        @Override
        protected void onPostExecute(String s) {

            dlg.setMessage(s);

            try {

                if (isSuccess) {

                    rs.first();

                    do {

                        Message msn = new Message(rs.getString(1).equals(thisUser), rs.getString(2));
                        ma.add(msn);

                    } while (rs.next());

                }
                gmpd.dismiss();

            } catch (Exception e){
                e.printStackTrace();
                gmpd.dismiss();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            dlg = new AlertDialog.Builder(ChatActivity.this);
            dlg.setNeutralButton("OK", null);

            if (con == null)
                con = connectionClass.conn();

            try {

                if (con == null)
                    z = connectionClass.getException();
                else {

                    Statement stmt = con.createStatement();

                    rs = stmt.executeQuery("show tables like '" + table + "'");

                    if (rs.next()) {
                        rs = stmt.executeQuery("SELECT * FROM " + table);

                        if (rs.next())
                            isSuccess = true;
                    }else
                        gmpd.dismiss();
                }

            } catch (Exception ex) {
                z = ex.getMessage();
                ex.printStackTrace();
            }
            return z;
        }
    }
}
