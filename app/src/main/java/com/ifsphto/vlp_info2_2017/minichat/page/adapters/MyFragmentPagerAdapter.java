package com.ifsphto.vlp_info2_2017.minichat.page.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ifsphto.vlp_info2_2017.minichat.page.fragments.*;

/**
 * Created by vinibrenobr11 on 15/03/2017 at 11:42
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    /**
     * Essa Classe Gerencia os Fragments da Activity com
     * telas de deslizamento,
     * onde os posts e as mensagens são feitas
     *
     * @see Fragment para mais detalhes
     */

    // Nome das Abas
    private String[] mTabTitles;

    /**
     * Construtor passando:
     *
     * @param fm que é o gerenciador desses Fragments
     * @param mTabTitles que é um array com o nome das abas
     */
    public MyFragmentPagerAdapter(FragmentManager fm, String[] mTabTitles) {
        super(fm);
        this.mTabTitles = mTabTitles;
    }

    // Retorna a classe para gerenciar as telas ativas
    @Override
    public Fragment getItem(int position) {

        switch (position) {

            // 0 é a aba dos posts
            case 0:
                return new PostFragment();
            // 1 é a aba das mensagens
            case 1:
                return new MessageFragment();
            default:
                return null;
        }
    }

    // Retorna o número de Strings dentro do Array de títulos das abas
    @Override
    public int getCount() {
        return mTabTitles.length;
    }

    // Retorna o nome das abas
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }
}
