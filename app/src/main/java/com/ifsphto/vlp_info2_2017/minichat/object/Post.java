package com.ifsphto.vlp_info2_2017.minichat.object;

/**
 * Created by vinibrenobr11 on 21/03/2017 at 19:37<br><br>
 *
 * Esse classe é um objeto post, onde possui, o autor do Post, o conteúdo do Post,
 * e a data em que ele foi feito.
 */
public class Post {

    // Autor
    private String author;

    // Conteúdo do post
    private String content;

    // A data em que ele foi publicado
    private String date;

    /**
     * Construtor do objeto
     * @param author Autor do Post
     * @param content Texto do Post
     * @param date Data em que ele foi publicado
     */
    public Post(String author, String content, String date) {
        this.author = author;
        this.content = content;
        //TODO: Formatar data e exibir ela bonitinha
        this.date = date;
    }

    // Gets aqui.

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

}
