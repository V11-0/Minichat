package com.ifsphto.vlp_info2_2017.minichat.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.ifsphto.vlp_info2_2017.minichat.R;

/**
 * Created by vinibrenobr11 on 20/08/2017 at 15:41:26
 */
public class SettingsFragment extends PreferenceFragment {

    /**
     * Adiciona as configurações pelo xml
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Adiciona as configurações do xml
        addPreferencesFromResource(R.xml.preferences);
    }
}
