package org.juancampos.enums;

public enum Operators {
    ADD("+"),
    SUB("_"),
    MULT("*"),
    DIV("/"),
    LET("#"),
    NEGATIVE("-");

    private final String symbol;

    Operators(String symbol) {
        this.symbol = symbol;
    }
    public String getSymbol(){
        return symbol;
    }
}
