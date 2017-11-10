package com.ifsphto.vlp_info2_2017.minichat.page;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.ifsphto.vlp_info2_2017.minichat.LoginActivity;
import com.ifsphto.vlp_info2_2017.minichat.R;
import com.ifsphto.vlp_info2_2017.minichat.utils.adapters.MessagesAdapter;

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

    // Mensagem
    private String message;

    // ListView para exibir as Mensagens
    private ListView messages_view;

    /*
     ProgressDialog que dizem ao usuário que as mensagens
     estão sendo enviadas ou sendo obtidas
      */
    private ProgressDialog gmpd;
    private ProgressDialog smpd;

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
        hereUser = prefs.getString("name", "Undefined");

        // Criará e setará o nome da pessoa com quem o usuário está conversando na barra superior
        Toolbar toolbar = findViewById(R.id.toolbar_chat);
        toolbar.setTitle(otherUser);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Recupera campo para digitar a mensagem, e o botão para envia-lás
        send_message = findViewById(R.id.send_message);
        FloatingActionButton fab = findViewById(R.id.fab_send_message);

        // Coloca os atributos do dialogo para indicar q a mensagem está sendo enviada
        smpd = new ProgressDialog(this);
        smpd.setMessage(getString(R.string.sending));
        smpd.setCancelable(false);
        smpd.incrementProgressBy(ProgressDialog.STYLE_SPINNER);

        // Define a ação que será realizada quando a pessoa clicar no botão enviar
        //fab.setOnClickListener(v -> sendMessage());

        // Coloca os atributos do dialogo para indicar q as mensagens da conversa estão
        // sendo obtidas
        gmpd = new ProgressDialog(this);
        gmpd.setMessage(getString(R.string.get_messages));
        gmpd.setCancelable(false);
        gmpd.incrementProgressBy(ProgressDialog.STYLE_SPINNER);

        // Recupera SwipeRefreshLayout e Carrega todas as mensagens do banco externo
        srl = findViewById(R.id.chat_refresh);
        srl.setColorSchemeColors(Color.BLUE, Color.CYAN, Color.MAGENTA, Color.RED, Color.BLACK);
        //srl.setOnRefreshListener(this::loadMessages);

        fab.setOnClickListener(view -> {

        });

        srl.setRefreshing(true);
        //loadMessages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
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
        new Thread(() -> {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle(dInfo.getHost().getHostAddress() + "|" + dInfo.getHost().getHostName())
                    .setMessage(dInfo.getPort());

            dlg.create().show();
        }).start();
    }
}