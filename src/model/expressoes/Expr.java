package model.expressoes;

public interface Expr {

    <E> E executar(ExprCommand<E> command);

}
