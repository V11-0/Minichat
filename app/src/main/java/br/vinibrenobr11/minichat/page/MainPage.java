package br.vinibrenobr11.minichat.page;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import br.vinibrenobr11.minichat.LoginActivity;
import br.vinibrenobr11.minichat.R;
import br.vinibrenobr11.minichat.connection.NSDConnection;
import br.vinibrenobr11.minichat.settings.SettingsActivity;
import br.vinibrenobr11.minichat.utils.Tags;

import java.util.Observable;
import java.util.Observer;

/**
 * Essa classe é, por enquanto a maior do projeto, ela é a pagina inicial
 * onde o usuário vê suas mensagens e descobre outros dispositivos na rede
 */

// TODO: 21/11/2017 Hardcoded Strings... passar para strings.xml

public class MainPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Observer {

    /*
     Nome do campo dentro do arquivo shared preferences
     ele diz se o usuário está logado atraves de um boolean
     essa string diz o nome desse campo
     */
    public static final String PREF_LOG = "isLoggedIn";

    // Objeto representando o arquivo SharedPreferences
    private SharedPreferences prefs;

    // Listas e Adapters
    private ListView mDevs;

    private NSDConnection nsdConn;
    private ArrayAdapter<NsdServiceInfo> devs;

    private TextView userId;
    private TextView userHost;

    @Override
    protected void onDestroy() {
        nsdConn.finishEverything();
        super.onDestroy();
    }

    // TODO: 19/11/2017 'Mensagens' e 'Descobrir' vão virar fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);

        setLayout();

        SwipeRefreshLayout myRefresh = findViewById(R.id.swiperefresh);
        myRefresh.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN,
                Color.YELLOW, Color.MAGENTA);

        myRefresh.setOnRefreshListener(this::discover);

        nsdConn = new NSDConnection(this);
        nsdConn.register(prefs.getString("name", null));
        // TODO: 18/11/2017 Transformar registro e broadcast do NSD em um serviço

        mDevs.setOnItemClickListener((adapterView, view, i, l) -> {
            nsdConn.resolve(devs.getItem(i));
            mDevs.setClickable(false);
        });

        discover();
        myRefresh.setRefreshing(true);
    }

    public void nameHasCollided() {

        EditText edt_name = new EditText(this);
        edt_name.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT));

        edt_name.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(50)});

        AlertDialog.Builder new_name = new AlertDialog.Builder(this);
        new_name.setTitle("Opa, deu merda").setMessage("Há alguem com o mesmo nome na rede. É " +
                "melhor muda-lo: ").setPositiveButton("OK", (dialogInterface, i) -> {

            String naime = edt_name.getText().toString();

            if (naime.equals("")) {
                Toast.makeText(this, "Não deixe o nome vazio", Toast.LENGTH_SHORT).show();
                return;
            } else {
                nsdConn.register(naime);

                SharedPreferences.Editor ed = prefs.edit();
                ed.putString("name", naime);
                ed.apply();
            }
        }).setView(edt_name).setCancelable(false).create().show();
    }

    private void setLayout() {
        // Cria e seta um título à barra superior
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.drawer_messages);

        // Cria um Array e recupera a ListView
        mDevs = findViewById(R.id.posts);

        // Recupera o arquivo SharedPreferences
        prefs = getSharedPreferences(LoginActivity.LOGIN_PREFS, MODE_PRIVATE);

        // Não sei
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Obtém o menu lateral e seta um Listener.
        // Essa classe implementa NavigationView.OnNavigationItemSelectedListener
        // por isso o 'this'
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Recupera cores para setar no gradiente
        SharedPreferences preferences = getSharedPreferences(
                Tags.PREFERENCES, MODE_PRIVATE);

        int start = preferences.getInt("GradStart", Color.BLACK);
        int center = preferences.getInt("GradCenter", Color.BLACK);
        int end = preferences.getInt("GradEnd", Color.BLACK);
        int angle = Integer.parseInt(preferences.getString("GradOrientation", "1"));

        GradientDrawable.Orientation orientation = null;

        switch (angle) {

            case 1:
                orientation = GradientDrawable.Orientation.BL_TR;
                break;
            case 2:
                orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                break;
            case 3:
                orientation = GradientDrawable.Orientation.LEFT_RIGHT;
        }

        View headerView = navigationView.getHeaderView(0);

        // Recupera os TextView que estão na imagem da barra lateral
        userId = headerView.findViewById(R.id.drawer_user);
        userHost = headerView.findViewById(R.id.drawer_host);

        GradientDrawable gradientDrawable = new GradientDrawable(orientation
        , new int[] {start, center, end});

        headerView.setBackground(gradientDrawable);

        navigationView.setCheckedItem(R.id.nav_messages);
    }

    /**
    O método onBackPressed é padrão do Android e sempre é executado
    quando o botão de voltar é pressionado

    Aqui ele é modificado
     */
    @Override
    public void onBackPressed() {

        // Obtém o layout da barra lateral
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            // Se o menu estiver aberto ou os botões estiverem á vista, eles serão fechado
            drawer.closeDrawer(GravityCompat.START);
        }
        else
            // Se não, o método padrão é executado
            super.onBackPressed();
    }

    // Este método é executado quando alguma opção no painel lateral é escolhida
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Verifica qual foi a escolha
        switch (item.getItemId()) {

            case R.id.nav_messages:
                // TODO: 19/11/2017 Layout alá WhatsApp
                break;
            case R.id.discover_devices:
                break;
            case R.id.logout:
                // Desloga
                logOut();
                break;
            case R.id.drawer_preferences:
                // Inicia a tela de configurações
                startActivityForResult(new Intent(this, SettingsActivity.class), 100);
                break;
        }

        // Recupera o painel e o fecha
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void discover() {
        new Thread(() -> {

            try {
                Thread.sleep(3000);
                nsdConn.discover();
            } catch (Exception e) {
                MainPage.this.runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), R.string.refresh_alr_running, Toast.LENGTH_LONG)
                                .show());
            }
        }).start();
    }

    /**
     * Desloga o usuário
     */
    public void logOut() {

        // Cria um dialogo perguntando se o usuário tem certeza
        AlertDialog.Builder ald = new AlertDialog.Builder(this);
        ald.setMessage(R.string.confirm_ald_title);
        ald.setNeutralButton(R.string.no, null);
        // Define o botão positivo do dialogo e qual sua ação
        ald.setPositiveButton(R.string.yes, (dialog, which) -> {

            // Cria um Intent para a Activity LoginActivity
            final Intent it = new Intent(MainPage.this, LoginActivity.class);

            // Cria um editor para o arquivo SharedPreferences
            final SharedPreferences.Editor ed = prefs.edit();

            // Define que o usuário não está mais logado
            ed.putBoolean(PREF_LOG, false);

            // Aplica as ediçoes no arquivo e volta para a tela de login
            ed.apply();
            startActivity(it);
            finish();
        });
        ald.show();
    }

    /*
    Este método é padrão do Android e é executado quando uma Activity volta para
    uma tela anterior que a solicitou
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Chama o que o método faz por padrão
        super.onActivityResult(requestCode, resultCode, data);

        // Verifica se o resultado corresponde ao sucesso
        if (resultCode == 52) {
            Toast.makeText(this, R.string.post_sucess, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Cria as opções do menu
        getMenuInflater().inflate(R.menu.menu_page, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // Reinicia o serviço ao ser selecionado no menu
            case R.id.menu_restart:
                nsdConn.finishEverything();
                nsdConn = new NSDConnection(this);
                nsdConn.register(userId.getText().toString());

                Toast.makeText(this, "Reiniciando Serviço", Toast.LENGTH_LONG).show();
                discover();
        }

        return super.onOptionsItemSelected(item);
    }

    public void setDrawerText(String serviceName, String host) {
        runOnUiThread(() -> {
            userId.setText(serviceName);
            userHost.setText(host);
        });
    }

    @Override
    public void update(Observable observable, Object o) {

        if (o instanceof NsdServiceInfo) {

            NsdServiceInfo resolved = (NsdServiceInfo) o;

            Intent it = new Intent(this, ChatActivity.class);
            it.putExtra("ServiceInfo", resolved);
            it.putExtra("Me", nsdConn.si);

            startActivity(it);
        } else {

            ArrayAdapter<String> name = new ArrayAdapter<>(this
                    , android.R.layout.simple_list_item_1);

            devs = nsdConn.getDevices();

            for (int i=0; i < devs.getCount(); i++) {
                String serviceName = devs.getItem(i).getServiceName();

                if (serviceName != null) {
                    name.add(serviceName);
                    this.runOnUiThread(() -> {
                        mDevs.setAdapter(name);
                        Toast.makeText(getApplicationContext(), "Dispositivo Encontrado"
                                , Toast.LENGTH_LONG).show();
                    });
                } else
                    Log.e(Tags.LOG_TAG, "Nome do dispositivo nulo");
            }
        }
    }
}