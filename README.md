# Submission of the Java Candidate Homework at Synopsys by Juan Campos


This is my implementation for the simple command line calculator.
## Assignment
#### Functional Requirements
Write a calculator program in Java that evaluates expressions in a very simple integer expression language. 
The program takes an input on the command line, computes the result, and prints it to the console.  For example:

``% java calculator.Main "add(2, 2)"``  
``4``

Few more examples:

| INPUT                                               	| OUTPUT 	|
|-----------------------------------------------------	|--------	|
| add(1, 2)                                           	| 3      	|
| add(1, mult(2, 3))                                  	| 7      	|
| mult(add(2, 2), div(9, 3))                          	| 12     	|
| let(a, 5, add(a, a))                                	| 10     	|
| let(a, 5, let(b, mult(a, 10), add(b, a)))           	| 55     	|
| let(a, let(b, 10, add(b, b)), let(b, 20, add(a, b)) 	| 40     	|

An expression is one of the following:
- Numbers: integers between Integer.MIN_VALUE and Integer.MAX_VALUE
- Variables: strings of characters, where each character is one of a-z, A-Z
- Arithmetic functions: add, sub, mult, div, each taking two arbitrary expressions as arguments.  In other words, each argument may be any of the expressions on this list.
- A “let” operator for assigning values to variables:
    - let(<variable name>, <value expression>, <expression where variable is used>)
    - As with arithmetic functions, the value expression and the expression where the variable is used may be an arbitrary expression from this list. 

#### Logging
Implement a logging layer to log all relevant information. Manage at least 3 levels of verbosity: INFO, ERROR, and DEBUG.  Allow verbosity to be set via a command-line option.
Build

#### Build
Create a Maven or Gradle build definition so your project may be built in any standard Java environment.



### Scope and assumptions
- The calculator will only work with int numbers. The operations may result in bigger numbers so the calculator can return numbers in the LONG range.
- Operations can handle negative numbers. The arithmetic operations can result in negative numbers, i.e. 1*(-1) = -1
- Division will be handled as an int division in java, the result will be rounded down to zero. 11/5 = 2.
- The only arithmetic operations allowed are:
    - Addition
    - Subtraction
    - Multiplication
    - Division
- LET expressions:
    - Variable names are limited to a-z and A-Z. They are, however, handled as case insensitive by the calculator. The variable name :    
```boo``` is the same as variable name ```BOO```
    - Variable names must be immediately followed by a comma. The calculator will throw an invalid argument exception if the variable is not followed by a comma. For example, these are violation:  
    ```let(BOO,5,add(BOO5,BOO))```  
    ```let(BOO4,1,add(BOO,BOO)```
- Logging
    - Logging can only be ERROR, INFO and DEBUG
    - Logging follows the [log4j2 hierarchy](https://stackoverflow.com/questions/7745885/log4j-logging-hierarchy-order)  
    - Logging option is case insensitive in the command line. ```-l debug``` is the same as ```-l DEBUG```

    

### Out of scope
- Floating numbers. The division will only result in a (Java) rounded down to zero no decimals number.
- Division by zero. Any division by zero is not handled, letting the natural arithmetic exception float to the output.
- Multi-line calculations. The complete entry in quotes will be considered the operation.
- Logging to a file. The logging is console only.

## Design
The calculator solution is based on a standard approach to a basic calculator problem.  
Basically the core of the solution uses two stacks:  
  - A stack for the operator
  - A stack for the numbers (operands)  

The calculator also has preferences for the operators.  
Multiplication and division before sum and subtraction.
The parenthesis are also operators. 

The basic approach is as follows:
Maintain two stacks, operator and operands. The string is parsed left to right. When a left parenthesis is reached , keep pushing the elements into the stacks. The values are calculated within the most inner parenthesis.  
 At the point a right parenthesis is reached, start popping out operators and operands and execute the operation. Push the result into the numbers stack. At the end of the process , the remaining number is the result.
 
 Usually in these calculators the syntax is as follows: 1+ (2+1). In the requirements here, operations are nested and the order is defined within the operation.  
 ```OPERATION(OPERAND1,OPERAND2)```.  
 In this case the operations can be nested and are solved based on the depth of the parenthesis. 
 Stacks were used as I preferred the solution that lets the recursion's be simulated by "popping" the stack to find the previous operation that must be executed to solve the current operation.
 
 
 #### Variable expression 
 The calculator has the wrinkle of using a variable expression.  
 Two artifacts were added to handle this. A map and an expression stack. The map was used to store variable names and values, as well as a flag to mark it as resolved if a value has been assigned.
 There is also an expression stack. It follows the same idea as the operations and operands stack. The top of the stack contains the variable name for the next variable expression that needs a value assigned. Since they are solved right to left, the stack will always have the very previous variable that needs solution.
 
 #### Parsing
 In order to parse the String, the operations are converted with the following substitution:  
 
 | INPUT                                               	| OUTPUT 	|
 |-----------------------------------------------------	|--------	|
 | add                                              	| +      	|
 | sub                                              	| _ (underscore)|
 | mult                                             	| *      	|
 | div                                              	| /      	|
 | let                                              	| #      	|
 
 This will allow to trigger actions just by parsing a single character.
 The string is parsed left to right. Using the stacks and maps approach means we only parse the string as far as calculating the operations once. There are also other parsings done during validation, but they are also one pass only. Therefore the space is O(XN) or in the end O(N).
 
 
 #### Validation
 
 The following scenarios are validated:
 - Empty operation command. The operation cannot be null or blank space(s)
 - Operation command can only start in one of the following formats. We will use the number 1 as a stand in for all numbers:
    - ADD(1,1)
    - SUB(1,1)
    - MULT(1,1)
    - DIV(1,1)
    - LET(1,1,1)  
    Any deviations from the start of the command will result in  0 and an invalid command message.
- Well formed parenthesis
    - All the parenthesis must be balanced. If there is an opening parenthesis there must be a closing parenthesis. The validator will check the whole input string without attempting to execute a command.  
    - The operation must have at a minimum a pair of parenthesis ```()``` 
- Invalid characters
    - Only alphanumeric characters (uppercase and lowercase) and the following characters are allowed ```(,)```. Any other characters in the input are invalid.  

- Variable names can only be [a-z][A-Z] If a variable name has a special character or a number it will be invalid. ```BAR5``` is invalid due to the trailing 5. 
 


## Getting Started

These instructions will get you a copy of the simple command line calculator application up and running on your local machine for evaluation purposes.


### Prerequisites
To run the executable jar the following needs to be installed to run from the command line
```
Java 8  at a minimum.
```
To execute the build from the command line the following needs to be installed on top of having Java 8
```
Maven 3
```  

To import the project into an IDE the host machine must have ```GIT``` installed.


### Cloning the project on IntelliJ  from GITHUB.
The Github repository is located at:
[Calculator Homework Repository](https://github.com/jpcampos/Calculator-Homework)

The repo can be cloned using the token provided in the email using the File->New Project-> Import from Version Control -> Github -> Use token.
The token will provide access to the repository Calculator


## Building the Application


* Execute the following command through the command line if maven is installed or using the Maven commands menu inside IntelliJ
```
mvn clean package
```

* The maven goal will  and build the jar file. This is the full runnable jar as it includes the dependencies.
```
Calculator-0.0.1-SNAPSHOT-with-dependencies.jar
```

## Running the Application
Navigate to the root folder of the application, or use the jar provided. If the jar is built, use the following command as an example.
```
java -jar target/Calculator-1.0-SNAPSHOT-jar-with-dependencies.jar "add(1,2)"  
RESULT = 3
```

#### Help command line
The application contains  help option. If the command line jar executable is called as follows, the jar will print out the help description
```Unknown option: '-h'
   Usage: <main class> [-l=<loglevel>] [<operations>...]
         [<operations>...]   The command for the calculator. The command is
                               accepted if it's in quotes. A valid command is "add
                               (1,2)"
     -l, --loglevel=<loglevel>
                             set the loglevel in the logger.Log levels allowed are
                               ERROR, INFO and DEBUG. Default is set to INFO  
```

#### Valid commands
The command must be contained within quotes. For example:
``"ADD(1,1)"``.

#### Logging Options
The command line accepts 3 logging options, which can be defined using:
```-l``` or ```--loglevel```  
The options are:  
    - ERROR : Will only print out the result or exceptions.  
    - INFO: Will provide a minimal level of logging information or also context for returning incorrect result.  
    - DEBUG: The most detailed level of information. 



## Running the tests

* Open the Project sidebar and navigate to the src/test/groovy folder
* Right click on the folder and choose the option:
```
Run "All Tests"
```
All the tests are written in spock. Run the tests and look at running console to get more information on what tests are doing during execution.


## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Spock](http://spockframework.org/) - A testing framework based on Groovy, choosing for the speed of unit and integration testing development as well as ease of adaption of TDD principles.
* [picocli](https://picocli.info/) - A mighty tiny command line interface
* [Apache Commons Lang](http://commons.apache.org/proper/commons-lang/) -  Apache Commons Lang provides extra methods to Java core classes.
* [Log4J2](https://logging.apache.org/log4j/2.x/) Apache log4j2 for logging capabilities.

## Authors

* Juan Campos  - *Initial work* - 

## Resources

