package model.instrucoes;

import model.generico.Token;
import model.expressoes.Expr;

public class Retornar implements Instrucao {

    private final Token palavraChave;
    private final Expr valor;

    public Retornar(Token palavraChave, Expr valor) {
        this.palavraChave = palavraChave;
        this.valor = valor;
    }

    @Override
    public <E> E executar(InstrucoesCommand<E> command) {
        return command.commandInstrucaoRetornar(this);
    }

    public Token getPalavraChave() {
        return palavraChave;
    }

    public Expr getValor() {
        return valor;
    }
}
