package model.instrucoes;

public interface Instrucao {

    <E> E executar(InstrucoesCommand<E> command);

}
