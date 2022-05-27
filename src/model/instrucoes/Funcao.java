package model.instrucoes;

import model.generico.Token;

import java.util.List;

public class Funcao implements Instrucao {

    private final Token nome;
    private final List<Token> parametros;
    private final List<Instrucao> corpo;

    public Funcao(Token nome, List<Token> parametros, List<Instrucao> corpo) {
        this.nome = nome;
        this.parametros = parametros;
        this.corpo = corpo;
    }

    @Override
    public <E> E executar(InstrucoesCommand<E> command) {
        return command.commandInstrucaoFuncao(this);
    }

    public Token getNome() {
        return nome;
    }

    public List<Token> getParametros() {
        return parametros;
    }

    public List<Instrucao> getCorpo() {
        return corpo;
    }
}
