import model.generico.TipoToken;
import model.generico.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Leitor {

    private static final Map<String, TipoToken> palavrasChave;

    private final String codigoFonte;
    private List<Token> listaDeTokens = new ArrayList<>();

    // navegacao no arquivo
    private int linha = 1;
    private int inicio = 0;
    private int atual = 0;

    static {
        // definir as palavras-chave dos tokens padroes
        palavrasChave = new HashMap<>();
        palavrasChave.put("funcao", TipoToken.FUNCAO);
        palavrasChave.put("var", TipoToken.VAR);
        palavrasChave.put("ee", TipoToken.E);
        palavrasChave.put("ou", TipoToken.OU);
        palavrasChave.put("se", TipoToken.SE);
        palavrasChave.put("senao", TipoToken.SENAO);
        palavrasChave.put("verdadeiro", TipoToken.VERDADEIRO);
        palavrasChave.put("falso", TipoToken.FALSO);
        palavrasChave.put("para", TipoToken.PARA);
        palavrasChave.put("enquanto", TipoToken.ENQUANTO);
        palavrasChave.put("retornar", TipoToken.RETORNAR);
        palavrasChave.put("imprimir", TipoToken.IMPRIMIR);
        palavrasChave.put("nulo", TipoToken.NULO);
    }

    public Leitor(String codigoFonte) {
        this.codigoFonte = codigoFonte;
    }

    public List<Token> lerTokens() {
        while (!terminouCodigoFonte()) {
            inicio = atual;
            lerToken();
        }

        listaDeTokens.add(new Token(TipoToken.FIM_DO_ARQUIVO, "", null, linha));
        return listaDeTokens;
    }

    private void lerToken() {
        char c = avancarNoCodigoFonte();
        if (c == '{') {
            adicionarTokenNaLista(TipoToken.CHAVE_ESQ);
        } else if (c == '(') {
            adicionarTokenNaLista(TipoToken.PAREN_ESQ);
        } else if (c == '}') {
            adicionarTokenNaLista(TipoToken.CHAVE_DIR);
        } else if (c == ')') {
            adicionarTokenNaLista(TipoToken.PAREN_DIR);
        } else if (c == '.') {
            adicionarTokenNaLista(TipoToken.PONTO);
        } else if (c == ',') {
            adicionarTokenNaLista(TipoToken.VIRGULA);
        } else if (c == '-') {
            adicionarTokenNaLista(TipoToken.MENOS);
        } else if (c == '+') {
            adicionarTokenNaLista(TipoToken.MAIS);
        } else if (c == ';') {
            adicionarTokenNaLista(TipoToken.PONTO_VIRG);
        } else if (c == '*') {
            adicionarTokenNaLista(TipoToken.ASTERISCO);
            // tokens com dois caracteres
        } else if (c == '!') { // ! ou !=
            adicionarTokenNaLista(correspondente('=') ? TipoToken.EXCLAMACAO_IGUAL : TipoToken.EXCLAMACAO);
        } else if (c == '=') { // = ou ==
            adicionarTokenNaLista(correspondente('=') ? TipoToken.IGUAL_IGUAL : TipoToken.IGUAL);
        } else if (c == '<') { // < ou <=
            adicionarTokenNaLista(correspondente('=') ? TipoToken.MENOR_IGUAL : TipoToken.MENOR);
        } else if (c == '>') { // > ou >=
            adicionarTokenNaLista(correspondente('=') ? TipoToken.MAIOR_IGUAL : TipoToken.MAIOR);
        } else if (c == '/') { // divisao ou comentario
            if (correspondente('/')) {
                // comentario vai ate o final da linha
                while (getCharAtualNoCodigoFonte() != '\n' && !terminouCodigoFonte()) {
                    avancarNoCodigoFonte();
                }
            } else {
                adicionarTokenNaLista(TipoToken.BARRRA);
            }

        } else if (c == ' ' || c == '\r' || c == '\t') {
            // ignorar os espaco em branco
        } else if (c == '\n') {
            linha++;
        } else if (c == '"') { // comeco de uma string
            verificaString();
        } else {
            if (verificaDigito(c)) {
                verificaNumero();
            } else if (verificaLetra(c)) {
                identificador();
            } else {
                Principal.erroNaExecucao(linha, "Caracter nao esperado");
            }
        }
    }

    private void identificador() {
        while (verificaAlfaNumerico(getCharAtualNoCodigoFonte())) {
            avancarNoCodigoFonte();
        }

        String texto = codigoFonte.substring(inicio, atual);
        TipoToken tipo = palavrasChave.get(texto);
        if (tipo == null) {
            tipo = TipoToken.IDENTIFICADOR;
        }
        adicionarTokenNaLista(tipo);
    }

    private void verificaString() {
        while (getCharAtualNoCodigoFonte() != '"' && !terminouCodigoFonte()) {
            if (getCharAtualNoCodigoFonte() == '\n') {
                linha++;
            }
            avancarNoCodigoFonte();
        }

        if (terminouCodigoFonte()) {
            Principal.erroNaExecucao(linha, "String sem fim");
            return;
        }

        avancarNoCodigoFonte();

        String value = codigoFonte.substring(inicio + 1, atual - 1);
        adicionarTokenNaLista(TipoToken.STRING, value);
    }

    private boolean verificaDigito(char c) {
        return c >= '0' && c <= '9';
    }

    private void verificaNumero() {
        while (verificaDigito(getCharAtualNoCodigoFonte())) {
            avancarNoCodigoFonte();
        }

        if (getCharAtualNoCodigoFonte() == '.' && verificaDigito(verificarProximo())) {
            avancarNoCodigoFonte();

            while (verificaDigito(getCharAtualNoCodigoFonte())) {
                avancarNoCodigoFonte();
            }
        }

        adicionarTokenNaLista(TipoToken.NUMERO, Double.parseDouble(codigoFonte.substring(inicio, atual)));
    }

    private boolean correspondente(char esperado) {
        if (terminouCodigoFonte()) {
            return false;
        }
        if (codigoFonte.charAt(atual) != esperado) {
            return false;
        }

        atual++;
        return true;
    }

    private char getCharAtualNoCodigoFonte() {
        if (terminouCodigoFonte()) {
            return '\0';
        }
        return codigoFonte.charAt(atual);
    }

    private char verificarProximo() {
        if (atual + 1 >= codigoFonte.length()) {
            return '\0';
        }
        return codigoFonte.charAt(atual + 1);
    }

    private boolean verificaLetra(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean verificaAlfaNumerico(char c) {
        return verificaLetra(c) || verificaDigito(c);
    }

    private char avancarNoCodigoFonte() {
        return codigoFonte.charAt(atual++);
    }

    private boolean terminouCodigoFonte() {
        return atual >= codigoFonte.length();
    }

    private void adicionarTokenNaLista(TipoToken type) {
        adicionarTokenNaLista(type, null);
    }

    private void adicionarTokenNaLista(TipoToken type, Object literal) {
        String text = codigoFonte.substring(inicio, atual);
        listaDeTokens.add(new Token(type, text, literal, linha));
    }

}
