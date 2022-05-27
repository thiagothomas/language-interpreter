package model.excecoes;

import model.generico.Token;

public class ErroEmExecucao extends RuntimeException {
    private final Token token;

    public ErroEmExecucao(Token token, String mensagem) {
        super(mensagem);
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}
