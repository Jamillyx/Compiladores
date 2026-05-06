package compilador.sintatico;

import compilador.lexico.ClasseToken;
import compilador.lexico.Lexico;
import compilador.lexico.Token;

public class Sintatico {
    private Lexico lexico;
    private Token token;
    private String arquivoCodigo;

    public Sintatico(String arquivoCodigo) {
        this.arquivoCodigo = arquivoCodigo;

    }

    public void analisar() {
        lexico = new Lexico(arquivoCodigo);
        token = lexico.getNexToken();
        programa();

    }

    // <programa> ::= program id ; <corpo> .
    private void programa() {
        if (ehPalavraReservada("program")) {
            token = lexico.getNexToken();
            if (token.getClasse() == ClasseToken.Identificador) {
                token = lexico.getNexToken();
                if (token.getClasse() == ClasseToken.PontoVirgula) {
                    token = lexico.getNexToken();
                    corpo();
                    if (token.getClasse() == ClasseToken.Ponto) {
                        token = lexico.getNexToken();
                    } else {
                        throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                                token.getColuna() + "] Erro sintático=> Faltou ponto final no programa (.)");
                    }
                } else {
                    throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                            token.getColuna()
                            + "] Erro sintático=> Faltou ponto e vírgula depois do nome do programa (;)");
                }
            } else {
                throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                        token.getColuna() + "] Erro sintático=> Faltou identificar o nome do programa (id)");
            }
        } else {
            throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                    token.getColuna() + "] Erro sintático=> Faltou começar o programa (program)");
        }

    }

    private void corpo() {
        declarar();
        // rotina
        // A44
        if (ehPalavraReservada("begin")) {
            token = lexico.getNexToken();
            sentencas();
            if (ehPalavraReservada("end")) {
                token = lexico.getNexToken();
            } else {
                throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                        token.getColuna() + "] Erro sintático=> Faltou finalizar o programa (end)");
            }
        } else {
            throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                    token.getColuna() + "] Erro sintático=> Faltou begin para iniciar o programa (begin)");
        }

    }

    private void dvar() {
        variaveis();
        if (token.getClasse() == ClasseToken.DoisPontos) {
            token = lexico.getNexToken();
            tipo_var();

        } else {
            throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                    token.getColuna() + "] Erro sintático=> Faltou dois ponto na declaração de vriáveis");
        }

    }

    private void declarar() {
        if (ehPalavraReservada("var")) {
            token = lexico.getNexToken();
            dvar();
            mais_dc();
        }
    }

    private void mais_dc() {
        if (token.getClasse() == ClasseToken.PontoVirgula) {
            token = lexico.getNexToken();
            cont_dc();
        } else {
            throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                    token.getColuna() + "] Erro sintático=> Faltou ponto e virgula");
        }
        // epsilon - pode não haver mais declarações
    }

    private void cont_dc() {
        if (token.getClasse() == ClasseToken.Identificador) {
            dvar();
            mais_dc();
        }
    }

    private void variaveis() {
        if (token.getClasse() == ClasseToken.Identificador) {
            token = lexico.getNexToken();
            //
            mais_var();
        } else {
            throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                    token.getColuna() + "] Erro sintático=> Faltou um identificador de variável)");
        }

    }

    private void tipo_var() {
        if (ehPalavraReservada("integer")) {
            token = lexico.getNexToken();
        } else {
            throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                    token.getColuna() + "] Erro sintático=> Faltou o tipo das variaveis (integer)");
        }

    }

    private void mais_var() {
        if (token.getClasse() == ClasseToken.Virgula) {
            token = lexico.getNexToken();
            variaveis();
        }

    }

    private void sentencas() {
        comando();
        mais_sentencas();
    }

    private void mais_sentencas() {
        if (token.getClasse() == ClasseToken.PontoVirgula) {
            token = lexico.getNexToken();
            cont_sentencas();
        } else {
            throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                    token.getColuna() + "] Erro sintático=> Faltou pont e virgula");
        }
        // epsilon - pode terminar a sequência de sentenças
    }

    private void cont_sentencas() {
        if (ehPalavraReservada("read") || ehPalavraReservada("write") ||
                ehPalavraReservada("writeln") || ehPalavraReservada("for") ||
                ehPalavraReservada("repeat") || ehPalavraReservada("while") ||
                ehPalavraReservada("if") || token.getClasse() == ClasseToken.Identificador) {
            sentencas();
                }
    }

    private void comando() {

        if (ehPalavraReservada("read")) {
            token = lexico.getNexToken();
            if (token.getClasse() == ClasseToken.AbreParentese) {
                token = lexico.getNexToken();
                var_read();
                if (token.getClasse() == ClasseToken.FechaParentese) {
                    token = lexico.getNexToken();
                } else {
                    throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                            token.getColuna() + "] Erro sintático=> Faltou fechar o parenteses");
                }
            } else {
                throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                        token.getColuna() + "] Erro sintático=> Faltou abrir o parentese");
            }

        } else if (ehPalavraReservada("write")) {
            token = lexico.getNexToken();
            if (token.getClasse() == ClasseToken.AbreParentese) {

                token = lexico.getNexToken();
                exp_write();
                if (token.getClasse() == ClasseToken.FechaParentese) {
                    token = lexico.getNexToken();
                } else {
                    throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                            token.getColuna() + "] Erro sintático=> Faltou fechar o parenteses");
                }
            } else {
                throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                        token.getColuna() + "] Erro sintático=> Faltou abrir o parentese");
            }

        } else if (ehPalavraReservada("writeln")) {
            token = lexico.getNexToken();
            if (token.getClasse() == ClasseToken.AbreParentese) {
                token = lexico.getNexToken();
                exp_write();
                if (token.getClasse() == ClasseToken.FechaParentese) {
                    token = lexico.getNexToken();
                } else {
                    throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                            token.getColuna() + "] Erro sintático=> Faltou fechar o parenteses");
                }
            } else {
                throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                        token.getColuna() + "] Erro sintático=> Faltou abrir o parentese");
            }
        } else if (ehPalavraReservada("for")) {
            token = lexico.getNexToken();
            if (token.getClasse() == ClasseToken.Identificador) {
                token = lexico.getNexToken();
                if (token.getClasse() == ClasseToken.Atribuicao) {
                    token = lexico.getNexToken();
                    expressao();
                    if (ehPalavraReservada("to")) {
                        token = lexico.getNexToken();
                        expressao();
                        if (ehPalavraReservada("do")) {
                            token = lexico.getNexToken();
                            if (ehPalavraReservada("begin")) {
                                token = lexico.getNexToken();
                                sentencas();
                                if (ehPalavraReservada("end")) {
                                    token = lexico.getNexToken();
                                } else {
                                    throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                                            token.getColuna() + "] Erro sintático=> Faltou o end");
                                }

                            } else {
                                throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                                        token.getColuna() + "] Erro sintático=> Faltou o begin");
                            }

                        } else {
                            throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                                    token.getColuna() + "] Erro sintático=> Faltou o do");
                        }
                    } else {
                        throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                                token.getColuna() + "] Erro sintático=> Faltou o to");
                    }
                } else {
                    throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                            token.getColuna() + "] Erro sintático=> Faltou a atribuição");
                }
            } else {
                throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                        token.getColuna() + "] Erro sintático=> Faltou o identificador");
            }
        } else if (ehPalavraReservada("repeat")) {
            token = lexico.getNexToken();
            sentencas();
            if (ehPalavraReservada("until")) {
                token = lexico.getNexToken();
                if (token.getClasse() == ClasseToken.AbreParentese) {
                    token = lexico.getNexToken();
                    expressao_logica();
                    if (token.getClasse() == ClasseToken.FechaParentese) {
                        token = lexico.getNexToken();
                    } else {
                        throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                                token.getColuna() + "] Erro sintático=> Faltou o fechar parentese");
                    }
                } else {
                    throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                            token.getColuna() + "] Erro sintático=> Faltou abrir parentese");
                }
            } else {
                throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                        token.getColuna() + "] Erro sintático=> Faltou until");
            }

        } else if (ehPalavraReservada("while")) {
            token = lexico.getNexToken();
            if (token.getClasse() == ClasseToken.AbreParentese) {
                token = lexico.getNexToken();
                expressao_logica();
                if (token.getClasse() == ClasseToken.FechaParentese) {
                    token = lexico.getNexToken();
                    if (ehPalavraReservada("do")) {
                        token = lexico.getNexToken();
                        if (ehPalavraReservada("begin")) {
                            token = lexico.getNexToken();
                            sentencas();
                            if (ehPalavraReservada("end")) {
                                token = lexico.getNexToken();
                            } else {
                                throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                                        token.getColuna() + "] Erro sintático=> Faltou end");
                            }

                        } else {
                            throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                                    token.getColuna() + "] Erro sintático=> Faltou begin");
                        }

                    } else {
                        throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                                token.getColuna() + "] Erro sintático=> Faltou do");
                    }
                } else {
                    throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                            token.getColuna() + "] Erro sintático=> Faltou o fechar parentese");
                }
            } else {
                throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                        token.getColuna() + "] Erro sintático=> Faltou abrir parentese");
            }
        } else if (ehPalavraReservada("if")) {
            token = lexico.getNexToken();
            if (token.getClasse() == ClasseToken.AbreParentese) {
                token = lexico.getNexToken();
                expressao_logica();
                if (token.getClasse() == ClasseToken.FechaParentese) {
                    token = lexico.getNexToken();
                    if (ehPalavraReservada("then")) {
                        token = lexico.getNexToken();
                        if (ehPalavraReservada("begin")) {
                            token = lexico.getNexToken();
                            sentencas();
                            if (ehPalavraReservada("end")) {
                                token = lexico.getNexToken();
                                pfalsa();
                            } else {
                                throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                                        token.getColuna() + "] Erro sintático=> Faltou end");
                            }
                        } else {
                            throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                                    token.getColuna() + "] Erro sintático=> Faltou begin");
                        }

                    } else {
                        throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                                token.getColuna() + "] Erro sintático=> Faltou  then");
                    }
                } else {
                    throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                            token.getColuna() + "] Erro sintático=> Faltou fechar parentese");
                }
            } else {
                throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                        token.getColuna() + "] Erro sintático=> Faltou abrir parentese");
            }
        } else if (token.getClasse() == ClasseToken.Identificador) {
            token = lexico.getNexToken();
            if (token.getClasse() == ClasseToken.Atribuicao) {
                token = lexico.getNexToken();
                expressao();
            } else {
                throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                        token.getColuna() + "] Erro sintático=> Faltou a atribuição");
            }
        } else {
            throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                    token.getColuna() + "] Erro sintático=> Faltou um comando ");
        }

    }

    private void var_read() {
        if (token.getClasse() == ClasseToken.Identificador) {
            token = lexico.getNexToken();
            mais_var_read();
        } else {
            throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                    token.getColuna() + "] Erro sintático=> Faltou o identificador");
        }

    }

    private void mais_var_read() {
        if (token.getClasse() == ClasseToken.Virgula) {
            token = lexico.getNexToken();
            var_read();
        }
    }

    // <exp_write> ::= id {A09} <mais_exp_write> |
    // string {A59} <mais_exp_write> |
    // intnum {A43} <mais_exp_write>
    private void exp_write() {
        if (token.getClasse() == ClasseToken.Identificador) {
            token = lexico.getNexToken();
            mais_exp_write();
        } else if (token.getClasse() == ClasseToken.String) {
            token = lexico.getNexToken();
            mais_exp_write();
        } else if (token.getClasse() == ClasseToken.Inteiro) {
            token = lexico.getNexToken();
            mais_exp_write();
        } else {
            throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                    token.getColuna() + "] Erro sintático=> Faltou o identificador ou string ou intnum");
        }
    }

    private void mais_exp_write() {
        if (token.getClasse() == ClasseToken.Virgula) {
            token = lexico.getNexToken();
            exp_write();
        }
    }

    private void expressao() {
        termo();
        mais_expressao();
    }

    private void termo() {
        fator();
        mais_termo();
    }

    private void mais_termo() {
        if (token.getClasse() == ClasseToken.Multiplicacao) {
            token = lexico.getNexToken();
            fator();
            mais_termo();
        } else if (token.getClasse() == ClasseToken.Divisao) {
            token = lexico.getNexToken();
            fator();
            mais_termo();
        }

    }

    private void fator() {
        if (token.getClasse() == ClasseToken.Identificador) {
            token = lexico.getNexToken();
        } else if (token.getClasse() == ClasseToken.Inteiro) {
            token = lexico.getNexToken();
        } else if (token.getClasse() == ClasseToken.AbreParentese) {
            token = lexico.getNexToken();
            expressao();
            if (token.getClasse() == ClasseToken.FechaParentese) {
                token = lexico.getNexToken();
            } else {
                throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                        token.getColuna()
                        + "] Erro sintático=> Faltou Fechar parenteses");
            }
        } else {
            throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                    token.getColuna()
                    + "] Erro sintático=> Faltou um inteiro ou Abrir parenteses ou ID ou Inteiro");
        }

    }

    private void mais_expressao() {
        if (token.getClasse() == ClasseToken.Soma) {
            token = lexico.getNexToken();
            termo();
            mais_expressao();
        } else if (token.getClasse() == ClasseToken.Subtracao) {
            token = lexico.getNexToken();
            termo();
            mais_expressao();
        }
    }

    private void expressao_logica() {
        termo_logico();
        mais_expr_logica();
    }

    private void mais_expr_logica() {
        if (ehPalavraReservada("or")) {
            token = lexico.getNexToken();
            termo_logico();
            mais_expr_logica();
        }
    }

    private void termo_logico() {
        fator_logico();
        mais_termo_logico();
    }

    private void mais_termo_logico() {
        if (ehPalavraReservada("and")) {
            token = lexico.getNexToken();
            fator_logico();
            mais_termo_logico();
        }
    }

    private void fator_logico() {
        
        // Verificamos os casos específicos primeiro porque eles são "únicos"
        if (token.getClasse() == ClasseToken.AbreParentese) {
            token = lexico.getNexToken();
            expressao_logica();
            if (token.getClasse() == ClasseToken.FechaParentese) {
                token = lexico.getNexToken();
            } else {
                throw new RuntimeException("[Linha = " + token.getLinha() + ", coluna=" + token.getColuna()
                        + "] Erro sintático=> Faltou fechar parênteses ')'");
            }
        } else if (ehPalavraReservada("not")) {
            token = lexico.getNexToken();
            fator_logico(); // A28
        } else if (ehPalavraReservada("true")) {
            token = lexico.getNexToken(); // A29
        } else if (ehPalavraReservada("false")) {
            token = lexico.getNexToken(); // A30
        } else {
            relacional();
        }

    }

    private void relacional() {
        expressao();
        if (token.getClasse() == ClasseToken.Igualdade) {
            token = lexico.getNexToken();
            expressao();
        } else if (token.getClasse() == ClasseToken.Maior) {
            token = lexico.getNexToken();
            expressao();
        } else if (token.getClasse() == ClasseToken.MaiorIgual) {
            token = lexico.getNexToken();
            expressao();
        } else if (token.getClasse() == ClasseToken.Menor) {
            token = lexico.getNexToken();
            expressao();
        } else if (token.getClasse() == ClasseToken.MenorIgual) {
            token = lexico.getNexToken();
            expressao();
        } else if (token.getClasse() == ClasseToken.Diferente) {
            token = lexico.getNexToken();
            expressao();
        } else {
            throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                    token.getColuna() + "] Erro sintático=> Faltou um operador relacional");
        }
        // O operador relacional é opcional na grammar
    }

    private void pfalsa() {
        if (ehPalavraReservada("else")) {
            token = lexico.getNexToken();
            if (ehPalavraReservada("begin")) {
                token = lexico.getNexToken();
                sentencas();
                if (ehPalavraReservada("end")) {
                    token = lexico.getNexToken();
                } else {
                    throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                            token.getColuna() + "] Erro sintático=> Faltou o end");
                }
            } else {
                throw new RuntimeException("[Linha = " + token.getLinha() + " , coluna=" +
                        token.getColuna() + "] Erro sintático=> Faltou o begin");
            }
        }
        // epsilon - else é opcional
    }

    private boolean ehPalavraReservada(String palavra) {
        return token.getClasse() == ClasseToken.PalavraReservada &&
                token.getValor().getTexto().equalsIgnoreCase(palavra);
    }

}