package com.ifsphto.vlp_info2_2017.minichat.settings;

import android.app.AlertDialog;
import android.os.Bundle;

/**
 * Created by vinibrenobr11 on 20/08/2017 at 15:40:36<br></br>
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

        String log = getIntent().getStringExtra("message");

        if (log != null)
            showDetailDialog(log).show();
    }

    private AlertDialog showDetailDialog(String message) {

        AlertDialog.Builder dlg_log = new AlertDialog.Builder(this);
        dlg_log.setTitle("Detalhes do Erro");
        dlg_log.setMessage(message);
        dlg_log.setNegativeButton("Ok", null);

        return dlg_log.create();
    }

}
