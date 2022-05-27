import model.excecoes.ErroDeParse;
import model.expressoes.*;
import model.generico.TipoToken;
import model.generico.Token;
import model.instrucoes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {

    private int posicaoAtuaNaLista = 0;
    private final List<Token> listaDeTokens;

    public Parser(List<Token> listaDeTokens) {
        this.listaDeTokens = listaDeTokens;
    }

    public List<Instrucao> parseTokens() {
        List<Instrucao> instrucoes = new ArrayList<>();

        while (!terminouCodigoFonte()) {
            instrucoes.add(declaracao());
        }

        return instrucoes;
    }

    private Instrucao declaracao() {
        try {

            if (correspondente(TipoToken.FUNCAO)) {
                return funcao();
            }

            if (correspondente(TipoToken.VAR)) {
                return declaracaoDeVariavel();
            }

            return instrucao();
        } catch (ErroDeParse erro) {
            sincronizarAposErroEncontradoNoParse();
            return null;
        }
    }

    private Instrucao instrucao() {
        if (correspondente(TipoToken.PARA)) {
            return instrucaoPara();
        }
        if (correspondente(TipoToken.SE)) {
            return instrucaoSe();
        }
        if (correspondente(TipoToken.IMPRIMIR)) {
            return instrucaoImprimir();
        }
        if (correspondente(TipoToken.RETORNAR)) {
            return instrucaoRetornar();
        }
        if (correspondente(TipoToken.ENQUANTO)) {
            return instrucaoEnquanto();
        }
        if (correspondente(TipoToken.CHAVE_ESQ)) {
            return new BlocoDeCodigo(blocoDeCodigo());
        }

        return instrucaoExpressao();
    }

    private Instrucao instrucaoPara() {
        consumirToken(TipoToken.PAREN_ESQ, "Depois de 'para' é esperado '('.");

        Instrucao inicializador;
        if (correspondente(TipoToken.PONTO_VIRG)) {
            inicializador = null;
        } else if (correspondente(TipoToken.VAR)) {
            inicializador = declaracaoDeVariavel();
        } else {
            inicializador = instrucaoExpressao();
        }

        Expr condicaoPara = null;
        if (!verificaTipo(TipoToken.PONTO_VIRG)) {
            condicaoPara = atribuicao();
        }
        consumirToken(TipoToken.PONTO_VIRG, "Depois de condição de loop é esperado ';'");

        Expr incrementoPara = null;
        if (!verificaTipo(TipoToken.PAREN_DIR)) {
            incrementoPara = atribuicao();
        }
        consumirToken(TipoToken.PAREN_DIR, "Depois da definição do 'para' é esperado ')'");

        Instrucao corpoDoPara = instrucao();

        if (incrementoPara != null) {
            corpoDoPara = new BlocoDeCodigo(
                    Arrays.asList(corpoDoPara,
                            new Expressao(incrementoPara)
                    )
            );
        }

        if (condicaoPara == null) {
            condicaoPara = new Literal(true);
        }
        corpoDoPara = new Enquanto(condicaoPara, corpoDoPara);

        if (inicializador != null) {
            corpoDoPara = new BlocoDeCodigo(Arrays.asList(inicializador, corpoDoPara));
        }

        return corpoDoPara;
    }

    private Instrucao instrucaoSe() {
        consumirToken(TipoToken.PAREN_ESQ, "Depois de 'se' é esperado '('.");
        Expr condicaoSe = atribuicao();
        consumirToken(TipoToken.PAREN_DIR, "Depois da condicao é esperado ')'");

        Instrucao corpoDoSe = instrucao();
        Instrucao corpoDoElse = null;
        if (correspondente(TipoToken.SENAO)) {
            corpoDoElse = instrucao();
        }

        return new Se(condicaoSe, corpoDoSe, corpoDoElse);
    }

    private Instrucao instrucaoImprimir() {
        Expr valorParaSerImpresso = atribuicao();
        consumirToken(TipoToken.PONTO_VIRG, "Falta ';' após o valor");
        return new Imprimir(valorParaSerImpresso);
    }

    private Instrucao instrucaoRetornar() {
        Token palavraChave = getTokenAnterior();
        Expr valorParaSerRetornado = null;
        if (!verificaTipo(TipoToken.PONTO_VIRG)) {
            valorParaSerRetornado = atribuicao();
        }

        consumirToken(TipoToken.PONTO_VIRG, "Falta ';' após o valor de retorno.");
        return new Retornar(palavraChave, valorParaSerRetornado);
    }

    private Instrucao declaracaoDeVariavel() {
        Token identificadorVariavel = consumirToken(TipoToken.IDENTIFICADOR, "Espera-se o identificador da variavel.");

        Expr inicializadorVariavel = null;
        if (correspondente(TipoToken.IGUAL)) {
            inicializadorVariavel = atribuicao();
        }

        consumirToken(TipoToken.PONTO_VIRG, "Depois de declaracao é esperado ';'");
        return new Variavel(identificadorVariavel, inicializadorVariavel);
    }

    private Instrucao instrucaoEnquanto() {
        consumirToken(TipoToken.PAREN_ESQ, "Depois de 'enquanto' é esperado '('");
        Expr condicaoEnquanto = atribuicao();
        consumirToken(TipoToken.PAREN_DIR, "Depois da condição é esperado ')'");
        Instrucao corpoEnquanto = instrucao();

        return new Enquanto(condicaoEnquanto, corpoEnquanto);
    }

    private Instrucao instrucaoExpressao() {
        Expr expressao = atribuicao();
        consumirToken(TipoToken.PONTO_VIRG, "Depois de uma expressão é esperado ';'");
        return new Expressao(expressao);
    }

    private Funcao funcao() {
        Token identificadorFuncao = consumirToken(TipoToken.IDENTIFICADOR, "Espera-se nome da funcao");
        consumirToken(TipoToken.PAREN_ESQ, "Depois do nome da funcao, espera-se '('");
        List<Token> parametrosFuncao = new ArrayList<>();

        if (!verificaTipo(TipoToken.PAREN_DIR)) {
            do {
                parametrosFuncao.add(
                        consumirToken(TipoToken.IDENTIFICADOR, "Espera-se definicao dos parametros")
                );
            } while (correspondente(TipoToken.VIRGULA));
        }

        consumirToken(TipoToken.PAREN_DIR, "Depois dos parametros, espera-se ')'");
        consumirToken(TipoToken.CHAVE_ESQ, "Antes do corpo da funcao espera-se { ");

        List<Instrucao> corpoDaFuncao = blocoDeCodigo();
        return new Funcao(identificadorFuncao, parametrosFuncao, corpoDaFuncao);
    }

    private List<Instrucao> blocoDeCodigo() {
        List<Instrucao> instrucoesDoBloco = new ArrayList<>();

        while (!verificaTipo(TipoToken.CHAVE_DIR) && !terminouCodigoFonte()) {
            instrucoesDoBloco.add(declaracao());
        }

        consumirToken(TipoToken.CHAVE_DIR, "Depois de um bloco de codigo, espera-se '}'");
        return instrucoesDoBloco;
    }

    private Expr atribuicao() {
        Expr expressao = expressaoOu();

        if (correspondente(TipoToken.IGUAL)) {
            Token igualdade = getTokenAnterior();
            Expr valorASerAtribuido = atribuicao();

            if (expressao instanceof Var) {
                Token nomeVariavel = ((Var) expressao).getNome();
                return new Atribuicao(nomeVariavel, valorASerAtribuido);
            }

            erroNoParse(igualdade, "Atribuicao inválida");
        }

        return expressao;
    }

    private Expr expressaoOu() {
        Expr expressao = expressaoE();

        while (correspondente(TipoToken.OU)) {
            Token operadorOu = getTokenAnterior();
            Expr expressaoADireita = expressaoE();
            expressao = new Logica(expressao, operadorOu, expressaoADireita);
        }

        return expressao;
    }

    private Expr expressaoE() {
        Expr expressao = expressaoIgualdade();

        while (correspondente(TipoToken.E)) {
            Token operadorE = getTokenAnterior();
            Expr expressaoADireita = expressaoIgualdade();
            expressao = new Logica(expressao, operadorE, expressaoADireita);
        }

        return expressao;
    }

    private Expr expressaoIgualdade() {
        Expr expressao = expressaoComparacao();

        while (correspondente(TipoToken.EXCLAMACAO_IGUAL, TipoToken.IGUAL_IGUAL)) {
            Token operadorIgualdade = getTokenAnterior();
            Expr expressaoADireita = expressaoComparacao();
            expressao = new ExprBinaria(expressao, operadorIgualdade, expressaoADireita);
        }

        return expressao;
    }

    private Expr expressaoComparacao() {
        Expr expressao = maisOuMenos();

        while (correspondente(TipoToken.MAIOR, TipoToken.MAIOR_IGUAL, TipoToken.MENOR, TipoToken.MENOR_IGUAL)) {
            Token operadorComparacao = getTokenAnterior();
            Expr expressaoADireita = maisOuMenos();
            expressao = new ExprBinaria(expressao, operadorComparacao, expressaoADireita);
        }

        return expressao;
    }

    private Expr maisOuMenos() {
        Expr expressao = divisaoOuMultiplicacao();

        while (correspondente(TipoToken.MENOS, TipoToken.MAIS)) {
            Token operador = getTokenAnterior();
            Expr termoADireita = divisaoOuMultiplicacao();
            expressao = new ExprBinaria(expressao, operador, termoADireita);
        }

        return expressao;
    }

    private Expr divisaoOuMultiplicacao() {
        Expr expressao = negacaoOuNegativo();

        while (correspondente(TipoToken.BARRRA, TipoToken.ASTERISCO)) {
            Token operador = getTokenAnterior();
            Expr termoADireita = negacaoOuNegativo();
            expressao = new ExprBinaria(expressao, operador, termoADireita);
        }

        return expressao;
    }

    private Expr negacaoOuNegativo() {
        if (correspondente(TipoToken.EXCLAMACAO, TipoToken.MENOS)) {
            Token operador = getTokenAnterior();
            Expr operadorADireita = negacaoOuNegativo();
            return new ExprUnaria(operador, operadorADireita);
        }

        return chamarExpressao();
    }

    private Expr chamarExpressao() {
        Expr expressao = valorExpressao();

        while (true) {
            if (correspondente(TipoToken.PAREN_ESQ)) {
                expressao = terminarChamadaExpressao(expressao);
            } else {
                break;
            }
        }

        return expressao;
    }

    private Expr terminarChamadaExpressao(Expr expressaoChamada) {
        List<Expr> argumentosDaExpressao = new ArrayList<>();
        if (!verificaTipo(TipoToken.PAREN_DIR)) {
            do {
                argumentosDaExpressao.add(atribuicao());
            } while (correspondente(TipoToken.VIRGULA));
        }

        Token parenteses = consumirToken(TipoToken.PAREN_DIR,
                "Depois dos argumentos, espera-se ')'.");

        return new Chamada(expressaoChamada, parenteses, argumentosDaExpressao);
    }

    private Expr valorExpressao() {
        if (correspondente(TipoToken.FALSO)) {
            return new Literal(false);
        }

        if (correspondente(TipoToken.VERDADEIRO)) {
            return new Literal(true);
        }

        if (correspondente(TipoToken.NULO)) {
            return new Literal(null);
        }

        if (correspondente(TipoToken.NUMERO, TipoToken.STRING)) {
            return new Literal(getTokenAnterior().getLiteral());
        }

        if (correspondente(TipoToken.IDENTIFICADOR)) {
            return new Var(getTokenAnterior());
        }

        if (correspondente(TipoToken.PAREN_ESQ)) {
            Expr expressao = atribuicao();
            consumirToken(TipoToken.PAREN_DIR, "Depois de uma expressao, espera-se ')'");
            return new Agrupamento(expressao);
        }

        throw erroNoParse(getTokenAtualNaLista(), "Espera-se uma expressao.");
    }

    private boolean correspondente(TipoToken... types) {
        for (TipoToken type : types) {
            if (verificaTipo(type)) {
                avancarNaListaDeTokens();
                return true;
            }
        }

        return false;
    }

    private Token consumirToken(TipoToken type, String message) {
        if (verificaTipo(type)) return avancarNaListaDeTokens();

        throw erroNoParse(getTokenAtualNaLista(), message);
    }

    private boolean verificaTipo(TipoToken type) {
        if (terminouCodigoFonte()) return false;
        return getTokenAtualNaLista().getTipo() == type;
    }

    private Token avancarNaListaDeTokens() {
        if (!terminouCodigoFonte()) {
            posicaoAtuaNaLista++;
        }
        return getTokenAnterior();
    }

    private boolean terminouCodigoFonte() {
        return getTokenAtualNaLista().getTipo() == TipoToken.FIM_DO_ARQUIVO;
    }

    private Token getTokenAtualNaLista() {
        return listaDeTokens.get(posicaoAtuaNaLista);
    }

    private Token getTokenAnterior() {
        return listaDeTokens.get(posicaoAtuaNaLista - 1);
    }

    private ErroDeParse erroNoParse(Token token, String message) {
        Principal.erroNaExecucao(token, message);
        return new ErroDeParse();
    }

    private void sincronizarAposErroEncontradoNoParse() {
        avancarNaListaDeTokens();

        while (!terminouCodigoFonte()) {
            if (getTokenAnterior().getTipo() == TipoToken.PONTO_VIRG) {
                return;
            }

            if (getTokenAtualNaLista().getTipo() == TipoToken.FUNCAO ||
                    getTokenAtualNaLista().getTipo() == TipoToken.VAR ||
                    getTokenAtualNaLista().getTipo() == TipoToken.SE ||
                    getTokenAtualNaLista().getTipo() == TipoToken.PARA ||
                    getTokenAtualNaLista().getTipo() == TipoToken.ENQUANTO ||
                    getTokenAtualNaLista().getTipo() == TipoToken.IMPRIMIR ||
                    getTokenAtualNaLista().getTipo() == TipoToken.RETORNAR) {
                return;
            }

            avancarNaListaDeTokens();
        }
    }
}
