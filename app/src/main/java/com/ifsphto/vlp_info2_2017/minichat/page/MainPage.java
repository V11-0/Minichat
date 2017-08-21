package com.ifsphto.vlp_info2_2017.minichat.page;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ifsphto.vlp_info2_2017.minichat.MainActivity;
import com.ifsphto.vlp_info2_2017.minichat.R;
import com.ifsphto.vlp_info2_2017.minichat.connection.ConnectionClass;
import com.ifsphto.vlp_info2_2017.minichat.connection.DownloadService;
import com.ifsphto.vlp_info2_2017.minichat.connection.UpdateConnection;
import com.ifsphto.vlp_info2_2017.minichat.object.Post;
import com.ifsphto.vlp_info2_2017.minichat.page.adapters.MyBaseAdapter;
import com.ifsphto.vlp_info2_2017.minichat.settings.SettingsActivity;

/**
 * Essa classe é, por enquanto a maior do projeto, ela é a pagina inicial
 * onde o usuário vê os posts de outros usuários
 *
 * Nessa classe principalmente iremos implementar o P2P
 * e também iniciar um serviço que faça download de uma versão atualixada do app
 */

public class MainPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    /*
     Nome do campo dentro do arquivo shared preferences
     ele diz se o usuário está logado atraves de um boolean
     essa string diz o nome desse campo
     */
    public static final String PREF_LOG = "isLoggedIn";

    // Objeto representando o arquivo SharedPreferences
    private SharedPreferences prefs;

    // Botões flutuantes
    private FloatingActionMenu fab_menu;
    private FloatingActionButton new_msg;

    // Listas e Adapters
    private List<Post> posts;
    private ListView mPosts;
    private MyBaseAdapter adp_posts;

    // Conexão
    private ConnectionClass connectionClass = new ConnectionClass();

    // Dialogos, de progresso de Download e de "Obtendo Posts"
    private ProgressDialog pd;
    private ProgressDialog upda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);

        // Cria e seta um título à barra superior
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_user));

        // Cria um Array e recupera a ListView
        posts = new ArrayList<>();
        mPosts = (ListView) findViewById(R.id.posts);

        // Recupera o arquivo SharedPreferences
        prefs = getSharedPreferences(MainActivity.LOGIN_PREFS, MODE_PRIVATE);

        // Cria e seta os atributos do Dialogo
        pd = new ProgressDialog(this);

        pd.incrementProgressBy(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(getString(R.string.load_posts));
        pd.setCancelable(false);

        // Exibe o dialogo
        pd.show();

        // Obtém os 3 botões flutuantes, 2 que são ativados ao clicar no maior
        fab_menu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        FloatingActionButton new_post = (FloatingActionButton) findViewById(R.id.new_post);
        new_msg = (FloatingActionButton) findViewById(R.id.new_msg);

        /*
        Aqui é setado que ao clicar fora dos botões enquanto eles estiverem visíveis,
        eles serão fechados.

        Essa classe implementa a interface View.OnClickListener
        por isso o parâmetro 'this' é passado
         */
        fab_menu.setClosedOnTouchOutside(true);
        new_post.setOnClickListener(this);
        new_msg.setOnClickListener(this);

        // Não sei
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Obtém o menu lateral e seta um Listener.
        // Essa classe implementa NavigationView.OnNavigationItemSelectedListener
        // por isso o 'this'
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        // Recupera os TextView que estão na imagem da barra lateral
        TextView userId = headerView.findViewById(R.id.UserId);
        TextView userEmail = headerView.findViewById(R.id.UserEmail);

        // Obtém o Adapter dos Posts
        adp_posts = new MyBaseAdapter(posts, this, R.layout.post_item);

        // Recupera os Posts
        GetPosts getPosts = new GetPosts();
        getPosts.execute("");

        // Seta o código a ser executado quando o dialogo "obtendo posts" for fechado
        pd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // Seta o adapter na ListView
                mPosts.setAdapter(adp_posts);
                // Seta um subtítulo na barra superior com o número de posts
                toolbar.setSubtitle(posts.size() + " " + getString(R.string.posts));
            }
        });

        // Seta o nome de usuario e email nos TextView da barra lateral,
        // obtido atráves do arquivo SharedPreferences
        userId.setText(prefs.getString("name", "Error"));
        userEmail.setText(prefs.getString("email", "Error"));
    }

    /*
    O método onBackPressed é padrão do Android e sempre é executado
    quando o botão de voltar é pressionado

    Aqui ele é modificado
     */
    @Override
    public void onBackPressed() {

        // Obtém o layout da barra lateral
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START) || fab_menu.isOpened()) {
            // Se o menu estiver aberto ou os botões estiverem á vista, eles serão fechado
            drawer.closeDrawer(GravityCompat.START);
            fab_menu.close(true);
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
                // TODO: Abrir layout das mensagens, tipo WhatsApp
                break;
            case R.id.logout:
                // Desloga
                logOut();
                break;
            case R.id.check_update_drawer:
                // Verifica Atualização
                verifyUpdate();
                break;
            case R.id.drawer_preferences:
                startActivityForResult(new Intent(this, SettingsActivity.class), 100);
                break;
        }

        // Recupera o painel e o fecha
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void verifyUpdate() {

        // Seta e mostra dialogo 'verificando por atualizações'
        upda = new ProgressDialog(this);
        upda.setMessage(getString(R.string.verifying));
        upda.setCancelable(false);
        upda.incrementProgressBy(ProgressDialog.STYLE_SPINNER);
        upda.show();

        // Executa a classe update
        Update up = new Update();
        up.execute("");
    }

    // Desloga o usuário
    public void logOut() {

        // Cria um dialogo perguntando se o usuário tem certeza
        AlertDialog.Builder ald = new AlertDialog.Builder(this);
        ald.setMessage(getString(R.string.confirm_ald_title));
        ald.setNeutralButton(getString(R.string.no), null);
        // Define o botão positivo do dialogo e qual sua ação
        ald.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Cria um Intent para a Activity MainActivity
                final Intent it = new Intent(MainPage.this, MainActivity.class);

                // Cria um editor para o arquivo SharedPreferences
                final SharedPreferences.Editor ed = prefs.edit();

                // Define que o usuário não está mais logado
                ed.putBoolean(PREF_LOG, false);

                // Aplica as ediçoes no arquivo e volta para a tela de login
                ed.apply();
                startActivity(it);
                finish();
            }
        });
        ald.show();
    }

    @Override
    public void onClick(View v) {

        Intent it = new Intent(this, SharingActivity.class);

        // Verifica quem chamou o método, e define dados no Intent
        if (v == new_msg)
            it.putExtra("tab", 1);
        else
            it.putExtra("tab", 0);

        /*
        Inicia a SharingActivity com os dados da aba escolhida
        esperando por um resultado ao retornar
         */
        startActivityForResult(it, 50);
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

            // Limpa a lista, redefine o Adapter, mostra dialogo "obtendo posts" e os recupera
            posts.clear();
            adp_posts = new MyBaseAdapter(posts, this, R.layout.post_item);
            pd.show();
            GetPosts getPosts = new GetPosts();
            getPosts.execute("");

        }
    }

    private class GetPosts extends AsyncTask<String,String,String> {
        String z = "";
        ResultSet rs;
        boolean isSuccess = false;

        AlertDialog.Builder dlg;
        Connection con;

        @Override
        protected void onPostExecute(String s) {

            try {

                dlg.setMessage(z);

                // Obtém os posts e os adicionam na List
                if (isSuccess) {
                    rs.first();
                    int i = 0;

                    while (true) {
                        if (i != 0)
                            rs.next();
                        Post post = new Post(rs.getString(1), rs.getString(2), rs.getString(3));
                        posts.add(i, post);
                        i++;
                        Log.v("Add", "Post " + i);
                        if (rs.isLast()) {
                            pd.dismiss();
                            break;
                        }
                    }
                    con.close();
                } else
                    dlg.show();

            } catch (Exception e) {
                e.printStackTrace();
                pd.dismiss();
                dlg.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            dlg = new AlertDialog.Builder(MainPage.this);
            dlg.setNeutralButton("OK", null);

            try {

                con = connectionClass.conn();

                if (con == null)
                    z = connectionClass.getException();
                else {
                    String query = "SELECT * FROM poststbl ORDER BY Date DESC";
                    Statement stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                }

                if (rs.next())
                    isSuccess = true;
                else
                    pd.dismiss();

            } catch (Exception ex) {
                pd.dismiss();
                z = ex.getMessage();
                ex.printStackTrace();
            }
            return z;
        }
    }

    private class Update extends AsyncTask<String,String,String> {

        String z = "";
        ResultSet rs;
        boolean isSuccess = false;

        AlertDialog.Builder dlg;
        AlertDialog.Builder down;
        Connection con;
        Statement stmt;

        @Override
        protected void onPostExecute(String s) {

            if (isSuccess) {

                try {

                    rs.first();
                    int ver = rs.getInt(1);

                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    int verCode = pInfo.versionCode;

                    if (ver > verCode) {

                        upda.dismiss();

                        dlg = new AlertDialog.Builder(MainPage.this);

                        dlg.setTitle(getString(R.string.update_av_title))
                                .setMessage(getString(R.string.update_av_msg)
                                        + getString(R.string.dlg_this_ver) + " " + pInfo.versionName +
                                        getString(R.string.dlg_new_ver) + " " + rs.getString(2))
                                .setNeutralButton(getString(R.string.not_now), null)
                                .setPositiveButton(getString(R.string.ofcourse), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent down = new Intent(MainPage.this, DownloadService.class);
                                        startService(down);

                                    }
                                }).show();

                    } else {
                        upda.dismiss();
                        Toast.makeText(MainPage.this, R.string.no_updates, Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    upda.dismiss();
                    dlg.show();
                } finally {
                    upda = null;
                    dlg = null;
                    rs = null;
                    stmt = null;
                    con = null;
                    down = null;
                }
            } else
                Toast.makeText(MainPage.this, "ERRRROU", Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... params) {

            dlg = new AlertDialog.Builder(MainPage.this);
            dlg.setNeutralButton("OK", null);

            try {

                con = new UpdateConnection().conn();

                if (con == null)
                    z = connectionClass.getException();
                else {
                    stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    rs = stmt.executeQuery("SELECT Version_Code,Version FROM att");
                }
                isSuccess = rs.next();

            } catch (Exception ex) {
                upda.dismiss();
                z = ex.getMessage();
                ex.printStackTrace();
            }
            return z;
        }
    }

}