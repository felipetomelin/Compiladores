package com.example.teste;

import com.example.teste.gals.LexicalError;
import com.example.teste.gals.Lexico;
import com.example.teste.gals.Token;
import org.fxmisc.richtext.CodeArea;

import static com.example.teste.gals.ScannerConstants.SCANNER_ERROR;

public class AnalisadorService {
    private Lexico lexico;
    private String quebraLinha = "\n";

    public String Compilar(CodeArea codeArea) {
        this.lexico = new Lexico();
        this.lexico.setInput(codeArea.getText());

        StringBuilder stringBuilder = new StringBuilder()
                .append(FormatarDireita("linha", 10))
                .append(FormatarDireita("classe", 30))
                .append("lexema")
                .append(this.quebraLinha);
        try {
            Token token;
            while ((token = lexico.nextToken()) != null) {
                if (token.getId() != 35)
                stringBuilder
                        .append(FormatarDireita(String.valueOf(ObterLinha(codeArea, token.getPosition())), 10))
                        .append(FormatarDireita(ObterTipo(token.getId()), 30))
                        .append(token.getLexeme())
                        .append(this.quebraLinha);
            }
            stringBuilder
                    .append("programa compilado com sucesso")
                    .append(this.quebraLinha);
        } catch (LexicalError e) {
            stringBuilder = new StringBuilder()
                    .append("linha ")
                    .append(ObterLinha(codeArea, e.getPosition())).append(":")
                    .append(" - ")
                    .append(FormatarMensagemErro(e, codeArea.getText()))
                    .append(this.quebraLinha);
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

    private String FormatarDireita(String texto, int quantidade) {
        return String.format("%-" + quantidade + "s", texto);
    }

    private String ObterTipo(int id) {
        switch (id) {
            case 36:
                return "identificador";
            case 32:
                return "constante int";
            case 33:
                return "constante float";
            case 34:
                return "constante string";
        }
        if (id >= 3 && id <= 15) {
            return "palavra reservada";
        }
        if (id >= 16 && id <= 31) {
            return "símbolo especial";
        }
        return "Não implementado";
    }

    private String FormatarMensagemErro(LexicalError error, String texto) {
        return SCANNER_ERROR[0].equals(error.getMessage())
                ? texto.charAt(error.getPosition()) + " " + error.getMessage()
                : error.getMessage();
    }
}
