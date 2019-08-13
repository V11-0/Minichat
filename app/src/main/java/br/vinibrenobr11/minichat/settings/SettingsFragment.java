package br.vinibrenobr11.minichat.settings;

import android.app.AlertDialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import br.vinibrenobr11.minichat.BuildConfig;
import br.vinibrenobr11.minichat.R;
import br.vinibrenobr11.minichat.utils.settings.VerifyUpdate;

/**
 * Created by vinibrenobr11 on 20/08/2017 at 15:41:26
 */
public class SettingsFragment extends PreferenceFragment {

    /**
     * Adiciona as configurações pelo xml
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Adiciona as configurações do xml
        addPreferencesFromResource(R.xml.preferences);

        // Obtém uma das preferencias e seta um Listener e um sumário formatado
        Preference update = findPreference("pref_update");
        update.setSummary(getString(R.string.pref_update_summary, BuildConfig.VERSION_NAME));
        update.setOnPreferenceClickListener(preference -> {

            update.setEnabled(false);

            android.view.View v = getView();

            assert v != null;
            Snackbar.make(v, R.string.verifying, Snackbar.LENGTH_SHORT).show();

            new Thread(() -> {

                android.app.Activity act = getActivity();

                final Object o = VerifyUpdate.verify(act);

                act.runOnUiThread(() -> {
                    if (o instanceof AlertDialog.Builder)
                        ((AlertDialog.Builder) o).create().show();
                    else if (o.equals("0"))
                        Toast.makeText(act, R.string.up_to_date, Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(act, o.toString(), Toast.LENGTH_LONG).show();

                    update.setEnabled(true);
                });
            }).start();

            return false;
        });
    }
}
