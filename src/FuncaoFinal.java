import model.excecoes.Retorno;
import model.instrucoes.Funcao;

import java.util.List;

public class FuncaoFinal implements FuncaoCallable {

    private final Funcao declaracao;
    private final Contexto contexto;

    public FuncaoFinal(Funcao declaracao, Contexto contexto) {
        this.contexto = contexto;
        this.declaracao = declaracao;
    }

    @Override
    public int numParametros() {
        return declaracao.getParametros().size();
    }

    @Override
    public Object chamaFuncao(Interpretador interpretador, List<Object> listaDeArgumentos) {

        Contexto contextoFuncao = new Contexto(contexto);
        for (int i = 0; i < declaracao.getParametros().size(); i++) {
            contextoFuncao.definirValor(declaracao.getParametros().get(i).getLexema(), listaDeArgumentos.get(i));
        }

        try {
            interpretador.executaBlocoDeCodigo(declaracao.getCorpo(), contextoFuncao);
        } catch (Retorno valorDeRetorno) {
            return valorDeRetorno.getValor();
        }

        return null;
    }
}
