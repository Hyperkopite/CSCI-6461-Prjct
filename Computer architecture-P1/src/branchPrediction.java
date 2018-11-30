public class branchPrediction {
    public static void main(String[] args) throws Exception {
        MemorySystem ms = new MemorySystem();
        ComputerArchitecture ca = new ComputerArchitecture();

        try {
            ca.init(ms);
        } catch (Exception e) {

        }

        //store 8 to memory1[30]
        ca.Air(0, "001000");
        ca.Str("11110", 0, 0, ms);
        //store 2 to memory1[31]
        ca.Air(1, "000010");
        ca.Str("11111", 1, 0, ms);
        // load 8 and 2 to register 2 and 3
        ca.Ldr("11110", 2, 0, ms);
        ca.Ldr("11111", 3, 0, ms);
        //store values in the registers to memory[40-45]
        ca.Str("101000", 0, 0, ms);
        ca.Str("101001", 1, 0, ms);
        ca.Str("101010", 2, 0, ms);
        ca.Str("101011", 3, 0, ms);
        for (int i=0; i<16; i++) {
            ca.r[0][i] = ca.getter_mar()[i];
            ca.r[1][i] = ca.getter_mbr()[i];
        }
        ca.Str("101100", 0, 0, ms);
        ca.Str("101101", 1, 0, ms);
        //display values in registers before branch predict
        System.out.println("\n****Before branch, store all the current values in to memory: \n");
        for (int i=40; i<46; i++) {
        	if (i == 42) {
        		System.out.print("r2=> ");
        	}
        	if (i == 43) {
        		System.out.print("r3=> ");
        	}
        	if (i != 42 && i != 43) {
        		System.out.print("     ");	
        	}
        	ms.displayMainMem(i);
        }
        //always fetch the 'else' block in branch if-else
        System.out.println("\n*************************************\nPrediction selects the \"else\" branch\n*************************************\n");
        ca.Air(2,"000001");
        ca.Air(3,"000001");
        ca.Str("11110", 2, 0, ms); //store to mem[30]
        ca.Str("11111", 3, 0, ms); //store to mem[31]
        //display changed values in registers during branch predict
        System.out.println("\n****Values in r2 and r3 during branch prediction: \n");
        ms.displayMainMem(30);
        ms.displayMainMem(31);
        //after find out the predicted result was wrong, restore all the values and run the right instructions
        ca.Ldr("101010", 2, 0, ms);
        ca.Ldr("101011", 3, 0, ms);
        for (int i=0; i<16; i++) {
            ca.setter_mar(i, ms.getMemory(44, i));
            ca.setter_mbr(i, ms.getMemory(45, i));
        }
        ca.Air(2, "000011");
        ca.Air(3, "000011");
        ca.Str("11110", 2, 0, ms);
        ca.Str("11111", 3, 0, ms);
        //display the right values in registers
        System.out.println("\n****Prediction wrong, recovered to right values in r2 and r3:\n");
        ms.displayMainMem(30);
        ms.displayMainMem(31);
    }
}