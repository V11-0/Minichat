package com.ifsphto.vlp_info2_2017.minichat;  // TODO: 02/03/2017  Encontrar e alterar ícones do projeto

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ifsphto.vlp_info2_2017.minichat.connection.ConnectionClass;
import com.ifsphto.vlp_info2_2017.minichat.page.MainPage;
import com.ifsphto.vlp_info2_2017.minichat.settings.SettingsActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Primeira tela do App, onde o usuário irá fazer um Login
 */
public class MainActivity extends AppCompatActivity {

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

    // SharedPreferences
    private SharedPreferences prefs;
    private ConnectionClass connectionClass;

    private ProgressDialog dlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Verificam se o usuário está conectado a uma rede ou não
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mwifi = connManager.getActiveNetworkInfo();

        // Cria a Classe ConnectionClass e a atribui ao objeto
        connectionClass = new ConnectionClass();

        // Recupera os EditTexts
        edt_login    = (EditText) findViewById(R.id.edt_login);
        edt_password = (EditText) findViewById(R.id.edt_password);
        sign_in      = (Button) findViewById(R.id.sign_in_button);

        // Obtém o arquivo SharedPreferences
        prefs = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);

        // Exibe um alerta se o usuário não estiver conectado e abre as configurações
        // de Wi-Fi do dispositivo dele
        if (mwifi == null) {
            Snackbar.make(sign_in, getString(R.string.err_no_network), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.err_no_net_action), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
                        }
                    }).show();
        }

        // Ação que será realizada quando o botão de login for clicado
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sign_in.setClickable(false);
                getLoginData();
            }
        });
    }

    private void getLoginData() {

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
            collum = "UserId";

        // O processo é cancelado e volta para a tela de login
        if (cancel) {
            focusView.requestFocus();
            sign_in.setClickable(true);
        }
        else {

            // Se chegar até aqui tudo está OK e um dialogo é criado
            dlg = new ProgressDialog(this);

            dlg.incrementProgressBy(ProgressDialog.STYLE_SPINNER);
            dlg.setCancelable(false);
            dlg.setTitle(getString(R.string.checking_data));
            dlg.setMessage(getString(R.string.pls_wait));
            dlg.show();

            // Executa a classe DoLogin para realizar a conexão
            DoLogin doLogin = new DoLogin();
            doLogin.execute("");
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
                Intent it = new Intent(this, NewUserActivity.class);
                startActivityForResult(it, REQUEST_CODE_NEW_USER); // Não coloca o finish aqui, já ta certo
                break;
            case R.id.stg_Item:
                Intent its = new Intent(this, SettingsActivity.class);
                startActivity(its);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startUserActWPD() {

        // Se tudo ocorrer bem a tela inicial será carregada

        Intent it = new Intent(MainActivity.this, MainPage.class);

        dlg.dismiss();

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
        }

        // Verifica se o usuário voltou das configurações de Wi-fi
        if (resultCode == 0) {
            // Verifica como está a rede e exibe um alerta se ele está agora conectado ou não
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mwifi = connManager.getActiveNetworkInfo();
            if (mwifi == null)
                Snackbar.make(sign_in, getString(R.string.no_connection), Snackbar.LENGTH_INDEFINITE).show();
            else
                Snackbar.make(sign_in, getString(R.string.connected), Snackbar.LENGTH_LONG).show();
        }
    }

    private class DoLogin extends AsyncTask<String,String,String> {
        String z = "";
        boolean isSuccess = false;
        boolean user_non_exists;

        @Override
        protected void onPostExecute(String r) {

            AlertDialog.Builder al = new AlertDialog.Builder(MainActivity.this);
            al.setIcon(android.R.drawable.ic_dialog_alert);
            al.setTitle(getString(R.string.error_login_title));
            al.setMessage(r);
            if (user_non_exists) {
                al.setNeutralButton(getString(R.string.return_new_user_dlgbutton), null);
                al.setPositiveButton(getString(R.string.create_new_user_dlgbutton), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent it = new Intent(MainActivity.this, NewUserActivity.class);
                        it.putExtra("login", user_or_email);
                        startActivityForResult(it, REQUEST_CODE_NEW_USER);
                    }
                });
            }else
                al.setNeutralButton("OK", null);

            if (isSuccess)
                startUserActWPD();
            else {
                dlg.dismiss();
                al.show();
                sign_in.setClickable(true);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                Connection con = connectionClass.conn();

                if (con == null)
                    z = connectionClass.getException();
                else {
                    String query = "select * from Usertbl where "
                            + collum + "='" + user_or_email + "' and Password='" + password + "'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    if (rs.next()) {
                        isSuccess = true;
                        SharedPreferences.Editor ed = prefs.edit();
                        ed.putString("name", rs.getString(1));
                        ed.putString("email", rs.getString(2));
                        if (!prefs.getBoolean(SEND_DATA, false)) {
                            stmt.execute("INSERT INTO Info VALUES ('" + Build.MODEL + "','" + Build.VERSION.RELEASE + "')");
                            ed.putBoolean(SEND_DATA, true);
                        }
                        ed.apply();
                    }
                    else {
                        query = "select * from Usertbl where "
                                + collum + "='" + user_or_email + "'";
                        rs = stmt.executeQuery(query);
                        if(rs.next())
                            z = getString(R.string.wrong_password);
                        else {
                            z = getString(R.string.user_non_exists);
                            user_non_exists = true;
                        }
                    }
                }
            } catch (Exception ex) {
                z = ex.getMessage();
                ex.printStackTrace();
            }
            return z;
        }
    }

}