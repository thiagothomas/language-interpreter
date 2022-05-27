package model.instrucoes;

import model.expressoes.Expr;

public class Expressao implements Instrucao {

    private final Expr expressao;

    public Expressao(Expr expressao) {
        this.expressao = expressao;
    }

    @Override
    public <E> E executar(InstrucoesCommand<E> command) {
        return command.commandInstrucaoExpressao(this);
    }

    public Expr getExpressao() {
        return expressao;
    }
}
