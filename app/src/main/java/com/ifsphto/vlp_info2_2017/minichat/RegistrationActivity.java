package com.ifsphto.vlp_info2_2017.minichat;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ifsphto.vlp_info2_2017.minichat.connection.ConnectionClass;
import com.ifsphto.vlp_info2_2017.minichat.security.Encrypt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Activity para criar um novo usuário no MySQL<br>
 *     Ela é bem parecida com {@link LoginActivity}
 */
public class RegistrationActivity extends AppCompatActivity {

    // Campos de texto, suas string e botão para fazer cadastro
    private EditText edt_new_user;
    private EditText edt_new_email;
    private EditText edt_new_password;
    private EditText edt_confirm_password;
    private Button signup_button;
    private String user_name;
    private String email;
    private String password;

    private EditText error_exist_e = null;
    private String error_exist_s = null;

    // Barra e texto para indicar a força de uma nova senha
    private ProgressBar passwordStrong;
    private TextView strength_text;

    // View para indicar um erro
    private View focusView = null;

    private ConnectionClass connectionClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        connectionClass = new ConnectionClass();

        // Recupera todas as Views do Layout
        edt_new_user         = findViewById(R.id.edt_new_user);
        edt_new_email        = findViewById(R.id.edt_new_email);
        edt_new_password     = findViewById(R.id.edt_new_password);
        edt_confirm_password = findViewById(R.id.edt_confirm_password);

        passwordStrong       = findViewById(R.id.passwordStrong);
        strength_text        = findViewById(R.id.strength_text);

        // Obtém o que foi passado da outra atividade para essa
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            String login = bundle.getString("login");

            if (login != null) {

                // O texto passado pela outra atividade será setado em um dos campos
                // O texto passado será o que o usuário digitou para fazer login na outra
                // Atividade, se ele tiver digitado um

                if (login.contains("@") && login.contains("."))
                    edt_new_email.setText(login);
                else
                    edt_new_user.setText(login);
            }
            // Limpa a variável bundle
            bundle.clear();
        }

        /*
        Aqui temos um método que verifica as mudanças no campo
        senha, e vai dizendo se a senha que o usuário está digitando é
        fraca, média ou forte.
         */
        edt_new_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                // FIXME: 22/10/2017 Melhorar isso aqui

                int progress = 0;
                int color = 0;
                String pass = String.valueOf(s);

                if (pass.contains("!") || pass.contains("@") || pass.contains("#") || pass.contains("$") ||
                        pass.contains("%") || pass.contains("&") || pass.contains("*") || pass.contains("(")) {

                    progress += 40;
                }
                if (pass.contains("1") || pass.contains("2") || pass.contains("3") || pass.contains("4") || pass.contains("5") ||
                        pass.contains("6") || pass.contains("7") || pass.contains("8") || pass.contains("90") ||
                        pass.contains("0") ) {

                    progress += 40;
                }

                if (progress < 20) {
                    color = Color.RED;
                    strength_text.setText(getString(R.string.password_weak));
                }else if (progress < 50) {
                    color = Color.YELLOW;
                    strength_text.setText(getString(R.string.password_medium));
                }else if (progress >= 50) {
                    color = Color.GREEN;
                    strength_text.setText(getString(R.string.password_strong));
                }

                strength_text.setTextColor(color);
                passwordStrong.setProgress(progress);
                passwordStrong.setDrawingCacheBackgroundColor(color);
            }
        });

        // Recupera e define a ação ao botão ser pressionado
        signup_button = findViewById(R.id.user_sign_up);
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup_button.setClickable(false);
                createNewUser();
            }
        });

    }

    /**
     * Mesma coisa que a
     * @see LoginActivity , só que com mais campos
     */
    private void createNewUser() {

        edt_new_user.setError(null);
        edt_new_password.setError(null);

        user_name = edt_new_user.getText().toString();
        email            = edt_new_email.getText().toString();
        password         = edt_new_password.getText().toString();
        String cPassword = edt_confirm_password.getText().toString();

        boolean cancel = false;

        if (TextUtils.isEmpty(cPassword)) {
            edt_confirm_password.setError(getString(R.string.c_password));
            focusView = edt_confirm_password;
            cancel = true;
        }else if (!cPassword.equals(password)) {
            edt_confirm_password.setError(getString(R.string.error_c_password));
            focusView = edt_confirm_password;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            edt_new_password.setError(getString (R.string.error_field_required) );
            focusView = edt_new_password;
            cancel = true;
        }else if (password.length() > 70) {
            edt_new_password.setError(getString (R.string.error_password_lenght_max) );
            focusView = edt_new_password;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            edt_new_email.setError(getString(R.string.error_field_required));
            focusView = edt_new_email;
            cancel = true;
        }else if (email.length() > 254) {
            edt_new_email.setError(getString(R.string.error_email_lenght));
            focusView = edt_new_email;
            cancel = true;
        }
        else if (!email.contains("@") || !email.contains(".") || email.contains(" ")) {
            edt_new_email.setError(getString(R.string.error_email_invalid));
            focusView = edt_new_email;
            cancel = true;
        }

        if (TextUtils.isEmpty(user_name)) {
            edt_new_user.setError(getString(R.string.error_field_required));
            focusView = edt_new_user;
            cancel = true;
        }else if (user_name.length() > 70) {
            edt_new_user.setError(getString(R.string.error_user_length));
            focusView = edt_new_user;
            cancel = true;

        }else if (!cancel) {

            // Verifica se há algum caractere especial no texto digitado pela pessoa
            // usando chars

            for (int i = 32; i <= 127; i++) {

                if (user_name.contains(String.valueOf((char) i))) {
                    edt_new_user.setError(getString(R.string.error_user_special_chars));
                    focusView = edt_new_user;
                    cancel = true;
                    break;
                }

                // Os caracteres normais são pulados da verificação
                // por esses ifs
                if (i == 47)
                    i+=10;
                if (i == 64)
                    i+= 26;
                if (i == 96)
                    i += 26;

            }
        }

        if (cancel) {
            focusView.requestFocus();
            signup_button.setClickable(true);
        }

        else {

            progress(true);

            // A conexão é iniciada
            new CreateUser().execute("");
        }
    }

    /**
     * Exibe ou remove progresso
     * @param show true, para exibir, false para normalizar layout
     */
    private void progress(boolean show) {

        if (show) {
            findViewById(R.id.new_user_form).setVisibility(View.GONE);
            findViewById(R.id.prog_spinner_reg).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.new_user_form).setVisibility(View.VISIBLE);
            findViewById(R.id.prog_spinner_reg).setVisibility(View.GONE);
        }

    }

    private class CreateUser extends AsyncTask<String,String,String> {
        String z = "";
        boolean isSuccess = false;

        @Override
        protected void onPostExecute(String r) {

            AlertDialog.Builder ad = new AlertDialog.Builder(RegistrationActivity.this);
            if(r.length() < 40)
                ad.setTitle(r);
            else
                ad.setMessage(r);

            if(isSuccess) {

                /*
                   Cria um diálogo que retorna para a tela de login, e verifica
                   as preferencias do usuário, se a preferencia estiver ativada
                   os dados cadastrados aparecerão nos campos da tela de login
                 */
                SharedPreferences stg_pref = getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE);
                String pref = stg_pref.getString("fill_login", "0");
                Intent it = new Intent();

                switch (pref) {

                    case "1":
                        it.putExtra("result", email);
                        break;
                    case "2":
                        it.putExtra("result", user_name);
                        break;
                    default:
                        Log.v("Option is Disabled", "Field will stay empty");
                }
                setResult(LoginActivity.REQUEST_CODE_NEW_USER, it);
                finish();
            }
            else {

                progress(false);

                // Se ocorerer um erro, ele mostra ao usuário e volta para a tela
                // de cadastro
                if (error_exist_e != null) {

                    error_exist_e.setError(error_exist_s);
                    focusView.requestFocus();

                }else {

                    ad.setNeutralButton("OK", null);
                    ad.setIcon(android.R.drawable.ic_dialog_alert);
                    ad.show();
                }
            }

            signup_button.setClickable(true);
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                Connection con = connectionClass.conn(false);

                if (con == null)
                    z = connectionClass.getException();
                else {

                    user_name = user_name.replace("'", "''");
                    email = email.replace("'", "''");
                    password = Encrypt.encryptPass(password);

                    // Verifica se já existe um usuário com o mesmo nome
                    String query = "SELECT * FROM User WHERE Name ='" + user_name + "'";
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    if (rs.next()) {
                        error_exist_e = edt_new_user;
                        error_exist_s = getString(R.string.error_user_exists);
                        focusView = edt_new_user;
                        isSuccess = false;
                    }
                    else {

                        // Verifica se já existe um email igual
                        query = "SELECT * FROM User WHERE Email = '" + email + "'";
                        rs = stmt.executeQuery(query);

                        if (rs.next()) {
                            error_exist_e = edt_new_email;
                            error_exist_s = getString(R.string.error_email_exists);
                            focusView = edt_new_email;
                            isSuccess = false;
                        }
                        else {

                            // Insere os valores no MySQL
                            stmt.execute("INSERT INTO User (Name, Email, Password) VALUES " +
                                    "('" + user_name + "', '" + email + "', '" + password + "')");

                            z = getString(R.string.new_user_success);
                            isSuccess = true;
                        }
                    }
                }

            } catch (Exception ex) {
                isSuccess = false;
                z = ex.getMessage();
            }
            return z;
        }
    }
}