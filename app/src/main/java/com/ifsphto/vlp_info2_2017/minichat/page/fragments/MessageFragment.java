package com.ifsphto.vlp_info2_2017.minichat.page.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.ifsphto.vlp_info2_2017.minichat.MainActivity;
import com.ifsphto.vlp_info2_2017.minichat.R;
import com.ifsphto.vlp_info2_2017.minichat.connection.ConnectionClass;
import com.ifsphto.vlp_info2_2017.minichat.page.ChatActivity;

/**
 * Created by vinibrenobr11 on 15/03/2017 at 11:47
 *
 * Esse classe gerenciará a segunda aba da SharingActivity que é a aba de Mensagens
 * Onde por agora há apenas um botão para ver os usuários disponíveis
 *
 * AVISO: A classe
 * ProgressDialog está descontinuada, temos que achar um
 * jeito de substituí-la, mas não precisamos nos preocupar com isso
 */
public class MessageFragment extends Fragment {

    // Atributos da classe
    private ConnectionClass connectionClass = new ConnectionClass();
    private ArrayAdapter<String> adpUsers;
    private ProgressDialog pdlg;
    private Dialog dlg;

    // Método chamado quando a atividade que vai ter o Fragment é criada
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Recupera o botão flutuante do layout
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);

        // Cria o ArrayAdapter, passando como parâmetro o Context
        // atual e qual layout será usado nele
        adpUsers = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);

        // Atribui as propriedades ao Dialogo para avisar ao usuario
        pdlg = new ProgressDialog(getContext());
        pdlg.setTitle(getString(R.string.dlg_load_users_title));
        pdlg.setMessage(getString(R.string.pls_wait));
        pdlg.incrementProgressBy(ProgressDialog.STYLE_SPINNER);
        pdlg.setCancelable(false);

        // Método executado quando o botão flutuante recebe um toque
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Limpa o Array de usuarios e mostra o diálogo
                adpUsers.clear();
                pdlg.show();

                // Executa a classe para obter os usuários do banco
                GetUsers gu = new GetUsers();
                gu.execute("");

                /*
                Cria um outro dialogo que exibe o nome dos
                usuários que estão no banco
                 */
                dlg = new Dialog(getContext());
                dlg.setContentView(R.layout.choose_contact);

                // Cria um ListView e seta o Adapter nele
                ListView people_list = dlg.findViewById(R.id.people_list);
                people_list.setAdapter(adpUsers);

                // Método executado quando o usuário escolhe um usuário da Lista
                people_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        // Cria o Intent e passa para a tela de Chat quem foi o escolhido
                        Intent chat = new Intent(getActivity(), ChatActivity.class);
                        chat.putExtra("SelectedName", adpUsers.getItem(position));

                        // Inicia o chat
                        startActivity(chat);
                        dlg.dismiss();
                    }
                });
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.msg_layout, container, false);
    }

    private class GetUsers extends AsyncTask<String,String,String> {
        String z = "";
        ResultSet rs;
        boolean isSuccess = false;

        Connection con;

        @Override
        protected void onPostExecute(String s) {

            try {

                if (isSuccess) {

                    for (int i=1; i <= rs.getRow(); i++) {
                        adpUsers.add(rs.getString(1));
                        rs.next();
                    }

                    SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.LOGIN_PREFS,
                            Context.MODE_PRIVATE);

                    adpUsers.remove(prefs.getString("name", null));
                    pdlg.dismiss();
                    dlg.show();

                }else {
                    pdlg.dismiss();
                    Snackbar.make(getActivity().findViewById(R.id.fab), getString(R.string.check_net)
                            , Snackbar.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... params) {

            try {

                con = connectionClass.conn();

                if (con == null)
                    z = connectionClass.getException();
                else {
                    String query = "SELECT UserId FROM Usertbl";
                    Statement stmt = con.createStatement();
                    rs = stmt.executeQuery(query);
                }

                isSuccess = rs.next();

            } catch (Exception ex) {
                isSuccess = false;
                z = ex.getMessage();
                ex.printStackTrace();
            }
            return z;
        }
    }
}
