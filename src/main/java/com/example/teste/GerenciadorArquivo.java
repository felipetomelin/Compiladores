package com.example.teste;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.Optional;

public class GerenciadorArquivo {

    private ArquivosService arquivosService;
    private static final String QUEBRA_LINHA = System.lineSeparator();

    public GerenciadorArquivo() {
        this.arquivosService = null;
    }

    public String GetPathArquivoCodigoFonte() {
        return this.arquivosService.getPathArquivoTxt();
    }

    public void NovoArquivo() {
        this.arquivosService = null;
    }

    public String carregarConteudoArquivo() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extension);
        File arquivo = fileChooser.showOpenDialog(null);
        if (arquivo != null) {
            this.arquivosService = new ArquivosService(arquivo);
            return this.ObterConteudoArquivo();
        }
        return null;
    }

    private String ObterConteudoArquivo() {
        StringBuilder conteudo = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(this.arquivosService.getPathArquivoTxt()))) {
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

    public void SalvarArquivoCodigoFonte(String conteudo) {
        if (this.TemArquivoCarregado()) {
            this.SalvarArquivoCodigoFonteExistente(conteudo);
        } else {
            this.SalvarArquivoCodigoFonteNovo(conteudo);
        }
    }

    public boolean TemArquivoCarregado() {
        return this.arquivosService != null;
    }

    private void SalvarArquivoCodigoFonteNovo(String conteudo) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nome do arquivo");
        dialog.setHeaderText("Digite o nome do novo arquivo a ser criado");
        dialog.setContentText("Nome do arquivo:");
        Optional<String> resposta = dialog.showAndWait();
        if (resposta.isPresent()) {
            String nomeArquivo = resposta.get();
            String pathArquivo = this.ObterPathArquivoNovo();
            if (pathArquivo == null) {
                return;
            }
            this.arquivosService = new ArquivosService(nomeArquivo, pathArquivo);
            boolean sucesso = this.SalvarArquivo(conteudo, this.arquivosService.getPathArquivoTxt());
            this.FeedbackSalvarArquivo(sucesso);
        }
    }

    private void SalvarArquivoCodigoFonteExistente(String conteudo) {
        boolean sucesso = this.SalvarArquivo(conteudo, this.arquivosService.getPathArquivoTxt());
        this.FeedbackSalvarArquivo(sucesso);
    }

    private String ObterPathArquivoNovo() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File diretorio = directoryChooser.showDialog(null);
        return diretorio != null ? diretorio.getPath() : null;
    }

    private void FeedbackSalvarArquivo(boolean sucesso) {
        Alert feedbackAlert;
        if (sucesso) {
            feedbackAlert = new Alert(Alert.AlertType.INFORMATION);
            feedbackAlert.setTitle("Arquivo salvo");
            feedbackAlert.setHeaderText("Arquivo salvo com sucesso");
            feedbackAlert.setContentText("Arquivo salvo em " + this.arquivosService.getPathArquivoTxt());
        } else {
            feedbackAlert = new Alert(Alert.AlertType.ERROR);
            feedbackAlert.setTitle("Erro");
            feedbackAlert.setHeaderText("Ocorreu um erro ao salvar o arquivo");
        }
        feedbackAlert.showAndWait();
    }

    private boolean SalvarArquivo(String conteudoArquivo, String pathArquivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathArquivo))) {
            writer.append(conteudoArquivo);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
