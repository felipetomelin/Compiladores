package com.example.teste;

import com.example.teste.gals.*;
import org.fxmisc.richtext.CodeArea;

import static com.example.teste.gals.ScannerConstants.SCANNER_ERROR;

public class AnalisadorService {
    private Lexico lexico;
    private String quebraLinha = "\n";
    public boolean ProgramaCompiladoSucesso;

    Sintatico sintatico = new Sintatico();
    Semantico semantico = new Semantico();

    public String Compilar(CodeArea codeArea) {
        this.lexico = new Lexico();
        this.lexico.setInput(codeArea.getText());

        StringBuilder stringBuilder = new StringBuilder();
        try {
            sintatico.parse(lexico, semantico);    // tradução dirigida pela sintaxe
            stringBuilder
                    .append("programa compilado com sucesso")
                    .append(this.quebraLinha);
            this.ProgramaCompiladoSucesso = true;
        } catch (LexicalError e) {
            stringBuilder = new StringBuilder()
                    .append("Erro na linha ")
                    .append(ObterLinha(codeArea, e.getPosition()))
                    .append(" - ")
                    .append(FormatarMensagemErro(e, codeArea.getText()))
                    .append(this.quebraLinha);
        } catch (SyntaticError e) {
            System.out.println(e.getPosition() + " símbolo encontrado: na entrada " + e.getMessage());
            stringBuilder = new StringBuilder()
                    .append("Erro na linha ")
                    .append(ObterLinha(codeArea, e.getPosition()))
                    .append(" - ")
                    .append("encontrado ")
                    .append(this.sintatico.getCurrentToken())
                    .append(" ")
                    .append(e.getMessage());
        } catch (SemanticError e) {
            stringBuilder = new StringBuilder()
                    .append("Erro na linha ")
                    .append(ObterLinha(codeArea, e.getPosition()))
                    .append(" - ")
                    .append(e.getMessage());
        }

        return stringBuilder.toString();
    }

    private int ObterLinha(CodeArea codeArea, int posicao) {
        try {
            codeArea.selectRange(posicao, posicao);
        } catch (IndexOutOfBoundsException e) {
            codeArea.selectRange(0, 0);
        }
        return codeArea.getCaretSelectionBind().getParagraphIndex() + 1;
    }

    private String FormatarMensagemErro(LexicalError error, String texto) {
        return SCANNER_ERROR[0].equals(error.getMessage())
                ? texto.charAt(error.getPosition()) + " " + error.getMessage()
                : error.getMessage();
    }

    public String getCodigoObjeto() {
        return this.semantico.getCodigoObjeto().toString();
    }

}
