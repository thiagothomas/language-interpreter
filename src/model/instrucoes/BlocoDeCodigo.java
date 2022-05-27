package model.instrucoes;

import java.util.List;

public class BlocoDeCodigo implements Instrucao {

    private final List<Instrucao> instrucoes;

    public BlocoDeCodigo(List<Instrucao> instrucoes) {
        this.instrucoes = instrucoes;
    }

    @Override
    public <E> E executar(InstrucoesCommand<E> command) {
        return command.commandInstrucaoBloco(this);
    }

    public List<Instrucao> getInstrucoes() {
        return instrucoes;
    }

}
