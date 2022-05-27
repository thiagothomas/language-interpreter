package model.instrucoes;

import model.expressoes.Expr;

public class Imprimir implements Instrucao {

    private final Expr expressao;

    public Imprimir(Expr expressao) {
        this.expressao = expressao;
    }

    @Override
    public <E> E executar(InstrucoesCommand<E> command) {
        return command.commandInstrucaoImrpimir(this);
    }

    public Expr getExpressao() {
        return expressao;
    }
}
