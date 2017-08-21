package com.ifsphto.vlp_info2_2017.minichat.page.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.ifsphto.vlp_info2_2017.minichat.R;
import com.ifsphto.vlp_info2_2017.minichat.object.Message;

/**
 * Created by vinibrenobr11 on 29/04/2017 at 01:28
 */
public class MessagesAdapter extends ArrayAdapter<Message> {

    /**
     * Essa classe serve, digamos, para organizar as mensagens
     * na tela de conversa
     *
     * @see ArrayAdapter para mais detalhes
     */

    // ArrayList com todas as mensagens dessa conversa
    private List<Message> chatMessageList = new ArrayList<>();

    @Override
    public void add(Message object) {
        chatMessageList.add(object);
        super.add(object);
    }

    // Construtor dessa classe, recebe um objeto
    // Do tipo Context e um layout
    public MessagesAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    // Retorna o número de Mensagens no Array
    public int getCount() {
        return this.chatMessageList.size();
    }

    // Retorna uma Mensagem no index passado por parâmetro do Array
    public Message getItem(int index) {
        return this.chatMessageList.get(index);
    }

    /*
    Esse método "constroi" o layout das Mensagens
     Ele define em qual lado da tela a Mensagem
     vai ficar
     */
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        //Cria o objeto mensagem e a obtem do Array
        Message chatMessageObj = getItem(position);
        View row;

        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Define se a mensagem vai ficar no lado esquerdo ou direito da tela
        if (chatMessageObj.isLeft())
            row = inflater.inflate(R.layout.left, parent, false);
        else
            row = inflater.inflate(R.layout.right, parent, false);

        // Coloca a Mensagem no lado definido
        TextView chatText = row.findViewById(R.id.msgr);
        // Obtem o conteúdo da Mensagem
        chatText.setText(chatMessageObj.getMessage());
        // Retorna a View
        return row;
    }
}