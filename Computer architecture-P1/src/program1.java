/*
** Use System.in and System.out instead of instructions in and out here for better demonstration
** Will use instructions in and out in the machine code version.
 */

import java.util.Arrays;
import java.util.Scanner;

public class program1 {
    private static final int SEARCH = 61;
    private static final int START_OF_USERINPUT = 40;
    private static final int FIND = 62;
    private static final int DIFFERENCE = 60;
    private static final int FOR_INDEX = 31;

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
        //Clear the register. In this self-designed instruction set, only this instruction has
        //the ability to clear a register.
        ca.Src(0,15,0,0);
        //store initial value of variable search
        ca.Str(Integer.toBinaryString(SEARCH-FOR_INDEX),0,1, ms);


        //Use memory 40-59 for console input buffer for here
        System.out.println("Input 20 positive integers : ");
//        Scanner input = new Scanner(System.in);
        for (int i=START_OF_USERINPUT; i<60; i++) {
//            ca.Air(0,Integer.toString(input.nextInt()));
            ca.Air(0,"0101");
            ca.Str(Integer.toBinaryString(i-FOR_INDEX),0,1, ms);
        }


        //display the content in memory[40-59] to the console
        //let R0 be the console display register for now
        for (int i=START_OF_USERINPUT; i<60; i++) {
            ca.Ldr(Integer.toBinaryString(i-FOR_INDEX),0,1, ms);
            System.out.print("The content in memory "+ i +"is : ");
            System.out.println(Arrays.toString(ca.r[0]));
        }


        //Update variable search's value
        System.out.println("Input a integer you want to search : ");
//        Scanner search = new Scanner(System.in);
        ca.Src(0,15,0,0);
//        ca.Air(0,Integer.toString(search.nextInt()));
        ca.Air(0,"0101");
        ca.Str(Integer.toBinaryString(SEARCH-FOR_INDEX),0,1, ms);

        //Find the closest value
        //Will use instruction dvd to determine which one is bigger
        //Use if here, will use JZ in the machine code
        ca.Ldr(Integer.toBinaryString(SEARCH-FOR_INDEX),0,1, ms); //load search to R0
        ca.Ldr(Integer.toBinaryString(START_OF_USERINPUT-FOR_INDEX),2,1, ms); //load user input to R2
        ca.Dvd(0,2);

        if (Arrays.toString(ca.r[0]).equals("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]")) {
            //means search < user input
            ca.Smr(2,Integer.toBinaryString(SEARCH-FOR_INDEX),1, ms); // user input - search
            ca.Src(3,15,0,0);
            ca.Orr(3,2); //let R3 store the difference between search and user input
            //store present value of user input as find
            ca.Ldr(Integer.toBinaryString(START_OF_USERINPUT-FOR_INDEX),0,1, ms);
            ca.Str(Integer.toBinaryString(FIND-FOR_INDEX),0,1, ms);
        } else {
            //means search > user input
            ca.Ldr(Integer.toBinaryString(SEARCH-FOR_INDEX),0,1, ms); //load search to R0 again since the value had been changed to quotient.
            ca.Smr(0,Integer.toBinaryString(START_OF_USERINPUT-FOR_INDEX),1, ms); //search - user input
            ca.Src(3,15,0,0);
            ca.Orr(3,0);
            //store present value of user input as find
            ca.Ldr(Integer.toBinaryString(START_OF_USERINPUT-FOR_INDEX),0,1, ms);
            ca.Str(Integer.toBinaryString(FIND-FOR_INDEX),0,1, ms);
        }



        for (int i=START_OF_USERINPUT+1; i<60; i++) {
            ca.Ldr(Integer.toBinaryString(SEARCH-FOR_INDEX),0,1, ms); //load search to R0
            ca.Ldr(Integer.toBinaryString(i-FOR_INDEX),2,1, ms); //load user input to R2
            ca.Dvd(0,2);
            if (Arrays.toString(ca.r[0]).equals("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]")) {
                //means search < user input
                ca.Smr(2,Integer.toBinaryString(SEARCH-FOR_INDEX),1, ms); // user input - search
                ca.Str(Integer.toBinaryString(DIFFERENCE-FOR_INDEX),2,1, ms); //store the difference temporally
                ca.Src(0,15,0,0);
                ca.Orr(0,3); // let R0 store the value in R3
                ca.Dvd(0,2); // compare this time's difference to the difference stored in R3
                if (!Arrays.toString(ca.r[0]).equals("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]")) {
                    //means this time's difference < difference stored in R3
                    ca.Ldr(Integer.toBinaryString(DIFFERENCE-FOR_INDEX),3,1, ms); //update R3
                    //store present value of user input
                    ca.Ldr(Integer.toBinaryString(i-FOR_INDEX),0,1, ms);
                    ca.Str(Integer.toBinaryString(FIND-FOR_INDEX),0,1, ms);
                }
            } else {
                //means search > user input
                ca.Ldr(Integer.toBinaryString(SEARCH-FOR_INDEX),0,1, ms); //load search to R0 again since the value had been changed to quotient.
                ca.Smr(0,Integer.toBinaryString(i-FOR_INDEX),1, ms); //search - user input
                ca.Str(Integer.toBinaryString(DIFFERENCE-FOR_INDEX),0,1, ms); //store the difference temporally
                ca.Dvd(3,0);
                if (!Arrays.toString(ca.r[3]).equals("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]")) {
                    //means this time's difference < difference stored in R3
                    ca.Ldr(Integer.toBinaryString(DIFFERENCE-FOR_INDEX),3,1, ms); //update R3
                    //store present value of user input
                    ca.Ldr(Integer.toBinaryString(i-FOR_INDEX),0,1, ms);
                    ca.Str(Integer.toBinaryString(FIND-FOR_INDEX),0,1, ms);
                }
            }
        }
        ca.Ldr(Integer.toBinaryString(SEARCH-FOR_INDEX),0,1, ms);
        ca.Ldr(Integer.toBinaryString(FIND-FOR_INDEX),3,1,ms);
        System.out.println("Your input is : "+Arrays.toString(ca.r[0]));
        System.out.println("Your input is : "+Arrays.toString(ca.r[3]));
    }

}