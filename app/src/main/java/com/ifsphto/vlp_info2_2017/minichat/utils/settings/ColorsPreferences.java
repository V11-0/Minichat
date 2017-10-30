package com.ifsphto.vlp_info2_2017.minichat.utils.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;

import com.ifsphto.vlp_info2_2017.minichat.R;

/**
 * Created by vinibrenobr11 on 19/10/2017 at 17:18:23 <br><br>
 *
 * Essa classe é usada para aplicar preferencias do usuário em relação as cores da aplicação
 * Sendo essas preferencias por equanto de cores relacionados a posts, e de cores primárias
 */
public abstract class ColorsPreferences {

    /**
     * Retorna um array com as cores que o usuário definiu em preferences, esses valores são
     * salvos em  um {@link SharedPreferences} pelas preferences, e são recuperadas aqui
     * retornando-os em um array
     * @param act Activity que está chamanado o método
     * @return Array com valores armazendos de preferences
     */
    public static int[] getPostColors(Activity act) {

        SharedPreferences prefs = act.getSharedPreferences(act.getApplicationInfo()
                .packageName + "_preferences", Context.MODE_PRIVATE);

        int[] text_colors = new int[3];

        text_colors[0] = prefs.getInt("PostContent", Color.BLACK);
        text_colors[1] = prefs.getInt("PostAuthor", Color.BLACK);
        text_colors[2] = prefs.getInt("PostDate", Color.BLACK);

        return text_colors;
    }

    /**
     * Método não está pronto
     * @param act Activity que está chamando o método
     */
    public void setOrGetMainTheme(Activity act) {

        Resources.Theme t = act.getTheme();

        SharedPreferences prefs = act.getSharedPreferences(act.getApplicationInfo()
        .packageName + "_preferences", Context.MODE_PRIVATE);

        int primary = prefs.getInt("PrimaryColor", R.color.colorPrimary);
        int accent = prefs.getInt("AccentColor", R.color.colorAccent);

        //TODO: Aplicar cores primaria e accent definidos em preferences
    }

}
