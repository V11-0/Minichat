package br.vinibrenobr11.minichat.object;

/**
 * Created by vinibrenobr11 on 29/04/2017 at 01:20<br><br>
 *
 * Esse objeto representa uma mensagem que possui o boolean left que diz em qual lado da tela
 * essa mensagem vai ficar. E o conteúdo dessa mensagem.
 */
public class Message {

    // Define onde a mensagem ficará, no lado esquerdo ou direito
    private boolean left;

    // Conteúdo da mensagem
    private String message;

    /**
     * Construtor da mensagem
     * @param left Define quem enviou a mensagem, se foi o proprio usuário, ou a outra pessoa
     * @param message O conteúdo da mensagem
     */
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
