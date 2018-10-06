
/**
 * ComputerArchitecture Project.
 *
 * @author (6461-Group3)
 * @version 1.1
 * 9/21/2018
 */

import java.io.*;
import java.net.URL;
import java.util.Arrays;

public class ComputerArchitecture {
	// registers
	private static int[] pc = new int[12];
	private static int[][] r = new int[4][16]; // general purpose registers
	private static int[] mar = new int[16];
	private static int[] mbr = new int[16];
	private static int[] mfr = new int[4];
	private static int[] cc = new int[4];
	private static int[] ir = new int[16];
	private static int[][] x = new int[4][16]; // three index registers
	// memory
//	private int[][] memory1 = new int[2048][16];
//	private int[][] memory2 = new int[2048][16]; // make sure the memory can expand when required
	// extra needed variables
	public int stepByStep = 0; // 0 for normal mode, 1 for single-step mode
	private int effectiveAddress = 0; // effective address calculated by function decode()
	private int generalRegInUse;
	private int indexRegInUse;
	private int instructionsNum; // count total number of instructions in the file.
	private int[] rTemp = new int[16]; // used in indirect mode.
	private String[] current_instruction = new String[100000];

	public int[] getter_r(int num) {
		int[][] r_temp = new int[4][16];
		for (int i = 0; i < r[num].length; i++) {
			r_temp[num][i] = r[num][i];
		}
		return r_temp[num];
	}

	public int[] getter_x(int num) {
		int[][] x_temp = new int[4][16];
		for (int i = 0; i < x[num].length; i++) {
			x_temp[num][i] = x[num][i];
		}
		return x_temp[num];
	}

	public int[] getter_pc() {
		int[] pc_temp = new int[12];
		for (int i = 0; i < pc.length; i++) {
			pc_temp[i] = pc[i];
		}
		return pc_temp;
	}

	public int[] getter_mar() {
		int[] mar_temp = new int[16];
		for (int i = 0; i < mar.length; i++) {
			mar_temp[i] = mar[i];
		}
		return mar_temp;
	}

	public int[] getter_mbr() {
		int[] mbr_temp = new int[16];
		for (int i = 0; i < mbr.length; i++) {
			mbr_temp[i] = mbr[i];
		}
		return mbr_temp;
	}

	public int[] getter_mfr() {
		int[] mfr_temp = new int[4];
		for (int i = 0; i < mfr.length; i++) {
			mfr_temp[i] = mfr[i];
		}
		return mfr_temp;
	}

	public int[] getter_cc() {
		int[] cc_temp = new int[4];
		for (int i = 0; i < cc.length; i++) {
			cc_temp[i] = cc[i];
		}
		return cc_temp;
	}

	public int[] getter_ir() {
		int[] ir_temp = new int[16];
		for (int i = 0; i < ir.length; i++) {
			ir_temp[i] = ir[i];
		}
		return ir_temp;
	}

	public int[] getter_rTemp() {
		int[] rTemp_temp = new int[16];
		for (int i = 0; i < rTemp.length; i++) {
			rTemp_temp[i] = rTemp[i];
		}
		return rTemp_temp;
	}

	public int[] getter_mem(int num, MemorySystem ms) {
		int[] mem_temp = new int[16];
		for (int i = 0; i < 16; i++) {
//			mem_temp[i] = memory1[num][i];
			mem_temp[i] = ms.getMemory(num, i);
		}
		return mem_temp;
	}

	public void setter_r(int num) {
		for (int i = 0; i < 16; i++) {
			if (i < 10) {
				r[num][i] = UI.array_switch_button[i].getText().charAt(13) - '0';
			} else {
				r[num][i] = UI.array_switch_button[i].getText().charAt(14) - '0';
			}
		}
	}

	public void setter_pc() {
		for (int i = 0; i < 12; i++) {
			if (i < 10) {
				pc[i] = UI.array_switch_button[i].getText().charAt(13) - '0';
			} else {
				pc[i] = UI.array_switch_button[i].getText().charAt(14) - '0';
			}
		}
	}

	public void setter_mem(int num, MemorySystem ms) {
		for (int i = 0; i < 16; i++) {
			if (i < 10) {
//				memory1[num][i] = UI.array_switch_button[i].getText().charAt(13) - '0';
				ms.setMemory(num, i, UI.array_switch_button[i].getText().charAt(13) - '0');
			} else {
//				memory1[num][i] = UI.array_switch_button[i].getText().charAt(14) - '0';
				ms.setMemory(num, i, UI.array_switch_button[i].getText().charAt(14) - '0');
			}
		}
	}

	// load instructions in file to the array memory1.
	public void loadFile(String fileName, MemorySystem ms) throws IOException {
		InputStream input = readFile(fileName);
		BufferedReader bufferedInput = new BufferedReader(new InputStreamReader(input));

		String instructions = bufferedInput.readLine();
		current_instruction[0] = instructions;
		int i = 1000;
		while (instructions != null) {
			for (int j = 0; j < 16; j++) {
//				memory1[i][j] = Character.getNumericValue(instructions.charAt(j)); // split the String instructions into
				// 16 ints and then store them to
				// memory 1000-.
				ms.setMemory(i, j, Character.getNumericValue(instructions.charAt(j)));
			}
			instructionsNum += 1;
			i += 1;
			instructions = bufferedInput.readLine();
			current_instruction[i - 1000] = instructions;
		}

		// print
		System.out.println("In function loadFile() :");
	}

	/*
	 * Test for the function loadFile public void testLoadFile() { int i = 0;
	 *
	 * while (i<5) { String s = ""; for (int j=0;j<16;j++) { s = s +
	 * String.valueOf(memory1[i][j]); } System.out.println(s); i += 1; }
	 *
	 * }
	 */

	private InputStream readFile(String fileName) throws IOException {
		if (fileName == null) {
			throw new IOException("Error! fileName is null!");
		}
		URL file = getClass().getClassLoader().getResource(fileName);
		if (file == null) {
			throw new IOException("Error! Instruction file doesn't exist!");
		}
		return file.openStream();
	}

	// May have overflow problem, but doesn't need to pay attention to at project
	// part I.
	public void pcIncrement() throws IOException {
		for (int i = 11; i >= 0; i--) {
			if (pc[i] == 0) {
				pc[i] = 1;
				break;
			}
			if (pc[i] == 1) {
				pc[i] = 0;
			}
		}

		// print
		System.out.println("In function pcIncrement() :");
	}

	public int calMemAddr() throws IOException {
		int address = 0;
		for (int i = 15; i >= 4; i--) {
			if (mar[i] == 1) {
				address = address + (int) Math.pow(mar[i] * 2, (15 - i));
			}
		}

		// print
		System.out.println("In function calMemAddr() :");
		return address;
	}

	// fetch instruction from pc to mar.
	public void fetchFromPcToMar() throws IOException {
		for (int i = 11; i >= 0; i--) {
			mar[i + 4] = pc[i];
		}
		for (int i = 0; i < 4; i++) {
			mar[i] = 0;
		}

		// print
		System.out.println("In function fetchFromPcToMar() :");
	}

	public void fetchFromMemToMbr(int addr, MemorySystem ms) throws IOException {
//		for (int i = 0; i < 16; i++) {
////			mbr[i] = memory1[addr][i];
//		}
		int tempLen = 0;

		String temp = Integer.toBinaryString(ms.execute(addr, 1, mbr));
		tempLen = temp.length();
		for (int i = 0; i < tempLen; i++) {
			mbr[i + (16 - tempLen)] = Character.getNumericValue(temp.charAt(i));
		}
		for (int i = 0; i < (16 - tempLen); i++) {
			mbr[i] = 0;
		}

		// print
		System.out.println("In function fetchFromMemToMbr() :");
	}

	public void storeMbrToMem(int addr, MemorySystem ms) throws IOException {
//		for (int i = 0; i < 16; i++) {
////			memory1[addr][i] = mbr[i];
////			ms.setMemory(addr, i, mbr[i]);
////		}
		ms.execute(addr, 0, mbr);
		// print
		System.out.println("In function storeMbrToMem() :");
	}

	public void moveMbrToIr() throws IOException {
		for (int i = 0; i < 16; i++) {
			ir[i] = mbr[i];
		}

		// print
		System.out.println("In function moveMbrToIr() :");
	}

	public void moveMbrToReg(int reg) throws IOException {
		for (int i = 0; i < 16; i++) {
			r[reg][i] = mbr[i];
		}

		// print
		System.out.println("In function moveMbrToReg() :");
	}

	public void moveMbrToIndexReg(int index) throws IOException {
		for (int i = 0; i < 16; i++) {
			x[index][i] = mbr[i];
		}

		// print
		System.out.println("In function moveMbrToIndexReg() :");
	}

	public void moveRegToMbr(int reg) throws IOException {
		for (int i = 0; i < 16; i++) {
			mbr[i] = r[reg][i];
		}

		// print
		System.out.println("In function moveRegToMbr() :");
	}

	public void moveIndexRegToMbr(int index) throws IOException {
		for (int i = 0; i < 16; i++) {
			mbr[i] = x[index][i];
		}

		// print
		System.out.println("In function moveIndexRegToMbr() :");
	}

	public void moveAddrToMar(String addr) throws IOException {
		int addrLen = addr.length();
		for (int i = 0; i < addrLen; i++) {
			mar[16 - addrLen + i] = Character.getNumericValue(addr.charAt(i));
		}
		for (int i = 0; i < (16 - addrLen); i++) {
			mar[i] = 0;
		}

		// print
		System.out.println("In function moveAddrToMar() :");
	}

	public void moveMbrToRTemp() throws IOException {
		for (int i = 0; i < 16; i++) {
			rTemp[i] = mbr[i];
		}

		// print
		System.out.println("In function moveMbrToIr() :");
	}

	public void moveRTempToMar() throws IOException {
		for (int i = 0; i < 16; i++) {
			mar[i] = rTemp[i];
		}

		// print
		System.out.println("In function moveMbrToIr() :");
	}
	
	public void moveMbrToPc() throws IOException {
		for (int i = 0; i < 16; i++) {
			pc[i] = mbr[i];
		}

		// print
		System.out.println("In function moveMbrToPc() :");
	}
	
	//instruction JZ
	private void Jz(String addr, int reg, int indirect, MemorySystem ms) throws IOException {
		for(int i=0;i<16;i++){
			if (r[reg][i]==1){
				pcIncrement();
				System.out.println("In function Jz() :");
				return;
			}
		}
		if (indirect == 0) {
			int addrLen = addr.length();
			// load address to register reg
			for (int i = 0; i < addrLen; i++) {
				pc[16 - addrLen + i] = Character.getNumericValue(addr.charAt(i));
			}
			for (int i = 0; i < (16 - addrLen); i++) {
				pc[i] = 0;
			}
		} else {
			int address = 0;
			// move address to mar
			moveAddrToMar(addr);
			// calculate address in mar
			address = calMemAddr();
			// fetch values in memory to mbr
			fetchFromMemToMbr(address, ms);
			// move mbr to register pc
			moveMbrToPc();
			
		}
		
		// print
		System.out.println("In function Jz() :");
	}
	
	
	//instruction JNE
	private void Jne(String addr, int reg, int indirect, MemorySystem ms) throws IOException {
		for(int i=0;i<16;i++){
			if (r[reg][i]==1){

				if (indirect == 0) {
					int addrLen = addr.length();
					// load address to register reg
					for (int j = 0; j < addrLen; j++) {
						pc[16 - addrLen + j] = Character.getNumericValue(addr.charAt(j));
					}
					for (int j = 0; j < (16 - addrLen);j++) {
						pc[j] = 0;
					}
				} else {
					int address = 0;
					// move address to mar
					moveAddrToMar(addr);
					// calculate address in mar
					address = calMemAddr();
					// fetch values in memory to mbr
					fetchFromMemToMbr(address, ms);
					// move mbr to register pc
					moveMbrToPc();
					
				}
				
				// print
				System.out.println("In function Jne() :");
				return;
			}
		}
		pcIncrement();	
		// print
		System.out.println("In function Jne() :");
	}

	//instruction JCC
	private void Jcc(String addr, int ccindex, int indirect, MemorySystem ms) throws IOException {
		if (cc[ccindex]==1){
			if (indirect == 0) {
				int addrLen = addr.length();
				// load address to register reg
				for (int i = 0; i < addrLen; i++) {
					pc[16 - addrLen + i] = Character.getNumericValue(addr.charAt(i));
				}
				for (int i = 0; i < (16 - addrLen);i++) {
					pc[i] = 0;
				}
			} else {
				int address = 0;
				// move address to mar
				moveAddrToMar(addr);
				// calculate address in mar
				address = calMemAddr();
				// fetch values in memory to mbr
				fetchFromMemToMbr(address, ms);
				// move mbr to register pc
				moveMbrToPc();
				
			}
			System.out.println("In function Jcc() :");
		}
		else {
			pcIncrement();
			//print
			System.out.println("In function Jcc() :");
			
		}
	}
	
	//instruction JMA
		private void Jma(String addr, int reg, int indirect, MemorySystem ms) throws IOException {
			if (indirect == 0) {
				int addrLen = addr.length();
				// load address to register reg
				for (int i = 0; i < addrLen; i++) {
					pc[16 - addrLen + i] = Character.getNumericValue(addr.charAt(i));
				}
				for (int i = 0; i < (16 - addrLen);i++) {
					pc[i] = 0;
				}
			} else {
				int address = 0;
				// move address to mar
				moveAddrToMar(addr);
				// calculate address in mar
				address = calMemAddr();
				// fetch values in memory to mbr
				fetchFromMemToMbr(address, ms);
				// move mbr to register pc
				moveMbrToPc();
				
			}
			System.out.println("In function Jma() :");
		}
		
		//instruction RFS
		private void Rfs(String immed, int reg, int indirect, MemorySystem ms) throws IOException {
			int immedLen = immed.length();
			// load address to register reg
			for (int i = 0; i < immedLen; i++) {
				r[0][16 - immedLen + i] = Character.getNumericValue(immed.charAt(i));
			}
			for (int i = 0; i < (16 - immedLen);i++) {
				r[0][i] = 0;
			}
			for (int i=0; i<16; i++){
				pc[i]=r[3][i];
			}
			System.out.println("In function Rfs() :");
		}
		
		//instruction SOB
		private void Sob(String addr, int reg, int indirect, MemorySystem ms) throws IOException {
			boolean overzeor=false;
			for (int i=15; i>=0; i--){
				if (r[reg][i]==1){
					r[reg][i]=0;
					
					break;
				}
				else {
					r[reg][i]=1;
				}
			}
			for (int i=15; i>=1; i--){
				if (r[reg][i]==1){
					overzeor=true;
					break;
				}
			}
			
			
			
			if (r[reg][0]==0 && overzeor){
				if (indirect == 0) {
					int addrLen = addr.length();
					// load address to register reg
					for (int i = 0; i < addrLen; i++) {
						pc[16 - addrLen + i] = Character.getNumericValue(addr.charAt(i));
					}
					for (int i = 0; i < (16 - addrLen); i++) {
						pc[i] = 0;
					}
				} else {
					int address = 0;
					// move address to mar
					moveAddrToMar(addr);
					// calculate address in mar
					address = calMemAddr();
					// fetch values in memory to mbr
					fetchFromMemToMbr(address, ms);
					// move mbr to register pc
					moveMbrToPc();
					
				}
				
			}
			else {
				pcIncrement();
			}
			System.out.println("In function sob() :");
			
		}
		
		
		//JGE
private void Jge(String addr, int reg, int indirect, MemorySystem ms) throws IOException {		
			if (r[reg][0]==0){
				if (indirect == 0) {
					int addrLen = addr.length();
					// load address to register reg
					for (int i = 0; i < addrLen; i++) {
						pc[16 - addrLen + i] = Character.getNumericValue(addr.charAt(i));
					}
					for (int i = 0; i < (16 - addrLen); i++) {
						pc[i] = 0;
					}
				} else {
					int address = 0;
					// move address to mar
					moveAddrToMar(addr);
					// calculate address in mar
					address = calMemAddr();
					// fetch values in memory to mbr
					fetchFromMemToMbr(address, ms);
					// move mbr to register pc
					moveMbrToPc();
					
				}
				
			}
			else {
				pcIncrement();
			}
			System.out.println("In function Jge() :");
			
		}
		
		
		
	

	// instruction LDR
	private void Ldr(String addr, int reg, int indirect, MemorySystem ms) throws IOException {

		int address = 0;

		// move address to register mar.
		moveAddrToMar(addr);
		// calculate memory address in register mar.
		address = calMemAddr();
		// fetch values from memory1 to mbr.
		fetchFromMemToMbr(address, ms);
		// move values in mbr to register reg
		moveMbrToReg(reg);

		address = 0;
		if (indirect == 1) {
			for (int i = 0; i < 16; i++) {
				if (r[reg][i] == 1) {
					address = address + (int) Math.pow(r[reg][i] * 2, (15 - i));
				}
			}
			moveAddrToMar(Integer.toBinaryString(address));
			address = calMemAddr();
			fetchFromMemToMbr(address, ms);
			moveMbrToReg(reg);
		}
		// print
		System.out.println("In function Ldr() :");
	}

	// instruction STR
	private void Str(String addr, int reg, int indirect, MemorySystem ms) throws IOException {
		int address = 0;
		if (indirect == 0) {
			// move address to mar
			moveAddrToMar(addr);
			// move register reg to mbr
			moveRegToMbr(reg);
			// calculate address in mar
			address = calMemAddr();
			// store the values in mbr to memory[mar[]].
			storeMbrToMem(address, ms);
		} else {
			// move address to mar
			moveAddrToMar(addr);
			// calculate address in mar
			address = calMemAddr();
			// fetch address in memory to mbr
			fetchFromMemToMbr(address, ms);
			// move values in mbr to rTemp
			moveMbrToRTemp();
			// move rTemp to mar
			moveRTempToMar();
			// move register reg to mbr
			moveRegToMbr(reg);
			// calculate address in mar
			address = calMemAddr();
			// store the values in mbr to memory[mar[]]
			storeMbrToMem(address, ms);
		}
		// print
		System.out.println("In function Str() :");
	}

	// instruction LDA
	private void Lda(String addr, int reg, int indirect, MemorySystem ms) throws IOException {

		if (indirect == 0) {
			int addrLen = addr.length();
			// load address to register reg
			for (int i = 0; i < addrLen; i++) {
				r[reg][16 - addrLen + i] = Character.getNumericValue(addr.charAt(i));
			}
			for (int i = 0; i < (16 - addrLen); i++) {
				r[reg][i] = 0;
			}
		} else {
			int address = 0;
			// move address to mar
			moveAddrToMar(addr);
			// calculate address in mar
			address = calMemAddr();
			// fetch values in memory to mbr
			fetchFromMemToMbr(address, ms);
			// move mbr to register reg
			moveMbrToReg(reg);
		}
		// print
		System.out.println("In function Lda() :");
	}

	// instruction LDX
	private void Ldx(String addr, int index, int indirect, MemorySystem ms) throws IOException {
		int address = 0;
		// move address to mar
		moveAddrToMar(addr);
		// calculate values in mar
		address = calMemAddr();
		// fetch values from memory to mbr
		fetchFromMemToMbr(address, ms);
		// move values in mbr to index register x[index]
		moveMbrToIndexReg(index);

		address = 0;
		if (indirect == 1) {
			// move index register to mar
			for (int i = 0; i < 16; i++) {
				mar[i] = x[index][i];
			}
			// calculate address in mar
			address = calMemAddr();
			// fetch values from memory to mbr
			fetchFromMemToMbr(address, ms);
			// move values in mbr to index register x[index]
			moveMbrToIndexReg(index);
		}

		// print
		System.out.println("In function Ldx() :");
	}

	// instruction STX
	private void Stx(String addr, int index, int indirect, MemorySystem ms) throws IOException {
		int address = 0;

		if (indirect == 0) {
			// move address to mar
			moveAddrToMar(addr);
			// move values in index register x[index] to mbr
			moveIndexRegToMbr(index);
			// calculate address in mar
			address = calMemAddr();
			// store the values in mbr to memory1[mar[]]
			storeMbrToMem(address, ms);
		} else {
			// move address to mar
			moveAddrToMar(addr);
			// calculate address in mar
			address = calMemAddr();
			// fetch from memory to mbr
			fetchFromMemToMbr(address, ms);
			// move mbr to rTemp
			moveMbrToRTemp();
			// move rTemp to mar
			moveRTempToMar();
			// move values in index register x[index] to mbr
			moveIndexRegToMbr(index);
			// calculate address in mar
			address = calMemAddr();
			// store the values in mbr to memory1[mar[]]
			storeMbrToMem(address, ms);
		}
		// print
		System.out.println("In function Stx() :");
	}

	// AMR
	private void Amr(int r, String addr, int indirect, MemorySystem ms) throws IOException {
		int address = 0;
		moveAddrToMar(addr);
		address = calMemAddr();
		fetchFromMemToMbr(address, ms);
		moveMbrToRTemp();
		if (indirect == 0) { // direct addressing
			moveRegToMbr(r);
			// MBR += rTemp
			for (int i = 15; i >= 0; i--) {
				if (mbr[i] + rTemp[i] < 2) {
					mbr[i] += rTemp[i];
				} else {
					if (i != 0) { // not overflow
						mbr[i] += 2 - (mbr[i] + rTemp[i]);
						mbr[i - 1]++;
					} else {
						// i == 0, overflow
						cc[0] = 1;
						System.out.println("***Warning: Overflow detected in MBR, the result could be erroneous!***");
						UI.screen_update();
					}
				}
			}
			moveMbrToReg(r);
		} else { // indirect addressing
			moveRTempToMar();
			address = calMemAddr();
			fetchFromMemToMbr(address, ms);
			moveMbrToRTemp();
			moveRegToMbr(r);
			// MBR += rTemp
			for (int i = 15; i >= 0; i--) {
				if (mbr[i] + rTemp[i] < 2) {
					mbr[i] += rTemp[i];
				} else {
					if (i != 0) { // not overflow
						mbr[i] += 2 - (mbr[i] + rTemp[i]);
						mbr[i - 1]++;
					} else {
						// i == 0, overflow
						cc[0] = 1;
						System.out.println("***Warning: Overflow detected in MBR, the result could be erroneous!***");
						UI.screen_update();
					}
				}
			}
			moveMbrToReg(r);
		}
		System.out.println("In function AMR() :");
	}

	// SMR
	private void Smr(int r, String addr, int indirect, MemorySystem ms) throws IOException {
		int address = 0;
		moveAddrToMar(addr);
		address = calMemAddr();
		fetchFromMemToMbr(address, ms);
		moveMbrToRTemp();
		if (indirect == 0) { // direct addressing
			moveRegToMbr(r);
			// MBR -= rTemp
			for (int i = 15; i >= 0; i--) {
				if (mbr[i] - rTemp[i] >= 0) {
					mbr[i] -= rTemp[i];
				} else {
					if (i != 0) { // not underflow
						mbr[i] = mbr[i] + 2 - rTemp[i];
						mbr[i - 1]--;
					} else {
						// i == 0, underflow
						cc[1] = 1;
						System.out.println("***Warning: Underflow detected in MBR, the result could be erroneous!***");
						UI.screen_update();
					}
				}
			}
			moveMbrToReg(r);
		} else { // indirect addressing
			moveRTempToMar();
			address = calMemAddr();
			fetchFromMemToMbr(address, ms);
			moveMbrToRTemp();
			moveRegToMbr(r);
			// MBR -= rTemp
			for (int i = 15; i >= 0; i--) {
				if (mbr[i] - rTemp[i] >= 0) {
					mbr[i] -= rTemp[i];
				} else {
					if (i != 0) { // not underflow
						mbr[i] = mbr[i] + 2 - rTemp[i];
						mbr[i - 1]--;
					} else {
						// i == 0, underflow
						cc[1] = 1;
						System.out.println("***Warning: Underflow detected in MBR, the result could be erroneous!***");
						UI.screen_update();
					}
				}
			}
			moveMbrToReg(r);
		}
		System.out.println("In function SMR() :");
	}

	// AIR
	private void Air(int reg, String immed, MemorySystem ms) throws IOException {
		// fill the immed to 5 bits
		int temp = 5 - immed.length();
		for (int i = 0; i < temp; i++) {
			immed = "0".concat(immed);
		}
		for (int i = 15; i >= 11; i--) {
//			System.out.println("r[reg][i] = " + r[reg][i]);
//			System.out.println("immed.charAt(i - 11) = " + Character.getNumericValue(immed.charAt(i - 11)) + "\n");
			if (r[reg][i] + Character.getNumericValue(immed.charAt(i - 11)) < 2) {
				r[reg][i] += Character.getNumericValue(immed.charAt(i - 11));
			} else {
				if (i != 11) {
					r[reg][i] += 2 - (r[reg][i] + Character.getNumericValue(immed.charAt(i - 11)));
					r[reg][i - 1]++;
				} else {
					cc[0] = 1;
					System.out.println(
							"***Warning: Overflow detected in R[" + reg + "], the result could be erroneous!***");
					UI.screen_update();
				}
			}
		}
	}

	// SIR
	private void Sir(int reg, String immed, MemorySystem ms) throws IOException {
		// fill the immed to 5 bits
		int temp = 5 - immed.length();
		for (int i = 0; i < temp; i++) {
			immed = "0".concat(immed);
		}
		for (int i = 15; i >= 11; i--) {
//			System.out.println("r[reg][i] = " + r[reg][i]);
//			System.out.println("immed.charAt(i - 11) = " + Character.getNumericValue(immed.charAt(i - 11)) + "\n");
			if (r[reg][i] - Character.getNumericValue(immed.charAt(i - 11)) >= 0) {
				r[reg][i] -= Character.getNumericValue(immed.charAt(i - 11));
			} else {
				if (i != 11) {
					r[reg][i] = r[reg][i] + 2 - Character.getNumericValue(immed.charAt(i - 11));
					r[reg][i - 1]--;
				} else {
					cc[1] = 1;
					System.out.println(
							"***Warning: Underflow detected in R[" + reg + "], the result could be erroneous!***");
					UI.screen_update();
				}
			}
		}
	}

	// decode the instruction in register IR
	public void decode(MemorySystem ms) throws Exception {
		String opcode = "0";

		// calculate opcode in base10
		int temp = 0;
		for (int i = 0; i < 6; i++) {
			if (ir[i] == 1) {
				temp = temp + (int) Math.pow(ir[i] * 2, (5 - i));
			}
		}
		// transfer opcode from base10 to base8
		opcode = Integer.toString(temp, 8);

		// calculate effective address in the instruction.

		int addrIx = 0; // store the address in index register.
		int address = 0;
		int indirect = 0; // 0 means instruction doesn't use indirect mode.

		// calculate address in instruction.
		for (int i = 15; i >= 11; i--) {
			if (ir[i] == 1) {
				address = address + (int) Math.pow(ir[i] * 2, (15 - i));
			}
		}
		int immed=address;
		// find specified index register.
		indexRegInUse = ir[8] * 2 + ir[9] * 1;
		// calculate specified general use register.
		generalRegInUse = ir[6] * 2 + ir[7] * 1;

		if (indexRegInUse != 0) {
			for (int i = 0; i < 16; i++) {
				if (x[indexRegInUse][i] == 1) {
					addrIx = addrIx + (int) Math.pow(x[indexRegInUse][i] * 2, (15 - i));
				}
			}

			effectiveAddress = address + addrIx;

		} else {
			effectiveAddress = address;
		}

		// if instruction use indirect mode.
		if (ir[10] == 1) {
			indirect = 1;
		}

		switch (opcode) {
		case "1":
			Ldr(Integer.toBinaryString(effectiveAddress), generalRegInUse, indirect, ms);
			break;
		case "2":
			Str(Integer.toBinaryString(effectiveAddress), generalRegInUse, indirect, ms);
			break;
		case "3":
			Lda(Integer.toBinaryString(effectiveAddress), generalRegInUse, indirect, ms);
			break;
		case "4":
			Amr(generalRegInUse, Integer.toBinaryString(effectiveAddress), indirect, ms);
			break;
		case "5":
			Smr(generalRegInUse, Integer.toBinaryString(effectiveAddress), indirect, ms);
			break;
		case "6":
			Air(generalRegInUse, Integer.toBinaryString(effectiveAddress), ms);
			break;
		case "7":
			Sir(generalRegInUse, Integer.toBinaryString(effectiveAddress), ms);
			break;
		
			
		case "10":
			Jz(Integer.toBinaryString(effectiveAddress), generalRegInUse, indirect, ms);
			break;
		case "11":
			Jne(Integer.toBinaryString(effectiveAddress), generalRegInUse, indirect, ms);
			break;
		case "12":
			int ccindex=generalRegInUse;
			Jcc(Integer.toBinaryString(effectiveAddress), ccindex, indirect, ms);
			break;
		case "13":
			Jma(Integer.toBinaryString(effectiveAddress), generalRegInUse, indirect, ms);
			break;
			
//		case "14":
//			Jsr(Integer.toBinaryString(effectiveAddress), generalRegInUse, indirect, ms);
//			break;
		
		case "15":
			Rfs(Integer.toBinaryString(immed), generalRegInUse, indirect, ms);
			break;
		case "16":
			Sob(Integer.toBinaryString(effectiveAddress), generalRegInUse, indirect, ms);
			break;
		case "17":
			Jge(Integer.toBinaryString(effectiveAddress), generalRegInUse, indirect, ms);
			break;
		

		// use general register bit [6],[7] to calculate index register, because the
		// index register is using these two bits in instruction ldx and stx.
		case "41":
			Ldx(Integer.toBinaryString(effectiveAddress), generalRegInUse, indirect, ms);
			break;
		case "42":
			Stx(Integer.toBinaryString(effectiveAddress), generalRegInUse, indirect, ms);
			break;
		}
	}

	public void display(MemorySystem ms) {
		System.out.println("The values in four GPRs :");
		System.out.println("r0 : " + Arrays.toString(r[0]));
		System.out.println("r1 : " + Arrays.toString(r[1]));
		System.out.println("r2 : " + Arrays.toString(r[2]));
		System.out.println("r3 : " + Arrays.toString(r[3]));
		System.out.println(
				"--------------------------------------------------------------------------------------------------------");
		System.out.println("The values in registers :");
		System.out.println("mar : " + Arrays.toString(mar));
		System.out.println("mbr : " + Arrays.toString(mbr));
		System.out.println("ir : " + Arrays.toString(ir));
		System.out.println("pc : " + Arrays.toString(pc));
		System.out.println(
				"--------------------------------------------------------------------------------------------------------");
		System.out.println("The values in index registers :");
		System.out.println("x1 : " + Arrays.toString(x[1]));
		System.out.println("x2 : " + Arrays.toString(x[2]));
		System.out.println(
				"--------------------------------------------------------------------------------------------------------");
		System.out.println("The memory cells :");
//		System.out.println("memory[7] : " + Arrays.toString(memory1[7]));
//		System.out.println("memory[16] : " + Arrays.toString(memory1[16]));
//		System.out.println("memory[20] : " + Arrays.toString(memory1[20]));
//		System.out.println("memory[25] : " + Arrays.toString(memory1[25]));
		ms.displayMainMem(7);
		ms.displayMainMem(16);
		ms.displayMainMem(20);
		ms.displayMainMem(25);

		System.out.println(
				"--------------------------------------------------------------------------------------------------------");
	}

	public void display_preload(MemorySystem ms) {
		display(ms);
//		System.out.println("pre-load program -->memory[1000] : " + Arrays.toString(memory1[1000]));
//		System.out.println("pre-load program -->memory[1001] : " + Arrays.toString(memory1[1001]));
//		System.out.println("pre-load program -->memory[1002] : " + Arrays.toString(memory1[1002]));
//		System.out.println("pre-load program -->memory[1003] : " + Arrays.toString(memory1[1003]));
//		System.out.println("pre-load program -->memory[1004] : " + Arrays.toString(memory1[1004]));
//		System.out.println("pre-load program -->memory[1005] : " + Arrays.toString(memory1[1005]));
//		System.out.println("pre-load program -->memory[1006] : " + Arrays.toString(memory1[1006]));
//		System.out.println("pre-load program -->memory[1007] : " + Arrays.toString(memory1[1007]));
//		System.out.println("pre-load program -->memory[1008] : " + Arrays.toString(memory1[1008]));
		ms.displayMainMem(1000);
		ms.displayMainMem(1001);
		ms.displayMainMem(1002);
		ms.displayMainMem(1003);
		ms.displayMainMem(1004);
		ms.displayMainMem(1005);
		ms.displayMainMem(1006);
		ms.displayMainMem(1007);
		ms.displayMainMem(1008);
		System.out.println(
				"--------------------------------------------------------------------------------------------------------");
		System.out.println("The program is ready to go.\n");
	}

	// initialize all the register to 0, effectiveAddress to -1, stepByStep to 0.
	public void init(MemorySystem ms) throws Exception {
		try {

			for (int i = 0; i < 12; i++) {
				if (11 - i >= Integer.toBinaryString(1000).length()) {
					pc[i] = 0;
				} else {
					pc[i] = Integer.toBinaryString(1000).charAt(i - 2) - '0';
				}
			}

			ms.init();

			for (int i = 0; i < 16; i++) {
				r[0][i] = 0;
				r[1][i] = 0;
				r[2][i] = 0;
				r[3][i] = 0;
				mar[i] = 0;
				mbr[i] = 0;
				rTemp[i] = 0;
				ir[i] = 0;
				x[0][i] = 0;
				x[1][i] = 0;
				x[2][i] = 0;
				x[3][i] = 0;
			}

			for (int i = 0; i < 4; i++) {
				cc[i] = 0;
				mfr[i] = 0;
			}

			effectiveAddress = 0;
			instructionsNum = 0;
			stepByStep = 0;

		} catch (Exception e) {
			throw new Exception("Error! init failed!");
		}

		// print
		System.out.println("In function init() :");
		display_preload(ms);
		UI.screen_update();
	}

	public void run(MemorySystem ms) throws IOException {
		int pcAddr;

		try {
			loadFile("test.txt", ms);
		} catch (Exception e) {

		}

		int insLen = instructionsNum;
		while (instructionsNum != 0) {
			System.out.println(
					"Successfully loaded.\nThe instruction is: " + current_instruction[insLen - instructionsNum]);
			fetchFromPcToMar();
			pcAddr = calMemAddr();
			fetchFromMemToMbr(pcAddr, ms);
			moveMbrToIr();
			try {
				decode(ms);
			} catch (Exception e) {

			}
			pcIncrement();

			// print
			display(ms);
			System.out.println("End of this instruction: " + current_instruction[insLen - instructionsNum]
					+ "\n--------------------------------------------------------------------------------------------------------\n");
			effectiveAddress = 0;
			instructionsNum -= 1;
		}
	}

	public void run_single_step(MemorySystem ms) throws IOException {
		int pcAddr;

		if (stepByStep == 0) {
			try {
				loadFile("test.txt", ms);
			} catch (Exception e) {

			}
		}
		if (stepByStep == instructionsNum) {
			System.out.println("No more instructions!");
			return;
		}
		System.out.println("Successfully loaded.\nThe instruction is: " + current_instruction[stepByStep]);
		fetchFromPcToMar();
		pcAddr = calMemAddr();
		fetchFromMemToMbr(pcAddr, ms);
		moveMbrToIr();
		try {
			decode(ms);
		} catch (Exception e) {

		}
		pcIncrement();

		// print
		display(ms);
		System.out.println("End of this instruction: " + current_instruction[stepByStep]
				+ "\n--------------------------------------------------------------------------------------------------------\n");
		effectiveAddress = 0;
		stepByStep++;
	}
}