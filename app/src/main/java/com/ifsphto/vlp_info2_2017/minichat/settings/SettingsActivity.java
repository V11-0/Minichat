package com.ifsphto.vlp_info2_2017.minichat.settings;

import android.os.Bundle;


/**
 * Created by
 * @author vinibrenobr11 on 20/08/2017 at 15:40:36
 *
 * Esta é a classe de Configurações do app
 * Essa classe funciona carregando Fragments sendo que elas
 * carregam os dados a serem exibidos
 *
 * @see android.app.Fragment
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Carrega o Fragment SettingsFragment
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsFragment()).commit();
    }
}
