package com.example.teste;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;

import java.net.URL;
import java.util.ResourceBundle;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

@SuppressWarnings("restriction")
public class HelloController implements Initializable {
    private final GerenciadorArquivo gerenciadorArquivo = new GerenciadorArquivo();

    @FXML
    private CodeArea codeInput;

    @FXML
    private VirtualizedScrollPane<CodeArea> virtualScroll;

    @FXML
    private TextArea txtaDebug;

    @FXML
    private Label lFileName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        codeInput.setParagraphGraphicFactory(LineNumberFactory.get(codeInput));

        virtualScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        virtualScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        txtaDebug.setEditable(false);
    }

    @FXML
    public Button btnNovo;

    public void onBtnNovoAction() {
        this.gerenciadorArquivo.NovoArquivo();
        codeInput.clear();
        txtaDebug.clear();
        lFileName.setText("");
    }

    @FXML
    public Button btnAbrir;

    public void onBtnAbrirAction() {
        String conteudoArquivo = this.gerenciadorArquivo.carregarConteudoArquivo();
        if (conteudoArquivo != null) {
            lFileName.setText(this.gerenciadorArquivo.GetPathArquivoCodigoFonte());
            codeInput.clear();
            codeInput.appendText(conteudoArquivo);
        }
    }

    @FXML
    public Button btnSalvar;

    public void onBtnSalvarAction() {
        this.gerenciadorArquivo.SalvarArquivoCodigoFonte(codeInput.getText());
        if (this.gerenciadorArquivo.TemArquivoCarregado()) {
            lFileName.setText(this.gerenciadorArquivo.GetPathArquivoCodigoFonte());
        }
        txtaDebug.clear();
    }

    @FXML
    public Button btnCopiar;

    public void onBtnCopiarAction() {
        codeInput.copy();
    }


    @FXML
    public Button btnColar;

    public void onBtnColarAction() {
        codeInput.paste();
    }


    @FXML
    public Button btnRecortar;

    public void onBtnRecortarAction() {
        codeInput.cut();
    }

    @FXML
    public Button btnCompilar;

    public void onBtnCompilarAction() {
        AnalisadorService analisadorService = new AnalisadorService();
        String mensagemRetorno = analisadorService.Compilar(codeInput);

        if (analisadorService.ProgramaCompiladoSucesso) {
            this.gerenciadorArquivo.gerarArquivoCodigoObjeto(analisadorService.getCodigoObjeto());
        }

        sendDebugMessage(mensagemRetorno);
    }

    @FXML
    public Button btnEquipe;

    public void onBtnEquipeAction() {
        sendDebugMessage("Alunos que desenvolveram este compilador: Felipe Tomelin, Felipe Augusto de Carvalho de Araújo, Gustavo Celso Bozzano\n");
    }

    public void sendDebugMessage(String msg) {
        String debug = txtaDebug.getText();
        debug += msg;
        txtaDebug.setText(debug);
    }

    private final KeyCombination keyBtnNovo = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
    private final KeyCombination keyBtnAbrir = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
    private final KeyCombination keyBtnSalvar = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
    private final KeyCombination keyBtnCopiar = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
    private final KeyCombination keyBtnColar = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);
    private final KeyCombination keyBtnRecortar = new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN);
    private final KeyCode keyBtnCompilar = KeyCode.F7;
    private final KeyCode keyBtnEquipe = KeyCode.F1;

    public void handleKeyEvents(KeyEvent event) {
        if (this.keyBtnNovo.match(event)) {
            this.onBtnNovoAction();
        }
        if (this.keyBtnAbrir.match(event)) {
            this.onBtnAbrirAction();
        }
        if (this.keyBtnSalvar.match(event)) {
            this.onBtnSalvarAction();
        }
        if (this.keyBtnCopiar.match(event)) {
            this.onBtnCopiarAction();
        }
        if (this.keyBtnColar.match(event)) {
            this.onBtnColarAction();
        }
        if (this.keyBtnRecortar.match(event)) {
            this.onBtnRecortarAction();
        }
        if (event.getCode().equals(this.keyBtnCompilar)) {
            this.onBtnCompilarAction();
        }
        if (event.getCode().equals(this.keyBtnEquipe)) {
            this.onBtnEquipeAction();
        }
    }
}