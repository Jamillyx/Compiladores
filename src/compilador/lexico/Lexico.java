package compilador.lexico;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Lexico {
    private static final char EOF = (char) 65535;
    private String nomeArquivo;
    private BufferedReader br;
    private char caractere;
    private static final List<String> palavrasReservadas = Arrays.asList("const", "type", "var", "begin", "end",
            "while", "do", "for", "downto", "if", "then", "else", "case", "of", "array", "function", "procedure",
            "label", "record", "exit", "break", "continue", "and", "or", "not", "program", "integer", "write",
            "writeln", "read", "repeat", "until", "to", "true", "false");
    private int linha;
    private int coluna;

    public Lexico(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
        linha = 1;
        coluna = 1;
        String caminhoArquivo = Paths.get(nomeArquivo).toAbsolutePath().toString();
        try {
            br = new BufferedReader(new FileReader(caminhoArquivo, StandardCharsets.UTF_8));
            caractere = (char) br.read();
        } catch (IOException ex) {
            System.out.println("Erro abrindo o arquivo " + nomeArquivo);
            System.out.println("Caminho do arquivo: " + caminhoArquivo);
        }
    }

    public Token getNexToken() {
        StringBuilder lexema;
        Token token;

        try {
            while (caractere != 65535) { // EOF
                lexema = new StringBuilder();
                token = new Token(linha, coluna);

                if (Character.isDigit(caractere)) {
                    while (Character.isDigit(caractere)) {
                        lexema.append(caractere);
                        caractere = (char) br.read();
                        coluna++;
                    }
                    token.setClasse(ClasseToken.Inteiro);
                    token.setValor(new ValorToken(Integer.parseInt(lexema.toString())));
                    return token;
                } else if (Character.isAlphabetic(caractere)) {
                    while (Character.isAlphabetic(caractere) || Character.isDigit(caractere)) {
                        lexema.append(caractere);
                        caractere = (char) br.read();
                        coluna++;
                    }
                    if (palavrasReservadas.contains(lexema.toString().toLowerCase())) {
                        token.setClasse(ClasseToken.PalavraReservada);
                    } else {
                        token.setClasse(ClasseToken.Identificador);
                    }
                    token.setValor(new ValorToken(lexema.toString().toLowerCase()));
                    return token;
                } else if (caractere == '+') {
                    token.setClasse(ClasseToken.Soma);
                    token.setValor(new ValorToken("+"));
                    caractere = (char) br.read(); // avança para o próx
                    coluna++;
                    return token;
                } else if (caractere == '-') {
                    token.setClasse(ClasseToken.Subtracao);
                    token.setValor(new ValorToken("-"));
                    caractere = (char) br.read(); // avança para o próx
                    coluna++;
                    return token;

                } else if (caractere == '/') {
                    token.setClasse(ClasseToken.Divisao);
                    token.setValor(new ValorToken("/"));
                    caractere = (char) br.read(); // avança para o próx
                    coluna++;
                    return token;

                } else if (caractere == '*') {
                    token.setClasse(ClasseToken.Multiplicacao);
                    token.setValor(new ValorToken("*"));
                    caractere = (char) br.read(); // avança para o próx
                    coluna++;
                    return token;

                } else if (caractere == '=') {
                    token.setClasse(ClasseToken.Igualdade);
                    token.setValor(new ValorToken("="));
                    caractere = (char) br.read(); // avança para o próx
                    coluna++;
                    return token;

                } else if (caractere == ':') {
                    caractere = (char) br.read();
                    if (caractere == '=') {
                        token.setClasse(ClasseToken.Atribuicao);
                        token.setValor(new ValorToken(":="));
                        caractere = (char) br.read();
                        coluna++;
                    } else {
                        token.setClasse(ClasseToken.DoisPontos);
                        token.setValor(new ValorToken(":"));
                        coluna++;
                    }

                    return token;

                } else if (caractere == '>') {
                    caractere = (char) br.read();
                    coluna++;
                    if (caractere == '=') {
                        token.setClasse(ClasseToken.MaiorIgual);
                        token.setValor(new ValorToken(">="));
                        caractere = (char) br.read();
                        coluna++;
                    } else {
                        token.setClasse(ClasseToken.Maior);
                        token.setValor(new ValorToken(">"));
                    }

                    return token;

                } else if (caractere == '<') {
                    caractere = (char) br.read();
                    coluna++;
                    if (caractere == '=') {
                        token.setClasse(ClasseToken.MenorIgual);
                        token.setValor(new ValorToken("<="));
                        caractere = (char) br.read();
                        coluna++;
                    } else if (caractere == '>') {
                        token.setClasse(ClasseToken.Diferente);
                        token.setValor(new ValorToken("<>"));
                        caractere = (char) br.read();
                        coluna++;
                    } else {
                        token.setClasse(ClasseToken.Menor);
                        token.setValor(new ValorToken("<"));
                    }

                    return token;

                } else if (caractere == ';') {
                    token.setClasse(ClasseToken.PontoVirgula);
                    token.setValor(new ValorToken(";"));
                    caractere = (char) br.read(); // avança para o próx
                    coluna++;
                    return token;
                } else if (caractere == ',') {
                    token.setClasse(ClasseToken.Virgula);
                    token.setValor(new ValorToken(","));
                    caractere = (char) br.read(); // avança para o próx
                    coluna++;
                    return token;
                } else if (caractere == '(') {
                    token.setClasse(ClasseToken.AbreParentese);
                    token.setValor(new ValorToken("("));
                    caractere = (char) br.read(); // avança para o próx
                    coluna++;
                    return token;

                } else if (caractere == ')') {
                    token.setClasse(ClasseToken.FechaParentese);
                    token.setValor(new ValorToken(")"));
                    caractere = (char) br.read(); // avança para o próx
                    coluna++;
                    return token;

                } else if (caractere == '.') {
                    token.setClasse(ClasseToken.Ponto);
                    token.setValor(new ValorToken("."));
                    caractere = (char) br.read(); // avança para o próx
                    coluna++;
                    return token;
                } else if (caractere == ' ' || caractere == '\t') {
                    caractere = (char) br.read();
                    coluna++;
                } else if (caractere == '\n') {
                    linha++;
                    coluna = 1;
                    caractere = (char) br.read();

                } else if (caractere == '\'') {
                    StringBuilder texto = new StringBuilder();
                    caractere = (char) br.read();
                    coluna++;

                    while (caractere != '\'' && caractere != EOF) {
                        if (caractere == '\n') {
                            System.err.println("String não fechada na linha" + linha);
                            break;
                        }
                        texto.append(caractere);
                        caractere = (char) br.read();
                        coluna++;
                    }
                    if (caractere == '\'') {
                        token.setClasse(ClasseToken.String);
                        token.setValor(new ValorToken(texto.toString()));
                        caractere = (char) br.read();
                        coluna++;
                        return token;
                    } else if (caractere == EOF) {
                        System.err.println("Fim do arquivo");
                        token = new Token(linha, coluna);
                        token.setClasse(ClasseToken.EOF);
                        return token;

                    }
                } else if (caractere == '{') {
                    while (caractere != '}' && caractere != EOF) {
                        if (caractere == '\n') {
                            linha++;
                            coluna = 1;
                        } else {
                            coluna++;
                        }
                        caractere = (char) br.read();

                    }
                    if (caractere == '}') {
                        caractere = (char) br.read();
                        coluna++;
                       // System.out.println("Deu erro?" + caractere);
                        return getNexToken();
                    } else if (caractere == EOF) {
                        System.err.println("não fechou o comentário,fim do arquivo");
                        token.setClasse(ClasseToken.EOF);
                        return token;

                    }

                }

            }
            token = new Token(linha, coluna);
            token.setClasse(ClasseToken.EOF);
            return token;
        } catch (

        IOException e) {
            System.err.println("Não foi possível ler do arquivo: " + nomeArquivo);
        }
        return null;
    }

}
