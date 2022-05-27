package model.expressoes;

import model.generico.Token;

import java.util.List;

public class Chamada implements Expr {

    private final Expr exprChamada;
    private final Token parenteses;
    private final List<Expr> argumentos;

    public Chamada(Expr exprChamada, Token parenteses, List<Expr> argumentos) {
        this.exprChamada = exprChamada;
        this.parenteses = parenteses;
        this.argumentos = argumentos;
    }

    @Override
    public <E> E executar(ExprCommand<E> command) {
        return command.commandExprChamada(this);
    }

    public Expr getExprChamada() {
        return exprChamada;
    }

    public Token getParenteses() {
        return parenteses;
    }

    public List<Expr> getArgumentos() {
        return argumentos;
    }
}
