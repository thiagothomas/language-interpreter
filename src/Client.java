import model.expressoes.*;
import model.generico.TipoFuncao;
import model.generico.Token;
import model.instrucoes.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.IntStream;

public class Client implements ExprCommand<Void>, InstrucoesCommand<Void> {
    private final Interpretador interp;

    private final Stack<Map<String, Boolean>> escopos = new Stack<>();
    private TipoFuncao funcaoAtual = TipoFuncao.DEFAULT;

    Client(Interpretador interp) {
        this.interp = interp;
    }

    public void definirEscopos(List<Instrucao> instrucoes) {
        for (Instrucao instrucao : instrucoes) {
            definirEscopos(instrucao);
        }
    }

    private void definirEscopos(Instrucao instrucao) {
        instrucao.executar(this);
    }

    private void definirEscopos(Expr expressao) {
        expressao.executar(this);
    }

    @Override
    public Void commandInstrucaoBloco(BlocoDeCodigo blocoDeCodigo) {
        inicioDoEscopo();
        definirEscopos(blocoDeCodigo.getInstrucoes());
        fimDoEscopo();
        return null;
    }

    @Override
    public Void commandInstrucaoExpressao(Expressao expressao) {
        definirEscopos(expressao.getExpressao());
        return null;
    }

    @Override
    public Void commandInstrucaoFuncao(Funcao funcao) {
        declaracao(funcao.getNome());
        definicao(funcao.getNome());
        verificaFuncao(funcao);
        return null;
    }

    @Override
    public Void commandInstrucaoSe(Se se) {
        definirEscopos(se.getCondicao());
        definirEscopos(se.getEntaoCorpo());
        if (se.getSenaoCorpo() != null) {
            definirEscopos(se.getSenaoCorpo());
        }
        return null;
    }

    @Override
    public Void commandInstrucaoImrpimir(Imprimir imprimir) {
        definirEscopos(imprimir.getExpressao());
        return null;
    }

    @Override
    public Void commandInstrucaoRetornar(Retornar retornar) {
        if (funcaoAtual == TipoFuncao.DEFAULT) {
            Principal.erroNaExecucao(retornar.getPalavraChave(), "Retornar não permitido neste local");
        }

        if (retornar.getValor() != null) {
            definirEscopos(retornar.getValor());
        }

        return null;
    }

    @Override
    public Void commandInstrucaoVar(Variavel variavel) {
        declaracao(variavel.getNome());
        if (variavel.getInicializador() != null) {
            definirEscopos(variavel.getInicializador());
        }
        definicao(variavel.getNome());
        return null;
    }

    @Override
    public Void commandInstrucaoEnquanto(Enquanto enquanto) {
        definirEscopos(enquanto.getCondicao());
        definirEscopos(enquanto.getCorpo());
        return null;
    }

    @Override
    public Void commandExprAtribuicao(Atribuicao atribuicao) {
        definirEscopos(atribuicao.getValor());
        verificacaoLocal(atribuicao, atribuicao.getNome());
        return null;
    }

    @Override
    public Void commandExprBinaria(ExprBinaria exprBinaria) {
        definirEscopos(exprBinaria.getEsquerda());
        definirEscopos(exprBinaria.getDireita());
        return null;
    }

    @Override
    public Void commandExprChamada(Chamada chamada) {
        definirEscopos(chamada.getExprChamada());

        for (Expr argumentos : chamada.getArgumentos()) {
            definirEscopos(argumentos);
        }

        return null;
    }


    @Override
    public Void commandExprAgrupamento(Agrupamento agrupamento) {
        definirEscopos(agrupamento.getExpressao());
        return null;
    }

    @Override
    public Void commandExprLiteral(Literal literal) {
        return null;
    }

    @Override
    public Void commandExprLogica(Logica logica) {
        definirEscopos(logica.getEsquerda());
        definirEscopos(logica.getDireita());
        return null;
    }

    @Override
    public Void commandExprUnaria(ExprUnaria exprUnaria) {
        definirEscopos(exprUnaria.getDireita());
        return null;
    }

    @Override
    public Void commandExprVariavel(Var variavel) {
        verificacaoLocal(variavel, variavel.getNome());
        return null;
    }

    private void verificaFuncao(Funcao funcao) {
        TipoFuncao escopoDaFuncao = funcaoAtual;
        funcaoAtual = TipoFuncao.FUNCAO;

        inicioDoEscopo();
        for (Token parametro : funcao.getParametros()) {
            declaracao(parametro);
            definicao(parametro);
        }
        definirEscopos(funcao.getCorpo());
        fimDoEscopo();
        funcaoAtual = escopoDaFuncao;
    }

    private void inicioDoEscopo() {
        escopos.push(new HashMap<>());
    }

    private void fimDoEscopo() {
        escopos.pop();
    }

    private void declaracao(Token nomeVar) {
        if (escopos.isEmpty()) {
            return;
        }

        Map<String, Boolean> scope = escopos.peek();

        scope.put(nomeVar.getLexema(), false);
    }

    private void definicao(Token nomeVar) {
        if (escopos.isEmpty()) {
            return;
        }

        escopos.peek().put(nomeVar.getLexema(), true);
    }

    private void verificacaoLocal(Expr expressao, Token nomeVar) {
        IntStream.iterate(escopos.size() - 1, i -> i >= 0, i -> i - 1)
                .filter(i -> escopos.get(i).containsKey(nomeVar.getLexema()))
                .findFirst()
                .ifPresent(i -> interp.atribuiContexto(expressao, escopos.size() - 1 - i));

    }
}
