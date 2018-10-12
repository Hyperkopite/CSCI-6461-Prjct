/*
** Use System.in and System.out instead of instructions in and out here for better demonstration
** Will use instructions in and out in the machine code version.
 */

import java.util.Arrays;

public class program1 {
    private static final int SEARCH = 60;
    private static final int START_OF_USERINPUT = 40;
    private static final int FIND = 62;
    private static final int DIFFERENCE = 61;
    private static final int FOR_INDEX = 31;
    private static final int FINAL_DIFFERENCE = 39;

    public static void main(String[] args) throws Exception {
        MemorySystem ms = new MemorySystem();
        ComputerArchitecture ca = new ComputerArchitecture();

        try {
            ca.init(ms);
        } catch (Exception e) {

        }

        //load X1 with 30
        ca.Air(0,"11111");
        ca.Str(Integer.toBinaryString(FOR_INDEX),0,0, ms);
        ca.Ldx(Integer.toBinaryString(FOR_INDEX),1,0, ms);
        //Clear the register. In this self-designed instruction set, this instruction has
        //the ability to clear a register.
        ca.Src(0,15,0,0);
        //store initial value of variable search
        ca.Str(Integer.toBinaryString(SEARCH),0,0, ms);


        //Use memory 40-59 for console input buffer for here
        System.out.println("Input 20 positive integers : ");
//        Scanner input = new Scanner(System.in);
        for (int i=START_OF_USERINPUT; i<60; i++) {
//            ca.Air(0,Integer.toString(input.nextInt()));
            ca.Air(0,"1101");
            ca.Str(Integer.toBinaryString(i),0,0, ms);
            ca.Src(0,15,0,0);
            ms.displayMainMem(i);
        }

        ca.Air(0,"0110");
        ca.Str(Integer.toBinaryString(50),0,0, ms);
        ms.displayMainMem(50);

        //display the content in memory[40-59] to the console
        //let R0 be the console display register for now
        for (int i=START_OF_USERINPUT; i<60; i++) {
//            ca.Ldr(Integer.toBinaryString(i-FOR_INDEX),0,1, ms);
//            System.out.print("The content in memory "+ i +"is : ");
//            System.out.println(Arrays.toString(ca.r[0]));
//            ca.And(0,9);
            System.out.println("The user input in memory["+i+"] is : ");
            ms.displayMainMem(i);
        }


        //Update variable search's value
        System.out.println("Input a integer you want to search : ");
//        Scanner search = new Scanner(System.in);
        ca.Src(0,15,0,0);
//        ca.Air(0,Integer.toString(search.nextInt()));
        ca.Air(0,"0101");
        ca.Str(Integer.toBinaryString(SEARCH),0,0, ms);
        System.out.println("The search is :");
        ms.displayMainMem(SEARCH);

        //Find the closest value
        //Will use instruction dvd to determine which one is bigger
        //Use if here, will use JZ in the machine code
        ca.Ldr(Integer.toBinaryString(SEARCH),0,0, ms); //load search to R0
        ca.Ldr(Integer.toBinaryString(START_OF_USERINPUT),2,0, ms); //load user input to R2
        ca.Dvd(0,2);
        ca.Src(3,15,0,0);
        if (Arrays.toString(ca.r[0]).equals(Arrays.toString(ca.r[3]))) {
            //means search < user input
            ca.Smr(2,Integer.toBinaryString(SEARCH),0, ms); // user input - search
            ca.Orr(3,2); //let R3 store the difference between search and user input
            //store present value of user input as find
            ca.Ldr(Integer.toBinaryString(START_OF_USERINPUT),0,0, ms);
            ca.Str(Integer.toBinaryString(FIND),0,0, ms);
        } else {
            //means search > user input
            ca.Ldr(Integer.toBinaryString(SEARCH),0,0, ms); //load search to R0 again since the value had been changed to quotient.
            ca.Smr(0,Integer.toBinaryString(START_OF_USERINPUT),0, ms); //search - user input
            ca.Orr(3,0);
            //store present value of user input as find
            ca.Ldr(Integer.toBinaryString(START_OF_USERINPUT),0,0, ms);
            ca.Str(Integer.toBinaryString(FIND),0,0, ms);
        }

        ca.Str(Integer.toBinaryString(FINAL_DIFFERENCE),3,0, ms);

        for (int i=START_OF_USERINPUT+1; i<60; i++) {
            ca.Ldr(Integer.toBinaryString(SEARCH),0,0, ms); //load search to R0
            ca.Ldr(Integer.toBinaryString(i),2,0, ms); //load user input to R2
            ca.Dvd(0,2);
            if (Arrays.toString(ca.r[0]).equals(Arrays.toString(ca.r[9]))) {
                //means search < user input
                ca.Smr(2,Integer.toBinaryString(SEARCH),0, ms); // user input - search
                ca.Ldr(Integer.toBinaryString(FINAL_DIFFERENCE),0,0, ms);
                ca.Dvd(0,2); // compare this time's difference to the difference stored in Memory
                if (!Arrays.toString(ca.r[0]).equals(Arrays.toString(ca.r[9]))) {
                    //means this time's difference < difference stored in R4
                    ca.Str(Integer.toBinaryString(FINAL_DIFFERENCE),2,0, ms);//update Difference
                    //store present value of user input
                    ca.Ldr(Integer.toBinaryString(i),0,0, ms);
                    ca.Str(Integer.toBinaryString(FIND),0,0, ms);
                }
            } else {
                //means search > user input
                if (Arrays.toString(ca.r[1]).equals(Arrays.toString(ca.r[9]))) {
                    //store present value of user input
                    ca.Ldr(Integer.toBinaryString(i),0,0, ms);
                    ca.Str(Integer.toBinaryString(FIND),0,0, ms);
                    System.out.println("i is : "+i);
                    break;
                }
                ca.Ldr(Integer.toBinaryString(SEARCH),0,0, ms); //load search to R0 again since the value had been changed to quotient.
                ca.Smr(0,Integer.toBinaryString(i),0, ms); //search - user input
                ca.Str(Integer.toBinaryString(DIFFERENCE),0,0, ms);
                ca.Ldr(Integer.toBinaryString(FINAL_DIFFERENCE),2,0, ms);
                ca.Dvd(0,2);
                if (Arrays.toString(ca.r[0]).equals(Arrays.toString(ca.r[9]))) {
                    //means this time's difference < difference stored in R4
                    ca.Ldr(Integer.toBinaryString(DIFFERENCE),0,0, ms);
                    ca.Str(Integer.toBinaryString(FINAL_DIFFERENCE),0,0, ms); //update Difference
                    //store present value of user input
                    ca.Ldr(Integer.toBinaryString(i),0,0, ms);
                    ca.Str(Integer.toBinaryString(FIND),0,0, ms);
                }
            }
        }
        ca.Ldr(Integer.toBinaryString(SEARCH),0,0, ms);
        ca.Ldr(Integer.toBinaryString(FIND),3,0,ms);
        System.out.println("Your input is : "+Arrays.toString(ca.r[0]));
        System.out.println("Find : "+Arrays.toString(ca.r[3]));
    }

}
