package model.instrucoes;

import model.generico.Token;
import model.expressoes.Expr;

public class Variavel implements Instrucao {

    private final Token nome;
    private final Expr inicializador;

    public Variavel(Token nome, Expr inicializador) {
        this.nome = nome;
        this.inicializador = inicializador;
    }

    @Override
    public <E> E executar(InstrucoesCommand<E> command) {
        return command.commandInstrucaoVar(this);
    }

    public Token getNome() {
        return nome;
    }

    public Expr getInicializador() {
        return inicializador;
    }
}
