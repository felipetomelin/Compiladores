package com.example.teste;

import java.io.File;

public class ArquivosPrograma {

    private static final String TXT_EXTENSION = ".txt";
    private static final String IL_EXTENSION = ".il";
    private final String nomeArquivo;
    private final String pathArquivo;

    public ArquivosPrograma(File arquivo) {
        this(removerExtension(arquivo.getName()), arquivo.getParent());
    }

    public ArquivosPrograma(String nomeArquivo, String pathArquivo) {
        this.nomeArquivo = nomeArquivo;
        this.pathArquivo = pathArquivo;
    }

    private static String removerExtension(String nomeArquivo) {
        if (nomeArquivo.indexOf(".") > 0) {
            return nomeArquivo.substring(0, nomeArquivo.lastIndexOf("."));
        } else {
            return nomeArquivo;
        }
    }

    public String getNomeArquivoCodigoFonte() {
        return this.getNomeArquivo() + TXT_EXTENSION;
    }

    public String getNomeArquivoCodigoObjeto() {
        return this.getNomeArquivo() + IL_EXTENSION;
    }

    private String getNomeArquivo() {
        return this.nomeArquivo;
    }

    public String getPathArquivoCodigoFonte() {
        return this.getPathArquivo() + "\\" + this.getNomeArquivoCodigoFonte();
    }

    public String getPathArquivoCodigoObjeto() {
        return this.getPathArquivo() + "\\" + this.getNomeArquivoCodigoObjeto();
    }

    private String getPathArquivo() {
        return this.pathArquivo;
    }

}
