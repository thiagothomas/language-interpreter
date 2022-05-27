package model.instrucoes;

public interface InstrucoesCommand<E> {

    E commandInstrucaoVar(Variavel stmt);

    E commandInstrucaoFuncao(Funcao stmt);

    E commandInstrucaoExpressao(Expressao stmt);

    E commandInstrucaoSe(Se stmt);

    E commandInstrucaoEnquanto(Enquanto stmt);

    E commandInstrucaoBloco(BlocoDeCodigo stmt);

    E commandInstrucaoImrpimir(Imprimir stmt);

    E commandInstrucaoRetornar(Retornar stmt);

}
