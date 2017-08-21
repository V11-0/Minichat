package com.ifsphto.vlp_info2_2017.minichat.page.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.ifsphto.vlp_info2_2017.minichat.MainActivity;
import com.ifsphto.vlp_info2_2017.minichat.R;
import com.ifsphto.vlp_info2_2017.minichat.connection.ConnectionClass;

/**
 * Created by vinibrenobr11 on 15/03/2017 at 11:48
 *
 * Essa classe gerencia a aba 1 da tela de SharingActivity,
 * Ela, basicamente, pega o texto do EditText e Insere no MySQL
 */
public class PostFragment extends Fragment {

    // Atributos
    private EditText edt_post;
    private SharedPreferences prefs;
    private String content;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Arquivo com as preferencias do Usuário
        prefs = getActivity().getSharedPreferences(MainActivity.LOGIN_PREFS, Context.MODE_PRIVATE);

        // Botão para realizar o post, e campo para digitar
        Button btn_post = getActivity().findViewById(R.id.btn_post);
        edt_post = getActivity().findViewById(R.id.edt_post);

        // Método executado ao clicar no botão
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pega o que o usuário digitou
                content = edt_post.getText().toString();

                // Verifica se o que o usuario digitou está vazio
                if (TextUtils.isEmpty(content))
                    Snackbar.make(getView(), getString(R.string.error_post_null), Snackbar.LENGTH_LONG).show();
                // Aqui é verificado se o que o usuário digitou contém aspas simples ''
                // pois o MySQL não as suporta
                else if (content.contains("'"))
                    Snackbar.make(getView(), getString(R.string.post_error), Snackbar.LENGTH_LONG).show();
                else {

                    // Executa a classe para enviar o post
                    SetPost setPost = new SetPost();
                    setPost.execute("");

                    /*
                    Aqui a atividade é obtida, no caso, SharingActivity, e a termina com
                    um código de resultado 52
                     */
                    getActivity().setResult(52);
                    getActivity().finish();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.new_post_layout, container, false);
    }

    private class SetPost extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {

            AlertDialog.Builder dlg_error = new AlertDialog.Builder(getActivity());
            dlg_error.setNeutralButton("OK", null);

            try {
                ConnectionClass connectionClass = new ConnectionClass();

                Connection con = connectionClass.conn();

                if (con == null)
                    dlg_error.setMessage(connectionClass.getException()).show();
                else {

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Calendar calendar = Calendar.getInstance();

                    String query = "INSERT INTO Poststbl VALUES ('" + prefs.getString("name", "404") +
                            "', '" + content + "', '" +  sdf.format(calendar.getTime()) + "')";
                    Statement stmt = con.createStatement();
                    stmt.execute(query);
                    con.close();
                }

            } catch (Exception ex) {
                dlg_error.setMessage(ex.getMessage()).show();
                ex.printStackTrace();
            }
            return null;
        }
    }
}
