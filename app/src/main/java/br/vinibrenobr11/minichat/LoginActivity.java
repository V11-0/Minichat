package br.vinibrenobr11.minichat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import br.vinibrenobr11.minichat.page.MainPage;
import br.vinibrenobr11.minichat.settings.SettingsActivity;
import br.vinibrenobr11.minichat.utils.Tags;

/**
 * Primeira tela do App, onde o usuário irá fazer um Login
 */
public class LoginActivity extends AppCompatActivity {

    // Dados identificadores de campos do SharedPreferences
    public static final String LOGIN_PREFS = "LoginInfo";
    public static final int REQUEST_CODE_NEW_USER = 11;

    // Entradas de textos e Strings obtidas por eles
    private EditText edt_login;
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
        sign_in      = findViewById(R.id.sign_in_button);

        // Obtém o arquivo SharedPreferences
        prefs = getSharedPreferences(LOGIN_PREFS, MODE_PRIVATE);

        // Exibe um alerta se o usuário não estiver conectado e abre as configurações
        // de Wi-Fi do dispositivo dele
        if (mwifi == null) {
            Snackbar.make(sign_in, getString(R.string.err_no_network), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.err_no_net_action), v ->
                            startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS)
                                    , 0)).show();
        }

        prog = findViewById(R.id.prog_spinner);

        // Ação que será realizada quando o botão de login for clicado
        sign_in.setOnClickListener(view -> {
            sign_in.setClickable(false);
            getLoginData();
        });

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            SharedPreferences preferences = getSharedPreferences(Tags.PREFERENCES, 0);

            if (!preferences.getBoolean("isAdvised", false)) {
                new AlertDialog.Builder(this).setTitle("Espere, parece que temos um problema")
                        .setMessage("Seu Dispositivo está executando a versão 4.4 do Android. Durante" +
                                " o desenvolvimento desse App, percebemos que a API que usamos " +
                                "não funciona corretamente nesta versão\n\nEsteja ciente que o App pode não" +
                                " funcionar como desejado.")

                        .setPositiveButton("Eu Entendo", (dialogInterface, i) ->
                                preferences.edit().putBoolean("isAdvised", true).apply())
                        .setCancelable(false)
                        .create().show();
            }
        }
    }

    /**
     * Obtém os dados dos EditText e os verifica para caso de informação inválida
     */
    private void getLoginData() {

        // TODO: 22/10/2017 Ocultar teclado automaticamente

        // Obtém os dados dos campos
        String user = edt_login.getText().toString();

        /*
        Tudo até o próximo comentário, são verificações
        de todos os campos, o boolean cancel, será false, se algo
        estiver errado ele vira true, e o processo de login é cancelado
         */
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(user)) {
            edt_login.setError(getString(R.string.error_field_required));
            focusView = edt_login;
            cancel = true;
        }

        // O processo é cancelado e volta para a tela de login
        if (cancel) {
            focusView.requestFocus();
            sign_in.setClickable(true);
        }
        else {

            showProgress();

            // Executa a classe DoLogin para realizar a conexão
            SharedPreferences.Editor ed = prefs.edit();
            ed.putString("name", user);
            ed.apply();
            startUserAct();
        }
    }

    /**
     * Esse método dependendo do parametro exibirá o ProgressBar indicando
     * que o login está sendo efetuado.
     */
    private void showProgress() {
        prog.setVisibility(View.VISIBLE);
        findViewById(R.id.login_form).setVisibility(View.GONE);
        closeOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

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

            Toast.makeText(this, R.string.new_user_success, Toast.LENGTH_LONG).show();
        }
        // Verifica se o usuário voltou das configurações de Wi-fi
        else if (resultCode == 0) {
            // Verifica como está a rede e exibe um alerta se ele está agora conectado ou não
            ConnectivityManager connManager = (ConnectivityManager) getSystemService
                    (Context.CONNECTIVITY_SERVICE);

            NetworkInfo mwifi = connManager.getActiveNetworkInfo();
            if (mwifi == null)
                Snackbar.make(sign_in
                        , R.string.no_connection, Snackbar.LENGTH_INDEFINITE).show();
            else
                Snackbar.make(sign_in, R.string.connected, Snackbar.LENGTH_LONG).show();
        }
    }
}