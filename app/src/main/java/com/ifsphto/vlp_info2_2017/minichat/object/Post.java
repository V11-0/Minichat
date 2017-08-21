package com.ifsphto.vlp_info2_2017.minichat.object;

/**
 * Created by vinibrenobr11 on 21/03/2017 at 19:37
 */
public class Post {

    /**
     * Esse classe é um objeto
     * Post, onde possui, o autor do Post,
     * o conteúdo do Post, e a data em que ele
     * foi feito
     */

    // Autor
    private String author;

    // Conteúdo do post
    private String content;

    // A data em que ele foi publicado
    private String date;

    public Post(String author, String content, String date) {
        this.author = author;
        this.content = content;
        this.date = date;
    }

    // Gets aqui
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
