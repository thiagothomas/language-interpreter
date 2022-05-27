import java.util.List;

interface FuncaoCallable {

    int numParametros();

    Object chamaFuncao(Interpretador interpreter, List<Object> arguments);

}
