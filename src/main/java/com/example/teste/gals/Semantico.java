package com.example.teste.gals;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class Semantico implements Constants
{
    private static final String QUEBRA_LINHA = System.lineSeparator();
    private static final String FLOAT_64 = "float64";
    private static final String INT_64 = "int64";
    private static final String CHAR = "char";
    private static final String STRING = "string";
    private static final String BOOL = "bool";

    private final StringBuilder codigoObjeto = new StringBuilder();
    private final Stack<String> pilhaTipos = new Stack<>();
    private final HashMap<String, String> tabelaSimbolos = new HashMap<>();
    private final LinkedList<String> listaIds = new LinkedList<>();
    private final Stack<String> pilhaRotulos = new Stack<>();
    private int qtdRotulos = 1;
    private String tipoVar = "";
    private String operador = "";
    private Token tokenAtual;

    public void executeAction(int action, Token token)	throws SemanticError
    {
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
            //REVISAR
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
                        throw new SemanticError(identificaor + " nao declarado", identificaor.length());
                    }

                    this.codigoObjeto.append(QUEBRA_LINHA).append("stloc").append(identificaor);
                }
                this.listaIds.clear();
                break;
            case 104:
                this.listaIds.add(token.getLexeme());
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
//            case :
//                tipo1 = this.pilhaTipos.pop();
//                verifyIsNumberType(tipo1);
//                this.pilhaTipos.push(tipo1);
//                break;
            case 131:
                tipo1 = this.pilhaTipos.pop();
                verifyIsNumberType(tipo1);
                this.pilhaTipos.push(tipo1);
                this.codigoObjeto.append(QUEBRA_LINHA).append("ldc.i8 ").append(-1);
                this.codigoObjeto.append(QUEBRA_LINHA).append("conv.r8");
                this.codigoObjeto.append(QUEBRA_LINHA).append("mul");
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
                    throw new SemanticError("tipos incompatíveis em expressão relacional", this.tokenAtual.getPosition());
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
            case 108:
                tipo1 = this.pilhaTipos.pop();

                if (tipo1.equals(INT_64)) {
                    this.codigoObjeto.append(QUEBRA_LINHA).append("conv.i8");
                }

                this.codigoObjeto.append(QUEBRA_LINHA).append("call void [mscorlib]System.Console::Write(").append(tipo1).append(")");
                break;
            case 107:
                this.codigoObjeto.append(QUEBRA_LINHA).append("ldstr ").append("\"\\n\"");
                this.codigoObjeto.append(QUEBRA_LINHA).append("call void [mscorlib]System.Console::Write(string)");
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
//            case :
//                tipo1 = this.pilhaTipos.pop();
//                tipo2 = this.pilhaTipos.pop();
//                verificarDivisao(tipo1, tipo2);
//                this.codigoObjeto
//                        .append(QUEBRA_LINHA).append("div")
//                        .append(QUEBRA_LINHA).append("conv.i8");
//                break;
//            case :
//                this.pilhaTipos.push(CHAR);
//                this.codigoObjeto.append(QUEBRA_LINHA).append("ldstr ");
//
//                switch (token.getLexeme()) {
//                    case "\\n":
//                        this.codigoObjeto.append("\"\\n\"");
//                        break;
//                    case "\\s":
//                        this.codigoObjeto.append("\" \"");
//                        break;
//                    case "\\t":
//                        this.codigoObjeto.append("\"\\t\"");
//                        break;
//                }
//                break;
            case 130:
                this.pilhaTipos.push(STRING);
                this.codigoObjeto.append(QUEBRA_LINHA).append("ldstr ").append(token.getLexeme());
                break;
//            case 109: // REVISAR
//                this.codigoObjeto.append(QUEBRA_LINHA).append("brfalse ").append(this.criarRotulo());
//                break;
            case 109: // REVISAR
                String rotuloAtual = this.pilhaRotulos.pop();
                String rotuloNovo = this.criarRotulo();
                this.codigoObjeto.append(QUEBRA_LINHA).append("br ").append(rotuloNovo);
                this.codigoObjeto.append(QUEBRA_LINHA).append(rotuloAtual).append(":");
                break;
//            case :
//                this.codigoObjeto.append(QUEBRA_LINHA).append(this.pilhaRotulos.pop()).append(":");
//                break;
//            case :
//                this.codigoObjeto.append(QUEBRA_LINHA).append(this.criarRotulo()).append(":");
//                break;
//            case :
//                this.codigoObjeto.append(QUEBRA_LINHA).append("brtrue ").append(this.pilhaRotulos.pop());
//                break;
//            case :
//                switch (token.getLexeme()) {
//                    case "int":
//                        this.tipoVar = INT_64;
//                        break;
//                    case "float":
//                        this.tipoVar = FLOAT_64;
//                        break;
//                    case "boolean":
//                        this.tipoVar = BOOL;
//                        break;
//                    default:
//                        this.tipoVar = token.getLexeme();
//                        break;
//                }
//                break;
//            case 102 ou 104:
//                for (String listaId : this.listaIds) {
//                    this.tabelaSimbolos.put(listaId, this.tipoVar);
//                    this.codigoObjeto.append(QUEBRA_LINHA).append(".locals (").append(this.tipoVar).append(" ").append(listaId).append(")");
//                }
//                this.listaIds.clear();
//                break;
            case 127: // REVISAR
                id = token.getLexeme();
                tipoId = this.tabelaSimbolos.get(id);

                this.pilhaTipos.push(tipoId);
                this.codigoObjeto.append(QUEBRA_LINHA).append("ldloc ").append(id);

                if (tipoId.equals(INT_64)) {
                    this.codigoObjeto.append(QUEBRA_LINHA).append("conv.r8");
                }
                break;
//            case : NAO SEI QUAL EH
//                id = this.listaIds.pop();
//                tipoId = this.tabelaSimbolos.get(id);
//                this.pilhaTipos.pop();
//
//                if (tipoId.equals(INT_64)) {
//                    this.codigoObjeto.append(QUEBRA_LINHA).append("conv.i8");
//                }
//                this.codigoObjeto.append(QUEBRA_LINHA).append("stloc ").append(id);
//                break;
            case 105: // revisar tb
                for (String listaId : this.listaIds) {
                    tipoId = this.tabelaSimbolos.get(listaId);
                    String classe = "";
                    switch (tipoId) {
                        case INT_64:
                            classe = "Int64";
                            break;
                        case FLOAT_64:
                            classe = "Double";
                            break;
                    }
                    this.codigoObjeto.append(QUEBRA_LINHA).append("call string [mscorlib]System.Console::ReadLine()");
                    this.codigoObjeto.append(QUEBRA_LINHA).append("call ").append(tipoId).append(" [mscorlib]System.").append(classe).append("::Parse(string)");
                    this.codigoObjeto.append(QUEBRA_LINHA).append("stloc ").append(listaId);
                }
                this.listaIds.clear();
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
            throw new SemanticError("tipo(s) incompatível(is) em expressão aritmética", this.tokenAtual.getPosition());
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
            throw new SemanticError("tipo(s) incompatível(is) em expressão lógica", this.tokenAtual.getPosition());
        }
    }

    private void verificarBool(String tipo1, String tipo2) throws SemanticError {
        if (tipo1.equals(BOOL) && tipo2.equals(BOOL)) {
            this.pilhaTipos.push(BOOL);
        } else {
            throw new SemanticError("tipo(s) incompatível(is) em expressão lógica", this.tokenAtual.getPosition());
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
