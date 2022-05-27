package model.expressoes;

public class Agrupamento implements Expr {

    private final Expr expressao;

    public Agrupamento(Expr expressao) {
        this.expressao = expressao;
    }

    @Override
    public <E> E executar(ExprCommand<E> command) {
        return command.commandExprAgrupamento(this);
    }

    public Expr getExpressao() {
        return expressao;
    }
}
