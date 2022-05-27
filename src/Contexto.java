import model.excecoes.ErroEmExecucao;
import model.generico.Token;

import java.util.HashMap;
import java.util.Map;

public class Contexto {

    private final Contexto escopo;
    private final Map<String, Object> valores = new HashMap<>();

    public Contexto() {
        escopo = null;
    }

    public Contexto(Contexto escopo) {
        this.escopo = escopo;
    }

    public Object retornarVariavel(Token nomeVariavel) {
        if (valores.containsKey(nomeVariavel.getLexema())) {
            return valores.get(nomeVariavel.getLexema());
        }

        if (escopo != null)
            return escopo.retornarVariavel(nomeVariavel);

        throw new ErroEmExecucao(nomeVariavel, "Variável nao definida '" + nomeVariavel.getLexema() + "'.");
    }

    public void atribuirValorVariavel(Token nomeVariavel, Object valorVariavel) {
        if (valores.containsKey(nomeVariavel.getLexema())) {
            valores.put(nomeVariavel.getLexema(), valorVariavel);
            return;
        }

        if (escopo != null) {
            escopo.atribuirValorVariavel(nomeVariavel, valorVariavel);
            return;
        }

        throw new ErroEmExecucao(nomeVariavel, "Variável nao definida '" + nomeVariavel.getLexema() + "'.");
    }

    public void definirValor(String nomeVariavel, Object valorVariavel) {
        valores.put(nomeVariavel, valorVariavel);
    }

    public Contexto contextoAnterior(int profundidade) {
        Contexto contexto = this;
        for (int i = 0; i < profundidade; i++) {
            assert contexto != null;
            contexto = contexto.escopo;
        }

        return contexto;
    }

    public Object receberValorNaProfundidade(int profundidade, String nomeVar) {
        return contextoAnterior(profundidade).valores.get(nomeVar);
    }

    public void atribuirValorNaProfundidade(int profundidade, Token nomeVar, Object valorVar) {
        contextoAnterior(profundidade).valores.put(nomeVar.getLexema(), valorVar);
    }

}
