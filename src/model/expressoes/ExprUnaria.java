package model.expressoes;

import model.generico.Token;

public class ExprUnaria implements Expr {

    private final Token operador;
    private final Expr direita;

    public ExprUnaria(Token operador, Expr direita) {
        this.operador = operador;
        this.direita = direita;
    }

    @Override
    public <E> E executar(ExprCommand<E> command) {
        return command.commandExprUnaria(this);
    }

    public Token getOperador() {
        return operador;
    }

    public Expr getDireita() {
        return direita;
    }
}
