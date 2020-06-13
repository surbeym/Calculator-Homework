package org.juancampos.enums;

public enum Operators {
    ADD('+'),
    SUB('_'),
    MULT('*'),
    DIV('/'),
    LET('#'),
    NEGATIVE('-');

    private final char symbol;

    Operators(char symbol) {
        this.symbol = symbol;
    }
    public char getSymbol(){
        return symbol;
    }
}
