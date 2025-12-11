    package mars.mips.instructions.customlangs;
    import mars.mips.hardware.*;
    import mars.*;
    import mars.util.*;
    import mars.mips.instructions.*;

/*
In the video tutorial, John explains briefly how he created this custom assembly maker.
To make my own custom assembly language, I created a new file in the customlangs folder
and opened it in VS Code, making sure to copy in all of the imports and follow the format
of the ExampleAssembly.java file. I was then able to take this file to the terminal and
run it with BuildCustomLang.java to create a jar file that is able to run in MARS. Once I
opened MARS, I was able to choose my TestAssembly language from the language options and 
by clicking help, I could see the description of my one test instruction "meow"!
*/

public class TestAssembly extends CustomAssembly {
    @Override
    public String getName(){
        return "Test Assembly Language";
    }

    @Override
    public String getDescription(){
        return "Practicing creating a new assembly language!";
    }

    @Override
    protected void populate(){
        instructionList.add(
                new BasicInstruction("meow $t1, 100",
            	 "Meow : Test instruction that adds immediate value to register.",
                BasicInstructionFormat.I_FORMAT,
                "001000 fffff 00000 ssssssssssssssss",
                new SimulationCode()
               {
                   public void simulate(ProgramStatement statement) throws ProcessingException
                  {
                     int[] operands = statement.getOperands();
                     int add1 = RegisterFile.getValue(operands[0]);
                     int add2 = operands[1] << 16 >> 16;
                     int sum = add1 + add2;
                     RegisterFile.updateRegister(operands[0], sum);
                  }
               }));
    }
}