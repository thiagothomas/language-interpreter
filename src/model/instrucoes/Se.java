package model.instrucoes;

import model.expressoes.Expr;

public class Se implements Instrucao {

    private final Expr condicao;
    private final Instrucao entaoCorpo;
    private final Instrucao senaoCorpo;

    public Se(Expr condicao, Instrucao entaoCorpo, Instrucao senaoCorpo) {
        this.condicao = condicao;
        this.entaoCorpo = entaoCorpo;
        this.senaoCorpo = senaoCorpo;
    }

    @Override
    public <E> E executar(InstrucoesCommand<E> command) {
        return command.commandInstrucaoSe(this);
    }

    public Expr getCondicao() {
        return condicao;
    }

    public Instrucao getEntaoCorpo() {
        return entaoCorpo;
    }

    public Instrucao getSenaoCorpo() {
        return senaoCorpo;
    }
}
