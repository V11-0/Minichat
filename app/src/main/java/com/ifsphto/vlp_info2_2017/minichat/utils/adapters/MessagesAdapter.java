package com.ifsphto.vlp_info2_2017.minichat.utils.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ifsphto.vlp_info2_2017.minichat.R;
import com.ifsphto.vlp_info2_2017.minichat.object.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinibrenobr11 on 29/04/2017 at 01:28<br><br>
 *
 * Essa classe serve, digamos, para organizar as mensagens na tela de conversa
 * @see ArrayAdapter para mais detalhes.
 */
public class MessagesAdapter extends ArrayAdapter<Message> {

    // ArrayList com todas as mensagens dessa conversa
    private List<Message> chatMessageList = new ArrayList<>();

    /**
     * Construtor
     *
     * @param context            Context da Aplicação
     * @param textViewResourceId Layout a ser usado para exibir as mensagems
     */
    public MessagesAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    /**
     * Adiciona uma mensagem ao Adapter
     * @param object Mensagem
     */
    @Override
    public void add(Message object) {
        chatMessageList.add(object);
        super.add(object);
    }

    /**
     * Retorna o número de objetos do ArrayList
     * @return Tamanho do Array.
     */
    public int getCount() {
        return this.chatMessageList.size();
    }

    /**
     * Retorna uma Mensagem no index passado por parâmetro do Array
     * @param index index.
     * @return Mensagem
     */
    public Message getItem(int index) {
        return this.chatMessageList.get(index);
    }

    /**
     * Constroí a view com as mensagens.
     *
     * @param position Não sei.
     * @param convertView Não sei.
     * @param parent Não sei.
     * @return View com as mensagens.
     */
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        //Cria o objeto mensagem e a obtem do Array
        Message chatMessageObj = getItem(position);
        View row;

        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Define se a mensagem vai ficar no lado esquerdo ou direito da tela
        assert chatMessageObj != null;

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