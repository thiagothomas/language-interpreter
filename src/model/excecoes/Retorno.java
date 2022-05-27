package model.excecoes;

public class Retorno extends RuntimeException {
    private final Object valor;

    public Retorno(Object valor) {
        super(null, null, false, false);
        this.valor = valor;
    }

    public Object getValor() {
        return valor;
    }
}
