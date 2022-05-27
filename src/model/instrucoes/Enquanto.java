package model.instrucoes;

import model.expressoes.Expr;

public class Enquanto implements Instrucao {

    private final Expr condicao;
    private final Instrucao corpo;

    public Enquanto(Expr condicao, Instrucao corpo) {
        this.condicao = condicao;
        this.corpo = corpo;
    }

    @Override
    public <E> E executar(InstrucoesCommand<E> command) {
        return command.commandInstrucaoEnquanto(this);
    }

    public Expr getCondicao() {
        return condicao;
    }

    public Instrucao getCorpo() {
        return corpo;
    }
}
