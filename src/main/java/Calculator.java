import org.apache.commons.lang3.tuple.Pair;
import org.juancampos.Validator;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 * Synopsys Java Candidate Homework
 * Calculator program in Java that evaluates expressions in a very simple integer expression language.
 * The program takes an input on the command line, computes the result, and prints it to the console
 */
public class Calculator implements Callable<Integer> {
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

    @Override
    public Integer call() throws Exception {
        logger.info(LOGLEVEL + loglevel);
        Validator validator = new Validator();
        String calculateCommand = getCommandString(logger);
        Pair<Boolean, String> validatorResultContainer = validator.validate(calculateCommand);
        int result = 0;
        if(validatorResultContainer.getLeft()){
            //Calculate
        } else {
            logger.info(validatorResultContainer.getRight());
        }
        System.out.println(result);
        return result;
    }

    private String getCommandString(Logger logger) {
        StringBuilder calculateCommandBuilder = new StringBuilder();
        String calculateCommand = "";
        if (operations != null) {
            for (StringBuilder operation : operations) {
                calculateCommandBuilder.append(operation);
            }
            calculateCommand = calculateCommandBuilder.toString();
        }

        logger.info(THE_OPERATIONS_COMMAND_IS + calculateCommand);
        return calculateCommand;
    }
}




