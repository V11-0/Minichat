package com.ifsphto.vlp_info2_2017.minichat.page;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ifsphto.vlp_info2_2017.minichat.R;
import com.ifsphto.vlp_info2_2017.minichat.utils.adapters.MyFragmentPagerAdapter;

/**
 * Essa classe gerencia a tela onde há duas abas aba de Posts e de mensagens
 */

public class SharingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);

        Toolbar toolbar = findViewById(R.id.toolbar_share);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtém o que foi passado da outra atividade para essa
        Bundle bundle = getIntent().getExtras();

        // O Layout e o gerenciador das abas são recuperados
        TabLayout mTabLayout = findViewById(R.id.tabs);
        ViewPager mViewPager = findViewById(R.id.container);

        mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(),
                getResources().getStringArray(R.array.tab_title)));

        mTabLayout.setupWithViewPager(mViewPager);

        // Seta a pagina atual pelo que foi escolhido pelo usuário
        if (bundle != null)
            mViewPager.setCurrentItem(bundle.getInt("tab"));
    }

}