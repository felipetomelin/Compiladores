package com.example.teste;

import java.io.File;

public class ArquivosService {

    private static final String TXT_EXTENSION = ".txt";
    private static final String IL_EXTENSION = ".il";
    private final String nomeArquivo;
    private final String pathArquivo;

    public ArquivosService(File arquivo) {
        this(removerExtensoes(arquivo.getName()), arquivo.getParent());
    }

    public ArquivosService(String nomeArquivo, String pathArquivo) {
        this.nomeArquivo = nomeArquivo;
        this.pathArquivo = pathArquivo;
    }

    private static String removerExtensoes(String nomeArquivo) {
        if (nomeArquivo.indexOf(".") > 0) {
            return nomeArquivo.substring(0, nomeArquivo.lastIndexOf("."));
        } else {
            return nomeArquivo;
        }
    }

    public String getNomeArquivoTxt() {
        return this.getNomeArquivo() + TXT_EXTENSION;
    }
    public String getNomeArquivoIl() {
        return this.getNomeArquivo() + IL_EXTENSION;
    }
    private String getNomeArquivo() {
        return this.nomeArquivo;
    }

    public String getPathArquivoTxt() {
        return this.getPathArquivo() + "\\" + this.getNomeArquivoTxt();
    }
    public String getPathArquivoIl() {
        return this.getPathArquivo() + "\\" + this.getNomeArquivoIl();
    }
    private String getPathArquivo() {
        return this.pathArquivo;
    }

}
