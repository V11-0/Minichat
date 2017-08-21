package com.ifsphto.vlp_info2_2017.minichat.page;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ifsphto.vlp_info2_2017.minichat.R;
import com.ifsphto.vlp_info2_2017.minichat.page.adapters.MyFragmentPagerAdapter;
import com.ifsphto.vlp_info2_2017.minichat.settings.SettingsActivity;

/**
 * Essa classe gerencia a tela onde há duas abas
 * aba de Posts e de mensagens
 */

public class SharingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_share);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtém o que foi passado da outra atividade para essa
        Bundle bundle = getIntent().getExtras();

        // O Layout e o gerenciador das abas são recuperados
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);

        mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(),
                getResources().getStringArray(R.array.tab_title)));

        mTabLayout.setupWithViewPager(mViewPager);

        // Seta a pagina atual pelo que foi escolhido pelo usuário
        if (bundle != null)
            mViewPager.setCurrentItem(bundle.getInt("tab"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sharing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent it  = new Intent(this, SettingsActivity.class);
            startActivity(it);
        }

        return super.onOptionsItemSelected(item);
    }

}