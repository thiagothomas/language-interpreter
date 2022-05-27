package model.expressoes;

import model.generico.Token;

public class Var implements Expr{

    private final Token nome;

    public Var(Token nome) {
        this.nome = nome;
    }

    @Override
    public <E> E executar(ExprCommand<E> command) {
        return command.commandExprVariavel(this);
    }

    public Token getNome() {
        return nome;
    }
}
