package com.ifsphto.vlp_info2_2017.minichat.utils.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ifsphto.vlp_info2_2017.minichat.R;
import com.ifsphto.vlp_info2_2017.minichat.object.Post;
import com.ifsphto.vlp_info2_2017.minichat.utils.settings.ColorsPreferences;

import java.util.List;

/**
 * Created by vinibrenobr11 on 21/03/2017 at 21:39<br><br>
 *
 * Essa Classe personaliza o layout do {@link android.widget.ListView}<br>
 *
 * O Layout "Padrão" do ListView no Android só permite um texto por objeto
 */
public class PostsAdapter extends BaseAdapter {

    // Atributos da classe
    private final List<Post> post;
    private final Activity act;
    private int layout;

    /**
     * Construtor dessa classe
     * @param post Lista com todos os posts
     * @param act Activity passasa por referencia
     * @param layout Diz qual layout será usado para criar a ListView personalizada
     */
    public PostsAdapter(List<Post> post, Activity act, int layout) {
        this.post = post;
        this.act = act;
        this.layout = layout;
    }

    /**
     * Retorna o número de Posts na List.
     * @return Número de Posts.
     */
    @Override
    public int getCount() {
        return post.size();
    }

    /**
     * Retorna um post na posição indicada.
     * @param position posição no ArrayList.
     * @return Post na posição indicada.
     */
    @Override
    public Object getItem(int position) {
        return post.get(position);
    }

    /**
     * Serve pra nada, só tá aqui porque tem que ser implementado.
     * @param position posição
     * @return 0
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Faz e retorna toda a view dos posts
     * @param position Não Sei.
     * @param convertView Não Sei.
     * @param parent Não Sei.
     * @return Posts
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View view = act.getLayoutInflater().inflate(layout, parent, false);

        Post postagem = post.get(position);

        /*
        Esses são os objetos TextView que recebem os TextViews
        do layout personalizado, são eles:

        conteúdo do post
        autor do post
        data do post
         */
        TextView post_content = view.findViewById(R.id.post_content);
        TextView post_author  = view.findViewById(R.id.post_author);
        TextView post_date    = view.findViewById(R.id.post_date);

        /*
        Aqui é definido o conteúdo do post,
        O autor do Post, e a data em que ele foi feito
        Nos objetos TextView do Layout
         */
        post_content.setText(postagem.getContent());
        post_author.setText(postagem.getAuthor());
        post_date.setText(postagem.getDate());

        // Obtém as cores das preferencias e as seta
        int[] colors = ColorsPreferences.getPostColors(act);

        post_content.setTextColor(colors[0]);
        post_author.setTextColor(colors[1]);
        post_date.setTextColor(colors[2]);

        // Retorna a View
        return view;
    }
}