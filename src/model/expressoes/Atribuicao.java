package model.expressoes;

import model.generico.Token;

public class Atribuicao implements Expr {

    private final Token nome;
    private final Expr valor;

    public Atribuicao(Token nome, Expr valor) {
        this.nome = nome;
        this.valor = valor;
    }

    @Override
    public <E> E executar(ExprCommand<E> command) {
        return command.commandExprAtribuicao(this);
    }

    public Token getNome() {
        return nome;
    }

    public Expr getValor() {
        return valor;
    }
}
