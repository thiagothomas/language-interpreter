funcao B(a, b) {
    se (a ee b) {
        retornar "primeiro";
    } senao se(a ou b) {
        retornar "segundo";
    } senao {
        retornar "teceiro";
    }

}

funcao X(a, b) {
    retornar a*b;
}

imprimir "inicio teste booleans";
imprimir B(verdadeiro, verdadeiro);
imprimir B(verdadeiro, falso);
imprimir B(falso, falso);
imprimir "fim teste booleans";

imprimir "inicio teste for";
para(var i=1; i<10; i=i+1) {
    imprimir(i);
}
imprimir "fim teste for";


var batata=1;
imprimir "inicio teste while";
enquanto(batata < 10) {
    imprimir(batata);
    batata = batata + 1;
}
imprimir "fim teste while";

imprimir "inicio teste funcao x";
imprimir (X(5,3));
imprimir "fim teste funcao x";

// -------------------------------------------

var y = "teste";

funcao func() {
    imprimir(y);
    retornar "xy";
}

imprimir batata;
var xy = func();
imprimir xy;

imprimir(!falso);

imprimir(verdadeiro == falso);

var zzzzz = zzzzz;