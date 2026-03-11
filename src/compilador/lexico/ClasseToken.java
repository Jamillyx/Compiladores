package compilador.lexico;

public enum ClasseToken {
    Identificador,
    Inteiro,
    PalavraReservada,
    Soma,
    Subtracao,
    Divisao,
    Multiplicacao,
    Igualdade, // =
    Atribuicao, // :=
    Maior,
    MaiorIgual,
    Menor,
    MenorIgual,
    Diferente, //< >
    DoisPontos,
    PontoVirgula,
    Virgula,
    AbreParentese,
    FechaParentese,
    Ponto,
    String,
    EOF
}
