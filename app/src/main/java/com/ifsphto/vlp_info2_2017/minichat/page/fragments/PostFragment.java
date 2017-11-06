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
import android.widget.Toast;

import com.ifsphto.vlp_info2_2017.minichat.LoginActivity;
import com.ifsphto.vlp_info2_2017.minichat.R;
import com.ifsphto.vlp_info2_2017.minichat.connection.ConnectionClass;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by vinibrenobr11 on 15/03/2017 at 11:48<br></br>
 *
 * Essa classe gerencia a aba 1 da tela de SharingActivity, Ela, basicamente
 * pega o texto do EditText e Insere no MySQL
 */
public class PostFragment extends Fragment {

    // Atributos
    private EditText edt_post;
    private SharedPreferences prefs;
    private String content;

    /**
     * Executado quando a activity onde está o Fragment é iniciada
     * @param savedInstanceState Não sei direito
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Arquivo com as preferencias do Usuário
        prefs = getActivity().getSharedPreferences(LoginActivity.LOGIN_PREFS, Context.MODE_PRIVATE);

        // Botão para realizar o post, e campo para digitar
        Button btn_post = getActivity().findViewById(R.id.btn_post);
        edt_post = getActivity().findViewById(R.id.edt_post);

        // Método executado ao clicar no botão
        btn_post.setOnClickListener(v -> {
            // Pega o que o usuário digitou
            content = edt_post.getText().toString();

            // Verifica se o que o usuario digitou está vazio
            if (TextUtils.isEmpty(content))
                Snackbar.make(getView(), getString(R.string.error_post_null), Snackbar.LENGTH_LONG).show();
            else {

                prog(true);
                // Executa a classe para enviar o post
                SetPost setPost = new SetPost();
                setPost.execute("");
            }
        });
    }

    /**
     * Deixa o layout de forma a indicar progresso, removendo os campos de texto e
     * deixando apenas o espiral de progresso
     *
     * @param show true, para exibir o progresso, 0 para exibir o layout normalmente
     */
    private void prog(boolean show) {

        if (show) {
            getActivity().findViewById(R.id.post_prog).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.linear_post).setVisibility(View.GONE);
        } else {
            getActivity().findViewById(R.id.post_prog).setVisibility(View.GONE);
            getActivity().findViewById(R.id.linear_post).setVisibility(View.VISIBLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    private class SetPost extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {

            AlertDialog.Builder dlg_error = new AlertDialog.Builder(getActivity());
            dlg_error.setNeutralButton("OK", null);

            try {
                Connection con = ConnectionClass.conn(false);

                content = content.replace("'", "''");

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar calendar = Calendar.getInstance();

                String query = "INSERT INTO Host VALUES ('" + prefs
                        .getInt("id", Integer.MIN_VALUE) +
                        "', '" + content + "', '" + sdf.format(calendar.getTime()) + "')";

                con.createStatement().execute(query);
                con.close();

                /*
                 Aqui a atividade é obtida, no caso, SharingActivity, e a termina com
                 um código de resultado 52
                */
                getActivity().setResult(52);
                getActivity().finish();

            } catch (Exception ex) {
                ex.printStackTrace();
                prog(false);
                Toast.makeText(getContext(), "Ocorreu um erro", Toast.LENGTH_LONG).show();
            }
            return null;
        }
    }
}
