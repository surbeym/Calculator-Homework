# Submission of the Java Candidate Homework by Juan Campos


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
- Division will be handled as an int division in java, the result will be rounded down to zero. For example, 11/5 = 2.
- The only arithmetic operations allowed are:
    - Addition
    - Subtraction
    - Multiplication
    - Division
- LET expressions:
    - Variable names are limited to a-z and A-Z. They are, however, handled as case insensitive by the calculator. The variable name :    
```boo``` is the same as variable name ```BOO```
    - Variable names must be immediately followed by a comma. The calculator will throw an invalid argument exception if the variable is not followed by a comma. For example, these are violation:  
    ```let(BOO,5,add(BOO5,BOO))```  There is a number 5 after the last BOO variable.  
    ```let(BOO4,1,add(BOO,BOO)``` There is a number 4 right after the first BOO variable.  
- Logging
    - Logging can only be ERROR, INFO and DEBUG
    - Logging follows the [log4j2 hierarchy](https://stackoverflow.com/questions/7745885/log4j-logging-hierarchy-order)  
    - Logging option is case insensitive in the command line. ```-l debug``` is the same as ```-l DEBUG```

    

### Out of scope
- Floating numbers. The division will only result in a (Java) rounded down to zero with no decimals number.
- Division by zero. Any division by zero is not handled, letting the natural arithmetic exception float to the output.
- Multi-line calculations. The complete entry in quotes will be considered the operation.
- Logging to a file. The logging is console only.

## Deliverables
- A repository that can generate an executable jar via a maven build. The name of the jar built NEEDED to run as single application is:
```Calculator-1.0-SNAPSHOT-jar-with-dependencies.jar```
- Access to Github repository at [github.com/jpcampos/Calculator-Homework](github.com/jpcampos/Calculator-Homework)
- A CI build using github actions located at [https://github.com/jpcampos/Calculator-Homework/actions](https://github.com/jpcampos/Calculator-Homework/actions) for a Continous Integration build.
- Unit tests in the maven build using Spock Tests accessible via IDE (i.e. IntelliJ) as well.

## Design
The calculator solution is based on a standard approach to a basic calculator problem.
The approach is to use stacks as a recursor for "memory" of past operations.  
Basically the core of the solution uses two stacks:  
  - A stack for the operator
  - A stack for the numbers (operands)  

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
 Some issues arose when trying to manage the expression stacks and maps, so counters for open let parenthesis and flags to know how deep inside a LET expression were added. These flags ensure the proper time to "collapse" operations to produce the result or assign it to a variable.
 
 #### Parsing
 In order to parse the String, the operations are converted with the following substitution:  
 
 | INPUT                                               	| OUTPUT 	|
 |-----------------------------------------------------	|--------	|
 | add                                              	| +      	|
 | sub                                              	| _ (underscore)|
 | mult                                             	| *      	|
 | div                                              	| /      	|
 | let                                              	| #      	|
 
 This will allow the calculator to trigger actions just by parsing a single character.
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
The token will provide access to the repository Calculator-Homework


## Building the Application


* Execute the following command through the command line if maven is installed or using the Maven commands menu inside IntelliJ
```
mvn clean package
```

* The maven goal will  and build the jar file. This is the full runnable jar as it includes the dependencies.
```
Calculator-0.0.1-SNAPSHOT-with-dependencies.jar
```
NOTE: The jar with dependencies is needed to execute the calculator program. Any other jar generated by the build will not execute.

## Running the Application
Navigate to the root folder of the application. If the jar is built, use the following command as an example.
```
java -jar target/Calculator-1.0-SNAPSHOT-jar-with-dependencies.jar "add(1,2)"  
```

The console should return immediately with:
```
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

* The tests are executed during the maven build, but if there is a need to execute them manually, this can be done in the IDE.
* If using Intellij, these are the steps:
    * Open the Project sidebar and navigate to the src/test/groovy folder
    * Right click on the folder and choose the option:
    ```
    Run "All Tests"
    ```
All the tests are written in spock. Run the tests and look at the console to get more information on what tests are doing during execution.
A total of 120 tests should be successfully executed. The total coverage from the tests is 94% line coverage and 90% method coverage. The missing coverage is static constructors or static main method calls which are executed manually from the command line.

## Continuous Integration Build

A continuous integration build was set up using Github actions. 
The build history can be viewed at:

[Calculator CI Build History](https://github.com/jpcampos/Calculator-Homework/actions?query=workflow%3A%22Java+CI+with+Maven%22)


The CI build consists of:
- A java maven build
- Triggers to be executed on:
    - Merge to master
    - A pull request from a branch from master
    - A scheduled nightly build corresponding to 07:40 AM UTC time (around 7:40 AM Calgary time)

The source file for the CI build is located in the source project at:

[Calculator CI Build yml file](https://github.com/jpcampos/Calculator-Homework/blob/master/.github/workflows/maven.yml)


## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Spock](http://spockframework.org/) - A testing framework based on Groovy, choosing for the speed of unit and integration testing development as well as ease of adaption of TDD principles.
* [picocli](https://picocli.info/) - A mighty tiny command line interface
* [Apache Commons Lang](http://commons.apache.org/proper/commons-lang/) -  Apache Commons Lang provides extra methods to Java core classes.
* [Log4J2](https://logging.apache.org/log4j/2.x/) Apache log4j2 for logging capabilities.

## Authors

* Juan Campos  - *Initial work* - 

## Resources

