package model.generico;

public class Token {

    private final TipoToken tipo;
    private final String lexema;
    private final Object literal;
    private final int linha;

    public Token(TipoToken tipo, String lexema, Object literal, int linha) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.literal = literal;
        this.linha = linha;
    }

    public TipoToken getTipo() {
        return tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public Object getLiteral() {
        return literal;
    }

    public int getLinha() {
        return linha;
    }

    @Override
    public String toString() {
        return tipo + " " + lexema + " " + literal;
    }

}
