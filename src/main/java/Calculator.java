import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.juancampos.services.CalculatorService;
import org.juancampos.services.ICalculatorService;
import org.juancampos.services.IValidatorService;
import org.juancampos.services.ValidatorService;
import org.juancampos.enums.Operators;
import org.juancampos.services.ILogService;
import org.juancampos.services.LogService;
import picocli.CommandLine;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Java Candidate Homework
 * Calculator program in Java that evaluates expressions in a very simple integer expression language.
 * The program takes an input on the command line, computes the result, and prints it to the console.
 * The application uses the Command Line Interface CLI to parse the command line.
 * The application uses log4j2 for logging.
 */
public class Calculator implements Callable<Long> {
    public static final String ENTERING_MAIN_ROUTINE_OF_CALCULATOR = "Entering Main Routine of Calculator";
    public static final String RAW_OPERATIONS_TEXT_FROM_COMMAND_LINE = "Raw operations text from command line = {0}";
    public static final String CALCULATE_COMMAND_SENT_TO_CALCULATOR = "Calculate command sent to calculator = {0}";
    public static final String PROCESSED_CALCULATOR_COMMAND = "Processed Calculator Command = {0}";

    @CommandLine.Option(names = {"-l", "--loglevel"}, description = "set the loglevel in the logger.Log levels allowed are ERROR, INFO and DEBUG. Default is set to INFO")
    String loglevel;

    @CommandLine.Parameters (description = "The command for the calculator. The command is accepted if it's in quotes. A valid command is \"add(1,2)\"")
    List<StringBuilder> operations;


    public static void main(String[] args) {
        LOGGER.debug(ENTERING_MAIN_ROUTINE_OF_CALCULATOR);
        new CommandLine(new Calculator()).execute(args);
    }
     private static final Logger LOGGER = LogManager.getLogger(Calculator.class.getName());

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
        ILogService logService = LogService.getInstance();
        logService.setLogLevel(loglevel);
        IValidatorService validator = ValidatorService.getInstance();
        String calculateCommand = getCommandString(operations);
        boolean validCharactersCommand = validator.validateCharacters(calculateCommand);
        long result = 0;
        if (validCharactersCommand){
            calculateCommand = substituteOperators(calculateCommand);
            boolean validatorResultContainer = validator.validate(calculateCommand);
            if(validatorResultContainer) {
                ICalculatorService calculatorEngine = CalculatorService.getInstance();
                result = calculatorEngine.calculate(calculateCommand);
            }
            LOGGER.info("RESULT = " + result);
            System.out.println("RESULT = " + result);
        }
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
        LOGGER.debug(MessageFormat.format(RAW_OPERATIONS_TEXT_FROM_COMMAND_LINE,operations));
        StringBuilder calculateCommandBuilder = new StringBuilder();
        String calculateCommand = "";
        if (operations != null) {
            for (StringBuilder operation : operations) {
                calculateCommandBuilder.append(operation);
            }
            LOGGER.info(MessageFormat.format(CALCULATE_COMMAND_SENT_TO_CALCULATOR, calculateCommandBuilder.toString()));
            calculateCommand = calculateCommandBuilder.toString().toUpperCase();
            calculateCommand = calculateCommand.replaceAll("\\s+", "");
        }
        LOGGER.debug(MessageFormat.format(PROCESSED_CALCULATOR_COMMAND,calculateCommand));
        return calculateCommand;
    }

    protected String substituteOperators(String calculateCommand) {
        if (StringUtils.isNotEmpty(calculateCommand)) {
            calculateCommand = calculateCommand.replaceAll("ADD+", String.valueOf(Operators.ADD.getSymbol()));
            calculateCommand = calculateCommand.replaceAll("SUB+", String.valueOf(Operators.SUB.getSymbol()));
            calculateCommand = calculateCommand.replaceAll("MULT+", String.valueOf(Operators.MULT.getSymbol()));
            calculateCommand = calculateCommand.replaceAll("DIV+", String.valueOf(Operators.DIV.getSymbol()));
            calculateCommand = calculateCommand.replaceAll("LET+", String.valueOf(Operators.LET.getSymbol()));
        }
        return calculateCommand;
    }
}




