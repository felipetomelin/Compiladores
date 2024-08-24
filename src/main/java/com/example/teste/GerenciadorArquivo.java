package com.example.teste;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.Optional;

public class GerenciadorArquivo {

    private ArquivosPrograma arquivosPrograma;
    private static final String QUEBRA_LINHA = System.lineSeparator();

    public GerenciadorArquivo() {
        this.arquivosPrograma = null;
    }

    public String getPathArquivoCodigoFonte() {
        return this.arquivosPrograma.getPathArquivoCodigoFonte();
    }

    public void novoArquivo() {
        this.arquivosPrograma = null;
    }

    public String carregarConteudoArquivo() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extension);
        File arquivo = fileChooser.showOpenDialog(null);
        if (arquivo != null) {
            this.arquivosPrograma = new ArquivosPrograma(arquivo);
            return this.obterConteudoArquivo();
        }
        return null;
    }

    private String obterConteudoArquivo() {
        StringBuilder conteudo = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(this.arquivosPrograma.getPathArquivoCodigoFonte()))) {
            String linha = reader.readLine();
            while (linha != null) {
                conteudo.append(linha).append(QUEBRA_LINHA);
                linha = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return conteudo.toString();
    }

    public void salvarArquivoCodigoFonte(String conteudo) {
        if (this.temArquivoCarregado()) {
            this.salvarArquivoCodigoFonteExistente(conteudo);
        } else {
            this.salvarArquivoCodigoFonteNovo(conteudo);
        }
    }

    public boolean temArquivoCarregado() {
        return this.arquivosPrograma != null;
    }

    private void salvarArquivoCodigoFonteNovo(String conteudo) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nome do arquivo");
        dialog.setHeaderText("Digite o nome do novo arquivo a ser criado");
        dialog.setContentText("Nome do arquivo:");
        Optional<String> resposta = dialog.showAndWait();
        if (resposta.isPresent()) {
            String nomeArquivo = resposta.get();
            String pathArquivo = this.obterPathArquivoNovo();
            if (pathArquivo == null) {
                return;
            }
            this.arquivosPrograma = new ArquivosPrograma(nomeArquivo, pathArquivo);
            boolean sucesso = this.salvarArquivo(conteudo, this.arquivosPrograma.getPathArquivoCodigoFonte());
            this.feedbackSalvarArquivo(sucesso);
        }
    }

    private void salvarArquivoCodigoFonteExistente(String conteudo) {
        boolean sucesso = this.salvarArquivo(conteudo, this.arquivosPrograma.getPathArquivoCodigoFonte());
        this.feedbackSalvarArquivo(sucesso);
    }

    private String obterPathArquivoNovo() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File diretorio = directoryChooser.showDialog(null);
        return diretorio != null ? diretorio.getPath() : null;
    }

    private void feedbackSalvarArquivo(boolean sucesso) {
        Alert feedbackAlert;
        if (sucesso) {
            feedbackAlert = new Alert(Alert.AlertType.INFORMATION);
            feedbackAlert.setTitle("Arquivo salvo");
            feedbackAlert.setHeaderText("Arquivo salvo com sucesso");
            feedbackAlert.setContentText("Arquivo salvo em " + this.arquivosPrograma.getPathArquivoCodigoFonte());
        } else {
            feedbackAlert = new Alert(Alert.AlertType.ERROR);
            feedbackAlert.setTitle("Erro");
            feedbackAlert.setHeaderText("Ocorreu um erro ao salvar o arquivo");
        }
        feedbackAlert.showAndWait();
    }

    public void gerarArquivoCodigoObjeto(String conteudo) {
        this.salvarArquivo(conteudo, this.arquivosPrograma.getPathArquivoCodigoObjeto());
    }

    private boolean salvarArquivo(String conteudoArquivo, String pathArquivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathArquivo))) {
            writer.append(conteudoArquivo);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
