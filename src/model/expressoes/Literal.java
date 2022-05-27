package model.expressoes;

public class Literal implements Expr {

    private final Object valor;

    public Literal(Object valor) {
        this.valor = valor;
    }

    @Override
    public <E> E executar(ExprCommand<E> command) {
        return command.commandExprLiteral(this);
    }

    public Object getValor() {
        return valor;
    }
}
