package com.ifsphto.vlp_info2_2017.minichat.page.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ifsphto.vlp_info2_2017.minichat.page.fragments.*;

/**
 * Created by vinibrenobr11 on 15/03/2017 at 11:42<br><br>
 *
 * Essa Classe Gerencia os Fragments da Activity com telas de deslizamento, onde os posts
 * e as mensagens são feitas<br>
 *
 * @see Fragment para mais detalhes
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    // Nome das Abas
    private String[] mTabTitles;

    /**
     * Construtor
     *
     * @param fm gerenciador desses Fragments
     * @param mTabTitles Array com o nome das abas
     */
    public MyFragmentPagerAdapter(FragmentManager fm, String[] mTabTitles) {
        super(fm);
        this.mTabTitles = mTabTitles;
    }

    /**
     * Retorna as classes que vão gerenciar os fragments
     * @param position posição do fragment
     * @return que gerencia os layouts correspondentes
     */
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

    /**
     * Retorna o número de Strings dentro do Array de títulos das abas
     * @return Números de Strings
     */
    @Override
    public int getCount() {
        return mTabTitles.length;
    }

    /**
     * Nome da aba em determinada posição
     * @param position index
     * @return Nome da aba
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }
}
