import model.excecoes.ErroEmExecucao;
import model.excecoes.Retorno;
import model.expressoes.*;
import model.generico.TipoToken;
import model.generico.Token;
import model.instrucoes.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Interpretador implements ExprCommand<Object>, InstrucoesCommand<Void> {

    private final Logger log = Logger.getLogger(Interpretador.class.getName());
    private final Contexto contextoGlobal = new Contexto();
    private Contexto contexto = contextoGlobal;

    private final Map<Expr, Integer> contextosLocais = new HashMap<>();

    public void comecarInterpretacao(List<Instrucao> listaDeInstrucoes) {
        try {
            for (Instrucao instrucao : listaDeInstrucoes) {
                executaInstrucao(instrucao);
            }
        } catch (ErroEmExecucao erroExecucao) {
            log.warning(MessageFormat.format("{0} -> linha {1}", erroExecucao.getMessage(), erroExecucao.getToken().getLinha()));
        }
    }

    @Override
    public Void commandInstrucaoBloco(BlocoDeCodigo blocoDeCodigo) {
        executaBlocoDeCodigo(blocoDeCodigo.getInstrucoes(), new Contexto(contexto));
        return null;
    }

    @Override
    public Void commandInstrucaoExpressao(Expressao expressao) {
        verificaExpressao(expressao.getExpressao());
        return null;
    }

    @Override
    public Void commandInstrucaoFuncao(Funcao funcao) {
        FuncaoFinal funcaoFinal = new FuncaoFinal(funcao, contexto);
        contexto.definirValor(funcao.getNome().getLexema(), funcaoFinal);
        return null;
    }

    @Override
    public Void commandInstrucaoSe(Se se) {
        if (objetoBooleanoVerdadeiro(verificaExpressao(se.getCondicao()))) {
            executaInstrucao(se.getEntaoCorpo());
        } else if (se.getSenaoCorpo() != null) {
            executaInstrucao(se.getSenaoCorpo());
        }
        return null;
    }

    @Override
    public Void commandInstrucaoImrpimir(Imprimir imprimir) {
        Object valorImpresso = verificaExpressao(imprimir.getExpressao());
        System.out.println(transformaEmString(valorImpresso));
        return null;
    }

    @Override
    public Void commandInstrucaoRetornar(Retornar retorno) {
        Object valorRetorno = null;
        if (retorno.getValor() != null) {
            valorRetorno = verificaExpressao(retorno.getValor());
        }

        throw new Retorno(valorRetorno);
    }

    @Override
    public Void commandInstrucaoVar(Variavel variavel) {
        Object valorVar = null;
        if (variavel.getInicializador() != null) {
            valorVar = verificaExpressao(variavel.getInicializador());
        }

        contexto.definirValor(variavel.getNome().getLexema(), valorVar);
        return null;
    }

    @Override
    public Void commandInstrucaoEnquanto(Enquanto enquanto) {
        while (objetoBooleanoVerdadeiro(verificaExpressao(enquanto.getCondicao()))) {
            executaInstrucao(enquanto.getCorpo());
        }
        return null;
    }

    @Override
    public Object commandExprAtribuicao(Atribuicao atribuicao) {
        Object valorAtribuicao = verificaExpressao(atribuicao.getValor());

        Integer profundidade = contextosLocais.get(atribuicao);
        if (profundidade != null) {
            contexto.atribuirValorNaProfundidade(profundidade, atribuicao.getNome(), valorAtribuicao);
        } else {
            contextoGlobal.atribuirValorVariavel(atribuicao.getNome(), valorAtribuicao);
        }

        return valorAtribuicao;
    }

    @Override
    public Object commandExprBinaria(ExprBinaria exprBinaria) {
        Object objetoEsquerda = verificaExpressao(exprBinaria.getEsquerda());
        Object objetoDireita = verificaExpressao(exprBinaria.getDireita());

        if (exprBinaria.getOperador().getTipo() == TipoToken.EXCLAMACAO_IGUAL) {
            return !objetosIguais(objetoEsquerda, objetoDireita);
        } else if (exprBinaria.getOperador().getTipo() == TipoToken.IGUAL_IGUAL) {
            return objetosIguais(objetoEsquerda, objetoDireita);
        } else if (exprBinaria.getOperador().getTipo() == TipoToken.MAIOR) {
            verificaSeSaoNumeros(exprBinaria.getOperador(), objetoEsquerda, objetoDireita);
            return (double) objetoEsquerda > (double) objetoDireita;
        } else if (exprBinaria.getOperador().getTipo() == TipoToken.MAIOR_IGUAL) {
            verificaSeSaoNumeros(exprBinaria.getOperador(), objetoEsquerda, objetoDireita);
            return (double) objetoEsquerda >= (double) objetoDireita;
        } else if (exprBinaria.getOperador().getTipo() == TipoToken.MENOR) {
            verificaSeSaoNumeros(exprBinaria.getOperador(), objetoEsquerda, objetoDireita);
            return (double) objetoEsquerda < (double) objetoDireita;
        } else if (exprBinaria.getOperador().getTipo() == TipoToken.MENOR_IGUAL) {
            verificaSeSaoNumeros(exprBinaria.getOperador(), objetoEsquerda, objetoDireita);
            return (double) objetoEsquerda <= (double) objetoDireita;
        } else if (exprBinaria.getOperador().getTipo() == TipoToken.MENOS) {
            verificaSeSaoNumeros(exprBinaria.getOperador(), objetoEsquerda, objetoDireita);
            return (double) objetoEsquerda - (double) objetoDireita;
        } else if (exprBinaria.getOperador().getTipo() == TipoToken.MAIS) {
            if (objetoEsquerda instanceof Double && objetoDireita instanceof Double) {
                return (double) objetoEsquerda + (double) objetoDireita;
            }
            if (objetoEsquerda instanceof String && objetoDireita instanceof String) {
                return objetoEsquerda + (String) objetoDireita;
            }

            throw new ErroEmExecucao(exprBinaria.getOperador(),
                    "Devem ser 2 numeros ou 2 strings");
        } else if (exprBinaria.getOperador().getTipo() == TipoToken.BARRRA) {
            verificaSeSaoNumeros(exprBinaria.getOperador(), objetoEsquerda, objetoDireita);
            return (double) objetoEsquerda / (double) objetoDireita;
        } else if (exprBinaria.getOperador().getTipo() == TipoToken.ASTERISCO) {
            verificaSeSaoNumeros(exprBinaria.getOperador(), objetoEsquerda, objetoDireita);
            return (double) objetoEsquerda * (double) objetoDireita;
        }

        return null;
    }

    @Override
    public Object commandExprChamada(Chamada chamada) {
        Object exprChamada = verificaExpressao(chamada.getExprChamada());

        List<Object> parametros = new ArrayList<>();
        for (Expr parametro : chamada.getArgumentos()) {
            parametros.add(verificaExpressao(parametro));
        }

        if (!(exprChamada instanceof FuncaoCallable)) {
            throw new ErroEmExecucao(chamada.getParenteses(), "Funcao nao definida");
        }

        FuncaoCallable funcao = (FuncaoCallable) exprChamada;

        if (parametros.size() != funcao.numParametros()) {
            throw new ErroEmExecucao(chamada.getParenteses(), "Eram esperados " +
                    funcao.numParametros() + " argumentos, mas foi recebido " +
                    parametros.size() + ".");
        }

        return funcao.chamaFuncao(this, parametros);
    }

    @Override
    public Object commandExprAgrupamento(Agrupamento agrupamento) {
        return verificaExpressao(agrupamento.getExpressao());
    }

    @Override
    public Object commandExprLiteral(Literal literal) {
        return literal.getValor();
    }

    @Override
    public Object commandExprLogica(Logica expressaoLogica) {
        Object objetoEsquerda = verificaExpressao(expressaoLogica.getEsquerda());

        if (expressaoLogica.getOperador().getTipo() == TipoToken.OU) {
            if (objetoBooleanoVerdadeiro(objetoEsquerda)) {
                return objetoEsquerda;
            }
        } else {
            if (!objetoBooleanoVerdadeiro(objetoEsquerda)) {
                return objetoEsquerda;
            }
        }

        return verificaExpressao(expressaoLogica.getDireita());
    }

    @Override
    public Object commandExprUnaria(ExprUnaria exprUnaria) {
        Object objetoDireita = verificaExpressao(exprUnaria.getDireita());

        if (exprUnaria.getOperador().getTipo() == TipoToken.EXCLAMACAO) {
            return !objetoBooleanoVerdadeiro(objetoDireita);
        } else if (exprUnaria.getOperador().getTipo() == TipoToken.MENOS) {
            verificaSeENumero(exprUnaria.getOperador(), objetoDireita);
            return -((double) objetoDireita);
        }

        return null;
    }

    @Override
    public Object commandExprVariavel(Var variavel) {
        return procuraVariavel(variavel.getNome(), variavel);
    }

    private Object procuraVariavel(Token nomeVariavel, Expr expressao) {
        Integer profundidade = contextosLocais.get(expressao);
        if (profundidade != null) {
            return contexto.receberValorNaProfundidade(profundidade, nomeVariavel.getLexema());
        } else {
            return contextoGlobal.retornarVariavel(nomeVariavel);
        }
    }

    private void verificaSeENumero(Token operador, Object operando) {
        if (operando instanceof Double) {
            return;
        }

        throw new ErroEmExecucao(operador, "Precisa ser um numero");
    }

    private void verificaSeSaoNumeros(Token operador, Object elementoEsquerda, Object elementoDireita) {
        if ((elementoEsquerda instanceof Double) && (elementoDireita instanceof Double)) {
            return;
        }

        throw new ErroEmExecucao(operador, "Precisam ser numeros");
    }

    private boolean objetoBooleanoVerdadeiro(Object objeto) {
        if (objeto == null) {
            return false;
        }
        if (objeto instanceof Boolean) {
            return (boolean) objeto;
        }
        return true;
    }

    public void executaBlocoDeCodigo(List<Instrucao> listaDeInstrucoes,
                                     Contexto contexto) {
        Contexto anterior = this.contexto;
        try {
            this.contexto = contexto;

            for (Instrucao instrucao : listaDeInstrucoes) {
                executaInstrucao(instrucao);
            }
        } finally {
            this.contexto = anterior;
        }
    }

    private boolean objetosIguais(Object objeto1, Object objeto2) {
        return (objeto1 == null && objeto2 == null) || (objeto1 != null && objeto1.equals(objeto2));
    }

    private void executaInstrucao(Instrucao instrucao) {
        instrucao.executar(this);
    }

    private Object verificaExpressao(Expr expressao) {
        return expressao.executar(this);
    }

    public void atribuiContexto(Expr expressao, int profundidade) {
        contextosLocais.put(expressao, profundidade);
    }

    private String transformaEmString(Object objeto) {
        if (objeto == null) return "nulo";

        if (objeto instanceof Double) {
            String texto = objeto.toString();
            if (texto.endsWith(".0")) {
                texto = texto.substring(0, texto.length() - 2);
            }
            return texto;
        }

        if (objeto instanceof Boolean) {
            return (objeto == Boolean.FALSE) ? "falso" : "verdadeiro";
        }
        return objeto.toString();
    }

}
