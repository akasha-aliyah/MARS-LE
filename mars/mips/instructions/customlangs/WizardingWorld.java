/*
AUTHOR: Akasha Barron
PROFESSOR: Dominic Dabish
CLASS: CS240
DUE DATE: 12/10/25
README: This is a custom assembly language for MARS based on the Harry Potter series. It includes 10 basic mips functions:
geminio (add), accio (addi), stupefy (sub), engorgio (mult), bombarda maxima (div), alohomora (beq), colloportus (bne), 
apparate (j), expecto patronum (print), confundo (bitwise OR). Additionally, it includes 10 fun unique instructions: 
arresto momentum (sub 1 from all $t registers), lumos (add 1 to given $t register), sectumsempra (subtract random value from given $t register),
confringo (explosion curse that deducts house points), flipendo (swap values of two registers), take an exam (randomly generates O.W.L. grades),
try a new spell (random event that may add or subtract house points), avada kedavra (sets all $v, $t, and $gb registers to 0),
view house points (displays current house points), get sorted into a house (randomly assigns a house). 
To implement this language into your own MARS-LE, download this file and move it into your "customlangs" folder in your own 
MARS-LE installation. Open up a terminal (I used Windows PowerShell) and navigate to your MARS-LE directory. From there, type in the command:
"java - jar BuildCustomLang.jar WizardingWorld.java" and hit enter. This will compile the custom language and add it to your MARS-LE installation.
You can then open Mars.jar from the MARS-LE files, click Tools, click Language Switcher, and select "Wizarding World Assembly" from the list.
After that, grab on tight to your broomstick because you are ready to fly!
*/
    package mars.mips.instructions.customlangs;
    import mars.simulator.*;
    import mars.mips.hardware.*;
    import mars.*;
    import mars.util.*;
    import mars.mips.instructions.*;
    import java.util.Random;

public class WizardingWorld extends CustomAssembly{
    @Override
    public String getName(){
        return "Wizarding World Assembly";
    }

    @Override
    public String getDescription(){
        return "An assembly language to cast magical spells!";
    }

    @Override
    protected void populate(){

        instructionList.add(    // Geminio ; "add" 
            new BasicInstruction("gem $t1, $t2, $t3",
            "Geminio: A doubling charm. Adds the values of the two registers together.",
            BasicInstructionFormat.R_FORMAT,
            "000000 sssss ttttt fffff 00000 100000",
            new SimulationCode() {
                public void simulate(ProgramStatement statement) throws ProcessingException {
                    int[] operands = statement.getOperands();
                    int add1 = RegisterFile.getValue(operands[1]);
                    int add2 = RegisterFile.getValue(operands[2]);
                    int sum = add1 + add2;
                    // overflow detected when A and B have the same sign and A + B has a different sign.
                    if ((add1 >= 0 && add2 >= 0 && sum < 0)
                        || (add1 < 0 && add2 < 0 && sum >= 0)) {
                    throw new ProcessingException(statement,
                        "arithmetic overflow",Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                    }
                    RegisterFile.updateRegister(operands[0], sum);
                }
            }));

        instructionList.add(    // Accio ; "addi"
            new BasicInstruction("acc $t1, $t2, -1",
            "Accio: A summoning charm. Adds an immediate value to the value in a register.",
            BasicInstructionFormat.I_FORMAT,
            "001000 sssss fffff tttttttttttttttt",
            new SimulationCode() {
                public void simulate(ProgramStatement statement) throws ProcessingException {
                    int[] operands = statement.getOperands();
                    int add1 = RegisterFile.getValue(operands[1]);
                    int add2 = operands[2] << 16 >> 16;
                    int sum = add1 + add2;
                    // overflow on A+B detected when A and B have same sign and A+B has other sign.
                    if ((add1 >= 0 && add2 >= 0 && sum < 0) || (add1 < 0 && add2 < 0 && sum >= 0)) {
                        throw new ProcessingException(statement, "arithmetic overflow",Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                    }
                    RegisterFile.updateRegister(operands[0], sum);
                }
            }));

        instructionList.add(    // Stupefy ; "sub"
            new BasicInstruction("stpf $t1, $t2, $t3",
            "Stupefy: A stunning spell. Subtracts the value in one register from the value in another.",
            BasicInstructionFormat.R_FORMAT,
            "000000 sssss ttttt fffff 00000 100010",
            new SimulationCode() {
                public void simulate(ProgramStatement statement) throws ProcessingException {
                    int[] operands = statement.getOperands();
                    int sub1 = RegisterFile.getValue(operands[1]);
                    int sub2 = RegisterFile.getValue(operands[2]);
                    int dif = sub1 - sub2;
                    // overflow on A-B detected when A and B have opposite signs and A-B has B's sign
                    if ((sub1 >= 0 && sub2 < 0 && dif < 0) || (sub1 < 0 && sub2 >= 0 && dif >= 0)) {
                        throw new ProcessingException(statement, "arithmetic overflow",Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                    }
                    RegisterFile.updateRegister(operands[0], dif);
                }
            }));

        instructionList.add(    // Engorgio ; "mult"
            new BasicInstruction("eng $t1, $t2",
            "Engorgio: An enlargement charm. Multiplies the values in the given registers.",
            BasicInstructionFormat.R_FORMAT,
            "000000 sssss ttttt 00000 00000 011000",
            new SimulationCode() {
                public void simulate(ProgramStatement statement) throws ProcessingException {        
                    int[] operands = statement.getOperands();
                    long product = (long) RegisterFile.getValue(operands[0]) * (long) RegisterFile.getValue(operands[1]);
                    RegisterFile.updateRegister(operands[0], (int) (product >> 32));
                    RegisterFile.updateRegister(operands[1], (int) ((product << 32) >> 32));
                }       
            }));

        instructionList.add(    // Bombarda Maxima ; "div"
            new BasicInstruction("bmbm $t1, $t2",
            "Bombarda Maxima: A powerful blasting curse. Divides the value in one register by the value in another.",
            BasicInstructionFormat.R_FORMAT,
            "000000 fffff sssss 00000 00000 011010",
            new SimulationCode() {
                public void simulate(ProgramStatement statement) throws ProcessingException {
                    int[] operands = statement.getOperands();
                    if (RegisterFile.getValue(operands[1]) == 0) {     // undefined division by zero
                        return;
                    }
                    RegisterFile.updateRegister(operands[1], RegisterFile.getValue(operands[0]) % RegisterFile.getValue(operands[1]));
                    RegisterFile.updateRegister(operands[0], RegisterFile.getValue(operands[0]) / RegisterFile.getValue(operands[1]));
                }
            }));
        
        instructionList.add(    // Alohomora ; "beq"
            new BasicInstruction("almr $t1, $t2, label",
            "Alohomora: An unlocking charm. Jumps to the instruction at the given label if the two given values are equal.",
            BasicInstructionFormat.I_BRANCH_FORMAT,
            "000100 fffff sssss tttttttttttttttt",
            new SimulationCode() {
                public void simulate(ProgramStatement statement) throws ProcessingException {
                    int[] operands = statement.getOperands();
                    if (RegisterFile.getValue(operands[0]) == RegisterFile.getValue(operands[1])) {
                        Globals.instructionSet.processBranch(operands[2]);
                    }
                }
            }));

        instructionList.add(    // Colloportus ; "bne"
            new BasicInstruction("clpt $t1, $t2, label",
            "Colloportus: A locking charm. Jumps to the instruction at the given label if the two given values are not equal.",
            BasicInstructionFormat.I_BRANCH_FORMAT,
            "000101 fffff sssss tttttttttttttttt",
            new SimulationCode() {
                public void simulate(ProgramStatement statement) throws ProcessingException{
                    int[] operands = statement.getOperands();
                    if (RegisterFile.getValue(operands[0]) != RegisterFile.getValue(operands[1])) {
                        Globals.instructionSet.processBranch(operands[2]);
                    }
                }
            }));

        instructionList.add(    // Apparate ; "j"
            new BasicInstruction("aprt label",
            "Apparate: Apparition. Jumps to the instruction at the given label.",
            BasicInstructionFormat.J_FORMAT,
            "000010 ffffffffffffffffffffffffff",
            new SimulationCode() {
                public void simulate(ProgramStatement statement) throws ProcessingException{
                    int[] operands = statement.getOperands();
                    Globals.instructionSet.processJump(
                       ((RegisterFile.getProgramCounter() & 0xF0000000)
                               | (operands[0] << 2)));
                }
            }));

        instructionList.add(    // Expecto Patronum ; "print"
            new BasicInstruction("expt label",
            "Expecto Patronum: The Patronus Charm. Prints the string stored at the given label.",
            BasicInstructionFormat.I_BRANCH_FORMAT,
            "110000 00000 00000 ffffffffffffffff",
            new SimulationCode() {
                public void simulate(ProgramStatement statement) throws ProcessingException{
                    char ch = 0;    // retrieve name of the label from the token list
                    String label = statement.getOriginalTokenList().get(1).getValue();  // look up label in program symbol table
                    int byteAddress = Globals.program.getLocalSymbolTable().getAddressLocalOrGlobal(label);
                    try {
                        ch = (char) Globals.memory.getByte(byteAddress);    // won't stop until NULL byte reached!
                        while (ch != 0) {
                            SystemIO.printString("" + ch);
                            byteAddress++;
                            ch = (char) Globals.memory.getByte(byteAddress);
                        }
                    } catch (AddressErrorException e) {
                        throw new ProcessingException(statement, e);
                    }
                }
            }));
        
        instructionList.add(    // Confundo ; "bitwise OR"
            new BasicInstruction("cnfd $t1, $t2, $t3",
            "Confundo: A confusion charm. Performs a bitwise OR on the values in two registers.",
            BasicInstructionFormat.R_FORMAT,
            "000000 sssss ttttt fffff 00000 100101",
            new SimulationCode() {
                public void simulate(ProgramStatement statement) throws ProcessingException {
                    int[] operands = statement.getOperands();
                    RegisterFile.updateRegister(operands[0], RegisterFile.getValue(operands[1]) | RegisterFile.getValue(operands[2]));
                }
            }));
        
        instructionList.add(    // Arresto Momentum
            new BasicInstruction("armt",
            "Arresto Momentum: A slowing charm. Decreases the value of all registers by 1.",
            BasicInstructionFormat.R_FORMAT,
            "000000 00000 00000 000000 00000 100011",
            new SimulationCode() {
                public void simulate(ProgramStatement statement) throws ProcessingException {
                    int[] operands = statement.getOperands();
                    for (int i = 8; i < 18; i++){      // iterate through every $t register
                        int val = RegisterFile.getValue(i);
                        RegisterFile.updateRegister(i, val - 1);
                    }
                }
            }));

        instructionList.add(    // Lumos
            new BasicInstruction("lms $t1",
            "Lumos: Lights the end of one's wand. Increases the value of the given register by 1.",
            BasicInstructionFormat.I_FORMAT,
            "001000 sssss fffff tttttttttttttttt",
            new SimulationCode() {
                public void simulate(ProgramStatement statement) throws ProcessingException {
                    int[] operands = statement.getOperands();
                    int add = RegisterFile.getValue(operands[0]);
                    int sum = add + 1;
                    RegisterFile.updateRegister(operands[0], sum);
                }
            }));

        instructionList.add(    // Sectumsempra
            new BasicInstruction("scsm $t1",
            "Sectumsempra: A laceration curse. Subtracts a random value from the given register.",
            BasicInstructionFormat.R_FORMAT,
            "000000 00000 00000 fffff 00000 101010",
            new SimulationCode() {
                public void simulate(ProgramStatement statement) throws ProcessingException {
                    int[] operands = statement.getOperands();
                    Random random = new Random();
                    int rand_val = random.nextInt(51);  // random value between 0 and 50
                    int sub = RegisterFile.getValue(operands[0]);
                    int diff = sub - rand_val;
                    RegisterFile.updateRegister(operands[0], diff);
                }
            }));
        
        instructionList.add(    // Confringo
            new BasicInstruction("cnfr",
            "Confringo: An explosion charm. Let's see how this goes for you.",
            BasicInstructionFormat.R_FORMAT,
            "000000 00000 00000 00000 00000 110001",
            new SimulationCode() {
                public void simulate(ProgramStatement statement) throws ProcessingException {
                    SystemIO.printString("Why would you try to explode something inside the castle?\n");
                    if (RegisterFile.getValue(19) == 0) {   // if House points == zero
                        SystemIO.printString("You would have lost House points... if you had any.\n");
                    } else if (RegisterFile.getValue(19) < 5) {
                        RegisterFile.updateRegister(19, 0);  // set House points to zero
                        SystemIO.printString("You lost all your House points!\n");
                    } else {
                        int current_points = RegisterFile.getValue(19);
                        RegisterFile.updateRegister(19, current_points - 5);  // subtract 5 points
                        SystemIO.printString("You lost 5 House points!\n");
                    }
                    SystemIO.printString("DETENTION!\n");
                    SystemIO.printString("\n");

                }
            }));

        instructionList.add(    // Flipendo
            new BasicInstruction("flip $t1, $t2",
            "Flipendo: A flipping charm. Swaps the values of two registers.",
            BasicInstructionFormat.R_FORMAT,
            "000000 fffff sssss 00000 00000 010001",
            new SimulationCode() {
                public void simulate(ProgramStatement statement) throws ProcessingException {
                    int[] operands = statement.getOperands();
                    int val1 = RegisterFile.getValue(operands[0]);
                    int val2 = RegisterFile.getValue(operands[1]);
                    RegisterFile.updateRegister(operands[0], val2);
                    RegisterFile.updateRegister(operands[1], val1);
                }
            }));

        instructionList.add(    // Take An Exam
            new BasicInstruction("test",
            "Take an Exam: Are you ready for your O.W.L.s?",
            BasicInstructionFormat.R_FORMAT,
            "000000 00000 00000 00000 00000 110001",
            new SimulationCode() {
                public void simulate(ProgramStatement statement) throws ProcessingException {
                    String[] grades = {"T", "D", "D", "P", "P", "A", "A", "A", "E", "E", "O"};   // follows bell curve, T lowest to O highest
                    String[] classes = {"Potions", "Transfiguration", "Charms", "Defense Against the Dark Arts", "Herbology", "History of Magic"};
                    Random random = new Random();
                    
                    SystemIO.printString("You have completed your O.W.L. exams! Here are your results:\n");
                    for (int i = 0; i < classes.length; i++) {
                        String grade = grades[random.nextInt(grades.length)];
                        SystemIO.printString(classes[i] + ": " + grade + "\n");
                        if (grade.equals("O")) {
                            SystemIO.printString("Take a House point for that Outstanding performance!\n");
                            int current_points = RegisterFile.getValue(19);
                            RegisterFile.updateRegister(19, current_points + 1);  // add 1 point
                        }
                    }
                    SystemIO.printString("\n");
                }
            }));
        
        instructionList.add(    // Try a New Spell
            new BasicInstruction("cast",
            "Try a New Spell: Practice makes perfect!",
            BasicInstructionFormat.R_FORMAT,
            "000000 00000 00000 00000 00000 110001",
            new SimulationCode() {
                public void simulate(ProgramStatement statement) throws ProcessingException {
                    Random random = new Random();
                    int cast = random.nextInt(7);   // random number between 0 and 6
                    int current_points = RegisterFile.getValue(19);
                    switch (cast){ 
                        case 0:
                           SystemIO.printString("Bring Madam Pompfrey. NOW.\n");
                           SystemIO.printString("Minus a House point for you.\n");
                           SystemIO.printString("\n");
                           RegisterFile.updateRegister(19, current_points - 1);
                           break;
                        case 1:
                           SystemIO.printString("I think that's enough practice for today...\n");
                           SystemIO.printString("Perhaps try asking your professor for help tomorrow.\n");
                           SystemIO.printString("\n");
                           break;
                        case 2:
                           SystemIO.printString("It's good, but is it good enough to pass your O.W.L.?\n");
                           SystemIO.printString("\n");
                           break;
                        case 3:
                           SystemIO.printString("BRILLIANT!\n");
                           SystemIO.printString("Take a House point!\n");
                           SystemIO.printString("\n");
                           RegisterFile.updateRegister(19, current_points + 1);
                           break;
                        case 4:
                           SystemIO.printString("You're really getting somewhere. Great improvement!\n");
                           SystemIO.printString("Take a House point!\n");
                           SystemIO.printString("\n");
                           RegisterFile.updateRegister(19, current_points + 1);
                           break;
                        case 5:
                           SystemIO.printString("EXCELLENT work. You must show Professor McGonagall.\n");
                           SystemIO.printString("Take a House point!\n");
                           SystemIO.printString("\n");
                           RegisterFile.updateRegister(19, current_points + 1);
                           break;
                        case 6:
                           SystemIO.printString("Your spell backfired! Watch where you're pointing that thing!\n");
                           SystemIO.printString("Minus a House point for you.\n");
                           SystemIO.printString("\n");
                           RegisterFile.updateRegister(19, current_points - 1);
                           break;
                        default:
                            SystemIO.printString("Your spell was successful!\n");
                            SystemIO.printString("\n");
                            break;
                    }
                }
            }));
        
        instructionList.add(    // Avada Kedavra
            new BasicInstruction("avkd",
            "Avada Kedavra: The Killing Curse. The value of all registers goes to 0.",
            BasicInstructionFormat.R_FORMAT,
            "000000 00000 00000 00000 00000 110001",
            new SimulationCode() {
                public void simulate(ProgramStatement statement) throws ProcessingException {
                    for (int i = 2; i < 27; i++){      // iterate through $v, $t, and $gb registers
                        RegisterFile.updateRegister(i, 0);
                    }
                    SystemIO.printString("You have used the Killing Curse. All your registers have been set to 0.\n");
                    SystemIO.printString("\n");
                }
            }));

        instructionList.add(    // View House Points
            new BasicInstruction("hspt",
            "House Points: Check your current House points.",
            BasicInstructionFormat.R_FORMAT,
            "000000 00000 00000 00000 00000 011111",
            new SimulationCode() {
                public void simulate(ProgramStatement statement) throws ProcessingException {
                    String pts = String.valueOf(RegisterFile.getValue(19));      // where House points are stored
                    int house = RegisterFile.getValue(18);
                    SystemIO.printString("You currently have " + pts + " House points.\n");
                    switch (house) {
                        case 1:
                            SystemIO.printString("Go Gryffindor!\n");
                            SystemIO.printString("\n");
                            break;
                        case 2:
                            SystemIO.printString("Go Hufflepuff!\n");
                            SystemIO.printString("\n");
                            break;
                        case 3:
                            SystemIO.printString("Go Ravenclaw!\n");
                            SystemIO.printString("\n");
                            break;
                        case 4:
                            SystemIO.printString("Go Slytherin!\n");
                            SystemIO.printString("\n");
                            break;
                        default:
                            SystemIO.printString("No House assigned.\n");
                            SystemIO.printString("\n");
                            break;
                    }
                }
            }));
            
        instructionList.add(    // Get Sorted into a House
            new BasicInstruction("sort",
            "The Sorting Hat: Find out which House you belong to.",
            BasicInstructionFormat.R_FORMAT,
            "000000 00000 00000 00000 00000 000010",
            new SimulationCode() {
                public void simulate(ProgramStatement statement) throws ProcessingException {
                    int random_house = new Random().nextInt(4) + 1; // random number between 1 and 4
                    RegisterFile.updateRegister(18, random_house);  // store House in $gb0
                    SystemIO.printString("I think I know where you belong...\n");
                    switch (random_house) {
                        case 1:
                            SystemIO.printString("Gryffindor!\n");
                            SystemIO.printString("\n");
                            break;
                        case 2:
                            SystemIO.printString("Hufflepuff!\n");
                            SystemIO.printString("\n");
                            break;
                        case 3:
                            SystemIO.printString("Ravenclaw!\n");
                            SystemIO.printString("\n");
                            break;
                        case 4:
                            SystemIO.printString("Slytherin!\n");
                            SystemIO.printString("\n");
                            break;
                        default:
                            SystemIO.printString("No House assigned.\n");
                            SystemIO.printString("\n");
                            break;
                    }
                }
            }));
    }
}