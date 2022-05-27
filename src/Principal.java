import model.generico.Token;
import model.instrucoes.Instrucao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import static java.text.MessageFormat.format;

public class Principal {

    private static final Logger log = Logger.getLogger(Principal.class.getName());
    private static final Interpretador INTERPRETADOR = new Interpretador();
    private static boolean houveAlgumErro = false;

    public static void main(String[] args) throws IOException {
        InputStream inputStream = System.in;
        InputStreamReader ir = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(ir);

        System.out.print("Digite o PATH do codigo que deseja interpretar: ");
        String path = bufferedReader.readLine();
        bufferedReader.close();

        var codigoFonte = Files.readAllBytes(Paths.get(path));
        System.out.println("=========================================================================================");
        System.out.println("Lendo arquivo '" + path + "'.");
        System.out.println("=========================================================================================");

        rodarCodigoFonte(new String(codigoFonte, Charset.defaultCharset()));
    }

    private static void rodarCodigoFonte(String source) throws IOException {
        // definindo funcoes padrao da linguagem
        var funcoesPadrao = Files.readAllBytes(Paths.get("funcoespadrao.txt"));
        Leitor leitor = new Leitor(new String(funcoesPadrao, Charset.defaultCharset()));
        List<Token> listaDeTokens = leitor.lerTokens();
        listaDeTokens.remove(listaDeTokens.size()-1); // remover o token que indica EOF (End Of File)

        // inicar leitura do codigo fonte em si
        leitor = new Leitor(source);
        listaDeTokens.addAll(leitor.lerTokens());

        Parser parser = new Parser(listaDeTokens);

        List<Instrucao> listaDeInstrucoes = parser.parseTokens();
        if (houveAlgumErro) return;

        Client definicaoDeEscopos = new Client(INTERPRETADOR);
        definicaoDeEscopos.definirEscopos(listaDeInstrucoes);

        if (houveAlgumErro) return;

        INTERPRETADOR.comecarInterpretacao(listaDeInstrucoes);
    }

    public static void erroNaExecucao(int linhaDoErro, String mensagem) {
        log.warning(format("linha {0} -> Erro: {1}", linhaDoErro, mensagem));
        houveAlgumErro = true;
    }

    public static void erroNaExecucao(Token token, String mensagem) {
        log.warning(format("linha {0} -> Erro em ''{1}'': {2}", token.getLinha(), token.getLexema(), mensagem));
        houveAlgumErro = true;
    }

}
