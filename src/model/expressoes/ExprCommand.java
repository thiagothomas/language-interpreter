package model.expressoes;

public interface ExprCommand<E> {

    E commandExprVariavel(Var expr);

    E commandExprAtribuicao(Atribuicao expr);

    E commandExprChamada(Chamada expr);

    E commandExprUnaria(ExprUnaria expr);

    E commandExprBinaria(ExprBinaria expr);

    E commandExprLiteral(Literal expr);

    E commandExprLogica(Logica expr);

    E commandExprAgrupamento(Agrupamento expr);

}
