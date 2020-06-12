import org.apache.commons.lang3.tuple.Pair;
import org.juancampos.CalculatorEngine;
import org.juancampos.Validator;
import org.juancampos.enums.Operators;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Synopsys Java Candidate Homework
 * Calculator program in Java that evaluates expressions in a very simple integer expression language.
 * The program takes an input on the command line, computes the result, and prints it to the console.
 * The application uses the Command Line Interface CLI to parse the command line.
 * The application uses log4j2 for logging.
 */
public class Calculator implements Callable<Long> {
    public static final String THE_OPERATIONS_COMMAND_IS = "The operations command is: ";
    public static final String LOGLEVEL = "Loglevel   : ";
    @CommandLine.Option(names = "-l")
    List<String> loglevel;

    @CommandLine.Parameters
    List<StringBuilder> operations;


    public static void main(String[] args) {
        new CommandLine(new Calculator()).execute(args);
    }

    public static final Logger logger = Logger.getLogger(Calculator.class.getName());

    /**
     * Picocli overriden method to call the calculation.
     * First step is to validate the command
     * Then if valid call the calculator engine
     * to get the result.
     * The method can throw a runtime exception
     * for invalid arithmetic operations
     * @return A long value with the result. Even though individual operands are int
     * a value can be calculated that is bigger then Integer.MAX_VALUE
     * @throws Exception It can throw a runtime calculator exception
     */
    @Override
    public Long call() throws Exception {
        logger.info(LOGLEVEL + loglevel);
        Validator validator = new Validator();
        String calculateCommand = getCommandString(operations);
        Pair<Boolean, String> validatorResultContainer = validator.validate(calculateCommand);
        long result = 0;
        if(validatorResultContainer.getLeft()){
            result = CalculatorEngine.calculate(calculateCommand);
        } else {
            logger.log(Level.SEVERE,validatorResultContainer.getRight());
        }
        System.out.println(result);
        return result;
    }


    /**
     * The method will format a command string to replace multi-letter word
     * command with a single character. This will allow faster processing and
     * also use a single character as a trigger as opposed to have to parse the line
     * to look ahead for the command.
     * @param operations The list of tokens that represent the string in operation from the command line
     * @return A formatted string.
     */
    String getCommandString(List<StringBuilder> operations) {
        StringBuilder calculateCommandBuilder = new StringBuilder();
        String calculateCommand = "";
        if (operations != null) {
            for (StringBuilder operation : operations) {
                calculateCommandBuilder.append(operation);
            }
            calculateCommand = calculateCommandBuilder.toString().toUpperCase();
            calculateCommand = calculateCommand.replaceAll("\\s+","");
            calculateCommand = calculateCommand.replaceAll("ADD+", Operators.ADD.getSymbol());
            calculateCommand = calculateCommand.replaceAll("SUB+", Operators.SUB.getSymbol());
            calculateCommand = calculateCommand.replaceAll("MULT+",Operators.MULT.getSymbol());
            calculateCommand = calculateCommand.replaceAll("DIV+",Operators.DIV.getSymbol());
            calculateCommand = calculateCommand.replaceAll("LET+",Operators.LET.getSymbol());
        }

        logger.info(THE_OPERATIONS_COMMAND_IS + calculateCommand);
        return calculateCommand;
    }
}




