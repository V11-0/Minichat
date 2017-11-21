package com.ifsphto.vlp_info2_2017.minichat.page;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.ifsphto.vlp_info2_2017.minichat.LoginActivity;
import com.ifsphto.vlp_info2_2017.minichat.R;
import com.ifsphto.vlp_info2_2017.minichat.connection.NSDConnection;
import com.ifsphto.vlp_info2_2017.minichat.database.DbManager;
import com.ifsphto.vlp_info2_2017.minichat.object.Message;
import com.ifsphto.vlp_info2_2017.minichat.utils.adapters.MessagesAdapter;

import java.util.ArrayList;

/**
 * Classe da tela de conversa, onde é possível enviar e receber mensagens enviadas diretamente
 * a outro usuário
 */
public class ChatActivity extends AppCompatActivity {

    // Strings dizendo qual o nome dos usuários desta conversa
    private static String otherUser;
    private static String hereUser;
    // Campo onde a mensagem é digitada
    private EditText send_message;
    // Adaptador para posicionar as mensagens corretamente na tela
    private MessagesAdapter ma;

    // ListView para exibir as Mensagens
    private ListView messages_view;

    private SwipeRefreshLayout srl;

    private NsdServiceInfo dInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // SharedPreferences aqui é criada, para posteriormente recuperar o nome do usuário atual
        SharedPreferences prefs = getSharedPreferences(LoginActivity.LOGIN_PREFS, MODE_PRIVATE);

        messages_view = findViewById(R.id.messages_view);

        dInfo = getIntent().getParcelableExtra("ServiceInfo");

        // Obtém o nome dos dois usuários desta conversa
        otherUser = dInfo.getServiceName();
        hereUser = prefs.getString("name", null);

        // Criará e setará o nome da pessoa com quem o usuário está conversando na barra superior
        Toolbar toolbar = findViewById(R.id.toolbar_chat);
        toolbar.setTitle(otherUser);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Recupera campo para digitar a mensagem, e o botão para envia-lás
        send_message = findViewById(R.id.send_message);
        FloatingActionButton fab = findViewById(R.id.fab_send_message);
        fab.setOnClickListener(view -> {
            sendMessage(send_message.getText().toString());
            send_message.setText("");
        });

        srl = findViewById(R.id.chat_refresh);
        srl.setColorSchemeColors(Color.BLUE, Color.CYAN, Color.MAGENTA, Color.RED, Color.BLACK);
        srl.setOnRefreshListener(this::loadMessages);

        srl.setRefreshing(true);
        loadMessages();
    }

    private void sendMessage(final String s) {
        new Thread(() -> {
            try {
                NSDConnection.sendMessage(getApplicationContext(), hereUser, dInfo, s);
                runOnUiThread(this::loadMessages);
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Snackbar.make(messages_view, "Ocorreu um erro", Snackbar.LENGTH_LONG).show());
            }
        }).start();
    }

    private void loadMessages() {

        DbManager dbManager = new DbManager(this, otherUser.replace(" ", ""));

        ma = new MessagesAdapter(this, R.id.msgr);
        setMessages(dbManager.select(otherUser));

        srl.setRefreshing(false);
    }

    private void setMessages(ArrayList<Message> messages) {

        for (Message m : messages)
            ma.add(m);

        messages_view.setAdapter(ma);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.chat_info:
                showInfoDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showInfoDialog() {

        final String[] info = new String[3];

        Thread t = new Thread(() -> {
            info[0] = dInfo.getHost().getHostAddress();
            info[1] = String.valueOf(dInfo.getPort());
        });

        t.start();

        new Thread(() -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle(info[0]).setMessage(info[1]);

            runOnUiThread(() -> dlg.create().show());
        }).start();
    }
}