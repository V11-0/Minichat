package com.ifsphto.vlp_info2_2017.minichat.object;

/**
 * Created by vinibrenobr11 on 29/04/2017 at 01:20
 */
public class Message {

    /**
     * Esse objeto representa uma mensagem
     *
     * Que possui o boolean left que diz em qual
     * lado da tela essa mensagem vai ficar, e o
     * conteúdo dessa mensagem
     */

    // Define onde a mensagem ficará
    // No lado esquerdo ou direito
    private boolean left;

    // Conteúdo da mensagem
    private String message;

    // Construtor
    public Message(boolean left, String message) {
        this.left = left;
        this.message = message;
    }

    // Gets
    public boolean isLeft() {
        return left;
    }

    public String getMessage() {
        return message;
    }
}
