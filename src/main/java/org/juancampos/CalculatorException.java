package org.juancampos;

public class CalculatorException extends RuntimeException{
    public CalculatorException() {
        super();
    }

    public CalculatorException(String s) {
        super(s);
    }

    public CalculatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CalculatorException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = -5365630128855087664L;
}
