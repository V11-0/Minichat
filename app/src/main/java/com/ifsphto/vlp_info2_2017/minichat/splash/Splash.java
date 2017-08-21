package com.ifsphto.vlp_info2_2017.minichat.splash;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.ifsphto.vlp_info2_2017.minichat.MainActivity;
import com.ifsphto.vlp_info2_2017.minichat.R;
import com.ifsphto.vlp_info2_2017.minichat.page.MainPage;

/**
 * Created by vinibrenobr11 on 03/03/2017 at 00:54
 *
 * Essa Classe que tem a função de gerenciar e exibir tela
 * de "Splash", ou de apresentação no início da execução
 * do app, ultilizando uma interface @{@link Runnable}
 *
 * @author vinibrenobr11 on 15/08/2017 at 19:28
 */
public class Splash extends Activity implements Runnable {

    // Intent usado para iniciar a primeira tela
    private Intent it;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        // TODO 14/08/2017: Icónes pretos na barra de navegação para Android O

        /*
        * Cria um SharedPreferences, e verifica se o usuário está
        * com login ativo ou não, e este valor é repassado a um boolean
        */
        SharedPreferences prefs = getSharedPreferences(MainActivity.LOGIN_PREFS, MODE_PRIVATE);
        boolean isLogged = prefs.getBoolean(MainPage.PREF_LOG, false);

        // Se o usuário já estiver logado, o app se inicia na "Tela inicial"
        // Onde ele verá os posts de outros usuários

        // Se não, o app iniciará na tela para ele fazer o login
        if(isLogged)
            it = new Intent(this, MainPage.class);
        else
            it = new Intent(this, MainActivity.class);

        // Espera 3 sec para executar o método run()
        // e ir para a próxima tela
        Handler handler = new Handler();
        handler.postDelayed(this, 3000);
    }

    // Método padrão da interface Runnable
    @Override
    public void run() {
        startActivity(it);
        finish();
    }
}