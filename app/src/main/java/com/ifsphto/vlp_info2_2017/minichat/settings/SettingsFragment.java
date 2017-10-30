package com.ifsphto.vlp_info2_2017.minichat.settings;

import android.app.AlertDialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.ifsphto.vlp_info2_2017.minichat.BuildConfig;
import com.ifsphto.vlp_info2_2017.minichat.R;
import com.ifsphto.vlp_info2_2017.minichat.utils.settings.VerifyUpdate;

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

            Snackbar.make(getView(), R.string.verifying, Snackbar.LENGTH_SHORT).show();

            new Thread(() -> {

                final Object o = VerifyUpdate.verify(getActivity());

                getActivity().runOnUiThread(() -> {
                    if (o instanceof AlertDialog.Builder)
                        ((AlertDialog.Builder) o).create().show();
                    else if (o.toString().equals("0"))
                        Toast.makeText(getActivity(), R.string.up_to_date, Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getActivity(), o.toString(), Toast.LENGTH_LONG).show();

                    update.setEnabled(true);
                });
            }).start();

            return false;
        });
    }
}
