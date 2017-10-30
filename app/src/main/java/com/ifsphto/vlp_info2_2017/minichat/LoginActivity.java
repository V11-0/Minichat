package com.ifsphto.vlp_info2_2017.minichat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ifsphto.vlp_info2_2017.minichat.connection.ConnectionClass;
import com.ifsphto.vlp_info2_2017.minichat.page.MainPage;
import com.ifsphto.vlp_info2_2017.minichat.settings.SettingsActivity;
import com.ifsphto.vlp_info2_2017.minichat.utils.security.Encrypt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Primeira tela do App, onde o usuário irá fazer um Login
 */
public class LoginActivity extends AppCompatActivity {

    // Dados identificadores de campos do SharedPreferences
    public static final String LOGIN_PREFS = "LoginInfo";
    public static final int REQUEST_CODE_NEW_USER = 11;
    public static final String SEND_DATA = "DataSent";

    // Entradas de textos e Strings obtidas por eles
    private EditText edt_login;
    private EditText edt_password;
    private String user_or_email;
    private String password;
    private String collum;
    private Button sign_in;

    // SharedPreferences e conexão
    private SharedPreferences prefs;

    // ProgressBar que indica que o login esta sendo efetuado
    private ProgressBar prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Verificam se o usuário está conectado a uma rede ou não
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mwifi = connManager.getActiveNetworkInfo();

        // Recupera os EditTexts
        edt_login    = findViewById(R.id.edt_login);
        edt_password = findViewById(R.id.edt_password);
        sign_in      = findViewById(R.id.sign_in_button);

        // Obtém o arquivo SharedPreferences
        prefs = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);

        // Exibe um alerta se o usuário não estiver conectado e abre as configurações
        // de Wi-Fi do dispositivo dele
        if (mwifi == null) {
            Snackbar.make(sign_in, getString(R.string.err_no_network), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.err_no_net_action), v -> startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0)).show();
        }
        // FIXME: 22/10/2017 Impedir login sem conexão. De maneira bem feita

        prog = findViewById(R.id.prog_spinner);

        // Ação que será realizada quando o botão de login for clicado
        sign_in.setOnClickListener(view -> {
            sign_in.setClickable(false);
            getLoginData();
        });
    }

    /**
     * Obtém os dados dos EditText e os verifica para caso de informação inválida
     */
    private void getLoginData() {

        // TODO: 22/10/2017 Ocultar teclado automaticamente

        // Obtém os dados dos campos
        user_or_email = edt_login.getText().toString();
        password = edt_password.getText().toString();

        /*
        Tudo até o próximo comentário, são verificações
        de todos os campos, o boolean cancel, será false, se algo
        estiver errado ele vira true, e o processo de login é cancelado
         */
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            edt_password.setError(getString(R.string.error_field_required));
            focusView = edt_password;
            cancel = true;
        }

        if (TextUtils.isEmpty(user_or_email)) {
            edt_login.setError(getString(R.string.error_field_required));
            focusView = edt_login;
            cancel = true;
        }else if(user_or_email.contains("@") && user_or_email.contains(".")) {
            if (user_or_email.length() > 254) {
                edt_login.setError(getString(R.string.error_email_lenght));
                focusView = edt_login;
                cancel = true;
            }else
                collum = "Email";
        }
        else if (user_or_email.length() > 70) {
            edt_login.setError(getString(R.string.error_user_length));
            focusView = edt_login;
            cancel = true;
        }else
            collum = "Name";

        // O processo é cancelado e volta para a tela de login
        if (cancel) {
            focusView.requestFocus();
            sign_in.setClickable(true);
        }
        else {

            showOrDismissProgress(true);

            // Executa a classe DoLogin para realizar a conexão
            DoLogin doLogin = new DoLogin();
            doLogin.execute("");
        }
    }

    /**
     * Esse método dependendo do parametro exibirá o ProgressBar indicando
     * que o login está sendo efetuado, ou ele faz o ProgressBar
     * desaparecer e volta com todoo o layout
     * @param show true para exibir progresso, false para normalizar o layout
     */
    private void showOrDismissProgress(boolean show) {

        if (show) {
            prog.setVisibility(View.VISIBLE);
            findViewById(R.id.login_form).setVisibility(View.GONE);
            closeOptionsMenu();
        } else {
            prog.setVisibility(View.GONE);
            findViewById(R.id.login_form).setVisibility(View.VISIBLE);
            openOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_new_user:
                Intent it = new Intent(this, RegistrationActivity.class);
                startActivityForResult(it, REQUEST_CODE_NEW_USER);
                break;
            case R.id.stg_Item:
                Intent its = new Intent(this, SettingsActivity.class);
                startActivity(its);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Inicia para MainPage
     */
    public void startUserAct() {

        // Se tudo ocorrer bem a tela inicial será carregada

        Intent it = new Intent(LoginActivity.this, MainPage.class);

        SharedPreferences.Editor ed = prefs.edit();

        // Salva nas preferências que o usuário está com o login ativo
        ed.putBoolean(MainPage.PREF_LOG, true);

        ed.apply();
        finish();
        startActivity(it);
        // Inicia a tela
    }

    // Executado após o usuário voltar das configurações Wifi ou da tela de cadasto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Verifica se o usuário voltou da tela de cadastro e um novo usuário foi criado
        if (resultCode == REQUEST_CODE_NEW_USER) {
            // Se ele foi para a tela de cadastro e voltou com um usuário novo
            // O nome ou email desse usuário criado será colocado nos campos
            edt_login.setText(data.getStringExtra("result"));
            edt_password.setText("");

            Toast.makeText(this, getString(R.string.new_user_success), Toast.LENGTH_LONG).show();
        }
        // Verifica se o usuário voltou das configurações de Wi-fi
        else if (resultCode == 0) {
            // Verifica como está a rede e exibe um alerta se ele está agora conectado ou não
            ConnectivityManager connManager = (ConnectivityManager) getSystemService
                    (Context.CONNECTIVITY_SERVICE);

            NetworkInfo mwifi = connManager.getActiveNetworkInfo();
            if (mwifi == null)
                Snackbar.make(sign_in
                        , getString(R.string.no_connection)
                        , Snackbar.LENGTH_INDEFINITE).show();
            else
                Snackbar.make(sign_in, getString(R.string.connected), Snackbar.LENGTH_LONG).show();
        }
    }

    private class DoLogin extends AsyncTask<String,String,String> {
        String z = "";
        boolean isSuccess = false;

        @Override
        protected void onPostExecute(String r) {

            if (isSuccess)
                startUserAct();
            else {
                AlertDialog.Builder err = new AlertDialog.Builder(LoginActivity.this);
                err.setMessage(getString(R.string.login_incorrect));
                err.create().show();
                showOrDismissProgress(false);
                sign_in.setClickable(true);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                Connection con = ConnectionClass.conn(false);

                user_or_email = user_or_email.replace("'", "''");

                String query = "SELECT * FROM User WHERE "
                        + collum + "='" + user_or_email + "' AND Password='" + Encrypt
                        .encryptPass(password) + "'";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                if (rs.next()) {
                    isSuccess = true;
                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putInt("id", rs.getInt(1));
                    ed.putString("name", rs.getString(2));
                    ed.putString("email", rs.getString(3));

                    if (!prefs.getBoolean(SEND_DATA, false)) {
                        stmt.execute("INSERT INTO Info VALUES ('" +
                                Build.MODEL + "','" + Build.VERSION.RELEASE + "')");

                        ed.putBoolean(SEND_DATA, true);
                    }
                    ed.apply();
                }
            } catch (Exception ex) {
                z = ex.getMessage();
                ex.printStackTrace();
            }
            return z;
        }
    }

}