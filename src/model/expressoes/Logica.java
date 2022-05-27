package model.expressoes;

import model.generico.Token;

public class Logica implements Expr {

    private final Expr esquerda;
    private final Token operador;
    private final Expr direita;

    public Logica(Expr esquerda, Token operador, Expr direita) {
        this.esquerda = esquerda;
        this.operador = operador;
        this.direita = direita;
    }

    @Override
    public <E> E executar(ExprCommand<E> command) {
        return command.commandExprLogica(this);
    }

    public Expr getEsquerda() {
        return esquerda;
    }

    public Token getOperador() {
        return operador;
    }

    public Expr getDireita() {
        return direita;
    }
}
