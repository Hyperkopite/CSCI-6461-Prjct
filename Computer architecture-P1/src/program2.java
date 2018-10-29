import java.util.Arrays;

public class program2 {
    public static void main(String[] args) throws Exception {
        MemorySystem ms = new MemorySystem();
        ComputerArchitecture ca = new ComputerArchitecture();

        int sentenceNum = 1;
        int wordNum = 0;
        int SEARCH = 30;
        boolean find = false; //if find match, set find to true
        boolean compare = true; //if compare is false, do not compare two values in the register
        String userInput = "sheath "; //mimic user input

        try {
            ca.init(ms);
        } catch (Exception e) {

        }

        ca.In("10100",0, ms);
        //store userInput to memory, start at memory1[SEARCH]
        for (int i=0; i<userInput.length(); i++) {
            var temp = Integer.toBinaryString((int)userInput.charAt(i));
            var k = 15;
            for (int j=temp.length()-1; j>=0; j--) {
                ms.setMemory(SEARCH+i, k, Character.getNumericValue(temp.charAt(j)));
                k--;
            }
        }
        //load value of character Space to R2, Period to R3.
        ca.Air(2, "100000");
        ca.Air(3, "101110");
        int increment = 0;
        for (int i=ca.START; i<ca.STOP; i++) {
            ca.Ldr(Integer.toBinaryString(i),1,0, ms); //store the character that read from file to R1.
            if (Arrays.toString(ca.r[1]).equals(Arrays.toString(ca.r[2]))) { wordNum++; compare = true; continue;} //word number +1 when meet character Space.
            if (Arrays.toString(ca.r[1]).equals(Arrays.toString(ca.r[3]))) { sentenceNum++; wordNum = 0;} //sentence number +1 when meet character period.
            if (compare) {
                ca.Ldr(Integer.toBinaryString(SEARCH + increment), 0, 0, ms); //store the first character of userInput to R0.
                if (!Arrays.toString(ca.r[1]).equals(Arrays.toString(ca.r[0]))) {
                    compare = false;
                    ca.Ldr(Integer.toBinaryString(SEARCH), 0, 0, ms);
                    increment = 0;
                    continue;
                }
                increment++;
                if (increment == userInput.length()-1) {find = true; break;} //minus the last character, which is ETX.
            }
        }

        if (find) {
            System.out.println("The word you are searching for is: \"" + userInput + "\" at :");
            System.out.println("Sentence : " + sentenceNum);
            System.out.println("Word : " + wordNum);
        } else {
            System.out.println("The word you are searching for is \"" + userInput + "\" ,doesn't exists!");
        }
    }
}
