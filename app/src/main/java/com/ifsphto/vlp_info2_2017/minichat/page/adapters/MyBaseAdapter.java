package com.ifsphto.vlp_info2_2017.minichat.page.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import com.ifsphto.vlp_info2_2017.minichat.R;
import com.ifsphto.vlp_info2_2017.minichat.object.Post;

/**
 * Created by vinibrenobr11 on 21/03/2017 at 21:39
 */
public class MyBaseAdapter extends BaseAdapter {

    /**
     * Essa Classe personaliza o layout do
     * @see android.widget.ListView, veja sua
     * documentacão para mais informações
     *
     * O Layout "Padrão" do ListView no Android
     * só permite um texto por objeto
     */

    // Atributos da classe
    private final List<Post> post;
    private final Activity act;
    private int layout;

    /**
     * Construtor dessa classe, onde recebe:
     * @param post onde é a Lista com todos os posts
     * @param act onde é a Activity passasa por referencia
     * @param layout onde diz qual layout será usado para criar
     *               a ListView personalizada
     */
    public MyBaseAdapter(List<Post> post, Activity act, int layout) {
        this.post = post;
        this.act = act;
        this.layout = layout;
    }

    // Retorna o número de Posts na List
    @Override
    public int getCount() {
        return post.size();
    }

    // Retorna um Post específico na posição passada
    // como parâmetro
    @Override
    public Object getItem(int position) {
        return post.get(position);
    }

    // Retorna 0, por que?, não sei.
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // Retorna a View onde esses Posts ficarão
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

        // Retorna a View
        return view;
    }
}