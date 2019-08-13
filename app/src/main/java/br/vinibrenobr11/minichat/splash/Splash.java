package br.vinibrenobr11.minichat.splash;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import br.vinibrenobr11.minichat.LoginActivity;
import br.vinibrenobr11.minichat.R;
import br.vinibrenobr11.minichat.page.MainPage;

/**
 * Created by vinibrenobr11 on 03/03/2017 at 00:54<br></br>
 *
 * Essa Classe que tem a função de gerenciar e exibir tela
 * de "Splash", ou de apresentação no início da execução
 * do app, ultilizando uma interface @{@link Runnable}
 */
public class Splash extends Activity implements Runnable {

    // Intent usado para iniciar a primeira tela
    private Intent it;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        // Deixa os ícones da barra de navegação pretos para Android Oreo ou superior
        if (Build.VERSION.SDK_INT >= 26) {
            int flags = getWindow().getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }

        /*
        * Cria um SharedPreferences, e verifica se o usuário está
        * com login ativo ou não, e este valor é repassado a um boolean
        */
        SharedPreferences prefs = getSharedPreferences(LoginActivity.LOGIN_PREFS, MODE_PRIVATE);
        boolean isLogged = prefs.getBoolean(MainPage.PREF_LOG, false);

        /*
         * Se o usuário já estiver logado, o app se inicia na "Tela inicial"
         * Onde ele verá os posts de outros usuários
         * Se não, o app iniciará na tela para ele fazer o login
         */
        it = isLogged? new Intent(this, MainPage.class) : new Intent(this, LoginActivity.class);

        // Espera 3 sec para executar o método run()
        // e ir para a próxima tela
        Handler handler = new Handler();
        handler.postDelayed(this, 3000);
    }

    /**
     * Método da interface Runnable que inicia a Activity especificada
     */
    @Override
    public void run() {
        startActivity(it);
        finish();
    }
}