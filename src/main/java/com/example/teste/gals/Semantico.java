package com.example.teste.gals;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class Semantico implements Constants {
    private static final String QUEBRA_LINHA = System.lineSeparator();
    private static final String FLOAT_64 = "float64";
    private static final String INT_64 = "int64";
    private static final String CHAR = "char";
    private static final String STRING = "string";
    private static final String BOOL = "bool";

    public StringBuilder getCodigoObjeto() {
        return codigoObjeto;
    }

    private final StringBuilder codigoObjeto = new StringBuilder();
    private final Stack<String> pilhaTipos = new Stack<>();
    private final HashMap<String, String> tabelaSimbolos = new HashMap<>();
    private final LinkedList<String> listaIds = new LinkedList<>();
    private final Stack<String> pilhaRotulos = new Stack<>();
    private int qtdRotulos = 1;
    private String tipoVar = "";
    private String operador = "";
    private Token tokenAtual;

    public void executeAction(int action, Token token) throws SemanticError {
        this.tokenAtual = token;

        String tipo1 = "";
        String tipo2 = "";
        String id = "";
        String tipoId = "";

        switch (action) {
            case 100:
                this.codigoObjeto
                        .append(".assembly extern mscorlib {}").append(QUEBRA_LINHA)
                        .append(".assembly _codigo_objeto{}").append(QUEBRA_LINHA)
                        .append(".module   _codigo_objeto.exe").append(QUEBRA_LINHA)
                        .append(QUEBRA_LINHA)
                        .append(".class public _UNICA{ ").append(QUEBRA_LINHA)
                        .append(".method static public void _principal() {").append(QUEBRA_LINHA)
                        .append(".entrypoint");
                break;
            case 101:
                this.codigoObjeto
                        .append(QUEBRA_LINHA).append("ret")
                        .append(QUEBRA_LINHA).append("}")
                        .append(QUEBRA_LINHA).append("}");
                break;
            case 102:
                for (String listaId : this.listaIds) {

                    switch (listaId.substring(0, 1)) {
                        case "i":
                            this.tipoVar = INT_64;
                            break;
                        case "f":
                            this.tipoVar = FLOAT_64;
                            break;
                        case "b":
                            this.tipoVar = BOOL;
                            break;
                        case "s":
                            this.tipoVar = STRING;
                            break;
                    }

                    this.tabelaSimbolos.put(listaId, this.tipoVar);
                    this.codigoObjeto.append(QUEBRA_LINHA).append(".locals (").append(this.tipoVar).append(" ").append(listaId).append(")");
                }
                this.listaIds.clear();
                break;
            case 103:
                tipo1 = this.pilhaTipos.pop();

                if (tipo1.equals(INT_64)) {
                    this.codigoObjeto.append(QUEBRA_LINHA).append("conv.i8");
                }

                for (int i = 0; i < this.listaIds.size() - 1; i++) {
                    this.codigoObjeto.append(QUEBRA_LINHA).append("dup");
                }


                for (String identificaor : this.listaIds) {
                    if (!tabelaSimbolos.containsKey(identificaor)) {
                        throw new SemanticError(identificaor + " não declarado", this.tokenAtual.getPosition());
                    }

                    this.codigoObjeto.append(QUEBRA_LINHA).append("stloc ").append(identificaor);
                }
                this.listaIds.clear();
                break;
            case 104:
                this.listaIds.add(token.getLexeme());
                break;
            case 105:
                tipoId = this.tabelaSimbolos.get(token.getLexeme());

                if (tipoId == null) {
                    throw new SemanticError(token.getLexeme() + " não declarado" ,this.tokenAtual.getPosition());
                }


                String classe = switch (tipoId) {
                    case INT_64 -> "Int64";
                    case FLOAT_64 -> "Double";
                    default -> "";
                };

                this.codigoObjeto.append(QUEBRA_LINHA).append("call string [mscorlib]System.Console::ReadLine()");
                this.codigoObjeto.append(QUEBRA_LINHA).append("call ").append(tipoId).append(" [mscorlib]System.").append(classe).append("::Parse(string)");
                this.codigoObjeto.append(QUEBRA_LINHA).append("stloc ").append(token.getLexeme());

                this.listaIds.clear();
                break;
            case 106:
                this.codigoObjeto.append(QUEBRA_LINHA).append("ldstr ").append(token.getLexeme());
                this.codigoObjeto.append(QUEBRA_LINHA).append("call void [mscorlib]System.Console::Write(string)");
                break;
            case 107:
                this.codigoObjeto.append(QUEBRA_LINHA).append("ldstr ").append("\"\\n\"");
                this.codigoObjeto.append(QUEBRA_LINHA).append("call void [mscorlib]System.Console::Write(string)");
                break;
            case 108:
                tipo1 = this.pilhaTipos.pop();

                if (tipo1.equals(INT_64)) {
                    this.codigoObjeto.append(QUEBRA_LINHA).append("conv.i8");
                }

                this.codigoObjeto.append(QUEBRA_LINHA).append("call void [mscorlib]System.Console::WriteLine(").append(tipo1).append(")");
                break;
            case 109:
                this.criarRotulo();
                String rotuloNovo = this.criarRotulo();
                this.codigoObjeto.append(QUEBRA_LINHA).append("brfalse ").append(rotuloNovo);
                break;
            case 110:
                String rotulo2 = this.pilhaRotulos.pop();
                String rotulo1 = this.pilhaRotulos.pop();
                this.codigoObjeto.append(QUEBRA_LINHA).append("br ").append(rotulo1);
                this.pilhaRotulos.push(rotulo1);
                this.codigoObjeto.append(QUEBRA_LINHA).append(rotulo2).append(":");
                break;
            case 111:
                String rotulo = this.pilhaRotulos.pop();
                this.codigoObjeto.append(QUEBRA_LINHA).append(rotulo).append(":");
                break;
            case 112:
                rotuloNovo = this.criarRotulo();
                this.codigoObjeto.append(QUEBRA_LINHA).append("brfalse ").append(rotuloNovo);
                this.pilhaRotulos.push(rotuloNovo);
                break;
            case 113:
                rotulo = criarRotulo();
                this.codigoObjeto.append(QUEBRA_LINHA).append(rotulo).append(":");
                this.pilhaRotulos.push(rotulo);
                break;
            case 114:
                rotulo = this.pilhaRotulos.pop();
                this.codigoObjeto.append(QUEBRA_LINHA).append("brtrue ").append(rotulo);
                break;
            case 115:
                rotulo = this.pilhaRotulos.pop();
                this.codigoObjeto.append(QUEBRA_LINHA).append("brfalse ").append(rotulo);
                break;
            case 116:
                tipo1 = this.pilhaTipos.pop();
                tipo2 = this.pilhaTipos.pop();
                verificarBool(tipo1, tipo2);
                this.codigoObjeto.append(QUEBRA_LINHA).append("and");
                break;
            case 117:
                tipo1 = this.pilhaTipos.pop();
                tipo2 = this.pilhaTipos.pop();
                verificarBool(tipo1, tipo2);
                this.codigoObjeto.append(QUEBRA_LINHA).append("or");
                break;
            case 118:
                this.pilhaTipos.push(BOOL);
                this.codigoObjeto.append(QUEBRA_LINHA).append("ldc.i4.1");
                break;
            case 119:
                this.pilhaTipos.push(BOOL);
                this.codigoObjeto.append(QUEBRA_LINHA).append("ldc.i4.0");
                break;
            case 120:
                tipo1 = this.pilhaTipos.pop();
                verificarBool(tipo1);
                this.codigoObjeto.append(QUEBRA_LINHA).append("ldc.i4.1");
                this.codigoObjeto.append(QUEBRA_LINHA).append("xor");
                break;
            case 121:
                this.operador = token.getLexeme();
                break;
            case 122:
                tipo1 = this.pilhaTipos.pop();
                tipo2 = this.pilhaTipos.pop();

                if (tipo1.equals(tipo2)) {
                    this.pilhaTipos.push(BOOL);
                } else {
                    throw new SemanticError("tipos incompatíveis", this.tokenAtual.getPosition());
                }

                switch (this.operador) {
                    case ">":
                        this.codigoObjeto.append(QUEBRA_LINHA).append("cgt");
                        break;
                    case "<":
                        this.codigoObjeto.append(QUEBRA_LINHA).append("clt");
                        break;
                    case "==":
                        this.codigoObjeto.append(QUEBRA_LINHA).append("ceq");
                        break;
                }
                break;
            case 123:
                tipo1 = this.pilhaTipos.pop();
                tipo2 = this.pilhaTipos.pop();
                verificarTiposNumericos(tipo1, tipo2);
                this.codigoObjeto.append(QUEBRA_LINHA).append("add");
                break;
            case 124:
                tipo1 = this.pilhaTipos.pop();
                tipo2 = this.pilhaTipos.pop();
                verificarTiposNumericos(tipo1, tipo2);
                this.codigoObjeto.append(QUEBRA_LINHA).append("sub");
                break;
            case 125:
                tipo1 = this.pilhaTipos.pop();
                tipo2 = this.pilhaTipos.pop();
                verificarTiposNumericos(tipo1, tipo2);
                this.codigoObjeto.append(QUEBRA_LINHA).append("mul");
                break;
            case 126:
                tipo1 = this.pilhaTipos.pop();
                tipo2 = this.pilhaTipos.pop();
                verificarDivisao(tipo1, tipo2);
                this.codigoObjeto.append(QUEBRA_LINHA).append("div");
                break;
            case 127:
                tipoId = this.tabelaSimbolos.get(token.getLexeme());
                this.pilhaTipos.push(tipoId);
                this.codigoObjeto.append(QUEBRA_LINHA).append("ldloc ").append(token.getLexeme());

                if (tipoId.equals(INT_64)) {
                    this.codigoObjeto.append(QUEBRA_LINHA).append("conv.r8");
                }
                break;
            case 128:
                this.pilhaTipos.push(INT_64);
                String lexemaInt = formatarInt(token.getLexeme());
                this.codigoObjeto.append(QUEBRA_LINHA).append("ldc.i8 ").append(lexemaInt);
                this.codigoObjeto.append(QUEBRA_LINHA).append("conv.r8");
                break;
            case 129:
                this.pilhaTipos.push(FLOAT_64);
                String lexemaFloat = formatarFloat(token.getLexeme());
                this.codigoObjeto.append(QUEBRA_LINHA).append("ldc.r8 ").append(lexemaFloat);
                break;
            case 130:
                this.pilhaTipos.push(STRING);
                this.codigoObjeto.append(QUEBRA_LINHA).append("ldstr ").append(token.getLexeme());
                break;
            case 131:
                tipo1 = this.pilhaTipos.pop();
                verifyIsNumberType(tipo1);
                this.pilhaTipos.push(tipo1);
                this.codigoObjeto.append(QUEBRA_LINHA).append("ldc.i8 ").append(-1);
                this.codigoObjeto.append(QUEBRA_LINHA).append("conv.r8");
                this.codigoObjeto.append(QUEBRA_LINHA).append("mul");
                break;
        }
        this.tokenAtual = null;
    }

    private void verificarTiposNumericos(String tipo1, String tipo2) throws SemanticError {
        verifyIsNumberType(tipo1);
        verifyIsNumberType(tipo2);

        if (tipo1.equals(INT_64) && tipo2.equals(INT_64)) {
            this.pilhaTipos.push(INT_64);
        } else {
            this.pilhaTipos.push(FLOAT_64);
        }
    }

    private void verifyIsNumberType(String tipo1) throws SemanticError {
        if (!tipo1.equals(FLOAT_64) && !tipo1.equals(INT_64)) {
            throw new SemanticError("tipo(s) incompatível(is)", this.tokenAtual.getPosition());
        }
    }

    private void verificarDivisao(String tipo1, String tipo2) throws SemanticError {
        verifyIsNumberType(tipo1);
        verifyIsNumberType(tipo2);
        this.pilhaTipos.push(FLOAT_64);
    }

    private void verificarBool(String tipo1) throws SemanticError {
        if (tipo1.equals(BOOL)) {
            this.pilhaTipos.push(BOOL);
        } else {
            throw new SemanticError("tipo(s) incompatível(is)", this.tokenAtual.getPosition());
        }
    }

    private void verificarBool(String tipo1, String tipo2) throws SemanticError {
        if (tipo1.equals(BOOL) && tipo2.equals(BOOL)) {
            this.pilhaTipos.push(BOOL);
        } else {
            throw new SemanticError("tipo(s) incompatível(is)", this.tokenAtual.getPosition());
        }
    }

    private String formatarInt(String lexemaInt) {
        String[] partesInt = lexemaInt.split("d");
        if (partesInt.length > 1) {
            int baseMultiplicacao = Integer.parseInt(partesInt[0]);
            int expoente = Integer.parseInt(partesInt[1]);

            int resultado = (int) (baseMultiplicacao * Math.pow(10, expoente));
            lexemaInt = String.valueOf(resultado);
        }

        return lexemaInt;
    }

    private String formatarFloat(String lexemaFloat) {
        if (lexemaFloat.charAt(0) == '.') {
            lexemaFloat = "0" + lexemaFloat;
        }

        String[] partesFloat = lexemaFloat.split("d");
        if (partesFloat.length > 1) {
            double baseMultiplicacao = Double.parseDouble(partesFloat[0]);
            int expoente = Integer.parseInt(partesFloat[1]);

            double resultado = baseMultiplicacao * Math.pow(10, expoente);
            lexemaFloat = String.valueOf(resultado);
        }

        return lexemaFloat;
    }

    private String criarRotulo() {
        String rotulo = "L" + this.qtdRotulos;
        this.pilhaRotulos.push(rotulo);
        this.qtdRotulos++;
        return rotulo;
    }
}
