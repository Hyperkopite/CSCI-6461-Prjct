
/**
 * ComputerArchitecture Project.
 *
 * @author (6461-Group3)
 * @version 3.0
 * 16/10/2018
 */
import java.io.*;
import java.net.URL;
import java.util.Arrays;

public class ComputerArchitecture {
	// registers
	public int[][] r = new int[10][16]; // general purpose registers
	private int[] pc = new int[12];
	private int[] mar = new int[16];
	private int[] mbr = new int[16];
	private int[] mfr = new int[4];
	private int[] cc = new int[4];
	private int[] ir = new int[16];
	private int[][] x = new int[4][16]; // three index registers
	// memory
//	private int[][] memory1 = new int[2048][16];
//	private int[][] memory2 = new int[2048][16]; // make sure the memory can expand when required
	// extra needed variables
	public boolean is_halted = false; // for halt instruction
	public boolean is_from_trap = false; // for detecting if the program just recovered from trap
	public int stepByStep = 0; // 0 for normal mode, 1 for single-step mode
	private int effectiveAddress = 0; // effective address calculated by function decode()
	private int generalRegInUse;
	private int indexRegInUse;
	private int instructionsNum; // count total number of instructions in the file.
	private int[] rTemp = new int[16]; // used in indirect mode.
	private String[] current_instruction = new String[1000];
	// for program2
	public final int START = 40;
	public int STOP = 0;

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
	 * 
	 * Test for the function loadFile public void testLoadFile() { int i = 0;
	 *
	 * 
	 * 
	 * while (i<5) { String s = ""; for (int j=0;j<16;j++) { s = s +
	 * 
	 * String.valueOf(memory1[i][j]); } System.out.println(s); i += 1; }
	 *
	 * 
	 * 
	 * }
	 * 
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
		for (int i = 11; i >= 0; i--) {
			pc[i] = mbr[i + 4];
		}
		// print
		System.out.println("In function moveMbrToPc() :");
	}

	// In
	public void In(String DevID, int reg, MemorySystem ms) throws IOException {
		int devid = 0;

		moveAddrToMar(DevID);
		devid = calMemAddr();
//	    for (int i=0; i<16; i++) {
//	        r[reg][i] = UI.switch_status[devid][i];
//	    }
		if (devid == 20) {
			String readIn;
			int len;
			InputStream input = readFile("Readin-for-program2.txt");
			BufferedReader bufferedInput = new BufferedReader(new InputStreamReader(input));

			readIn = bufferedInput.readLine();
			len = readIn.length();
			// convert character to binary string, then store them into memory, start at
			// memory1[40]
			for (int i = 0; i < len; i++) {
				String temp = Integer.toBinaryString((int) readIn.charAt(i));
				int k = 15;
				for (int j = temp.length() - 1; j >= 0; j--) {
					ms.setMemory(START + i, k, Character.getNumericValue(temp.charAt(j)));
					k--;
				}
				STOP = START + i;
//                ms.displayMainMem(START+i);
			}
            ms.setMemory(START + len, 15, 1);
            ms.setMemory(START + len, 14, 1); 
		}
	}

	// Out
	public void Out(String DevID, int reg) throws IOException {
		int devid = 0;

		moveAddrToMar(DevID);
		devid = calMemAddr();
		for (int i = 0; i < 16; i++) {
			UI.switch_status[devid][i] = r[reg][i];
		}
	}

	// Chk
	public void Chk(String DevID, int reg) throws IOException {
		moveAddrToMar(DevID);
		for (int i = 0; i < 16; i++) {
			r[reg][i] = mar[i];
		}
	}

	// instruction JZ
	private void Jz(String addr, int reg, int indirect, MemorySystem ms) throws IOException {
		for (int i = 1; i < 16; i++) {
			if (r[reg][i] == 1) {
				pcIncrement();
				System.out.println("In function Jz() :");
				return;
			}
		}
		if (indirect == 0) {
			int addrLen = addr.length();
			// load address to register reg
			for (int i = 0; i < addrLen; i++) {
				pc[12 - addrLen + i] = Character.getNumericValue(addr.charAt(i));
			}
			for (int i = 0; i < (12 - addrLen); i++) {
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

	// instruction JNE
	private void Jne(String addr, int reg, int indirect, MemorySystem ms) throws IOException {
		for (int i = 0; i < 16; i++) {
			if (r[reg][i] == 1) {
				if (indirect == 0) {
					int addrLen = addr.length();
					// load address to register reg
					for (int j = 0; j < addrLen; j++) {
						pc[12 - addrLen + j] = Character.getNumericValue(addr.charAt(j));
					}
					for (int j = 0; j < (12 - addrLen); j++) {
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

	// instruction JCC
	private void Jcc(String addr, int ccindex, int indirect, MemorySystem ms) throws IOException {
		if (cc[ccindex] == 1) {
			if (indirect == 0) {
				int addrLen = addr.length();
				// load address to register reg
				for (int i = 0; i < addrLen; i++) {
					pc[12 - addrLen + i] = Character.getNumericValue(addr.charAt(i));
				}
				for (int i = 0; i < (12 - addrLen); i++) {
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
		} else {
			pcIncrement();
			// print
			System.out.println("In function Jcc() :");
		}
	}

	// instruction JMA
	private void Jma(String addr, int indirect, MemorySystem ms) throws IOException {
		if (indirect == 0) {
			int addrLen = addr.length();
			// load address to register reg
			for (int i = 0; i < addrLen; i++) {
				pc[12 - addrLen + i] = Character.getNumericValue(addr.charAt(i));
			}
			for (int i = 0; i < (12 - addrLen); i++) {
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

	// JSR
	private void Jsr(String addr, int indirect, MemorySystem ms) throws IOException {
		pcIncrement();
		for (int i = 11; i >= 0; i--) {
			r[3][i + 4] = pc[i];
		}
		if (indirect == 0) {
			int addrLen = addr.length();
			// load address to register reg
			for (int i = 0; i < addrLen; i++) {
				pc[12 - addrLen + i] = Character.getNumericValue(addr.charAt(i));
			}
			for (int i = 0; i < (12 - addrLen); i++) {
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
		System.out.println("In function Jsr() :");
	}

	// instruction RFS
	private void Rfs(String immed, int reg, int indirect, MemorySystem ms) throws IOException {
		int immedLen = immed.length();
		// load address to register reg
		for (int i = 0; i < immedLen; i++) {
			r[0][16 - immedLen + i] = Character.getNumericValue(immed.charAt(i));
		}
		for (int i = 0; i < (16 - immedLen); i++) {
			r[0][i] = 0;
		}
		for (int i = 0; i < 12; i++) {
			pc[i] = r[3][i];
		}
		System.out.println("In function Rfs() :");
	}

	// instruction SOB
	private void Sob(String addr, int reg, int indirect, MemorySystem ms) throws IOException {
		boolean overzeor = false;
		for (int i = 15; i >= 0; i--) {
			if (r[reg][i] == 1) {
				r[reg][i] = 0;
				break;
			} else {
				r[reg][i] = 1;
			}
		}
		for (int i = 15; i >= 1; i--) {
			if (r[reg][i] == 1) {
				overzeor = true;
				break;
			}
		}
		if (r[reg][0] == 0 && overzeor) {
			if (indirect == 0) {
				int addrLen = addr.length();
				// load address to register reg
				for (int i = 0; i < addrLen; i++) {
					pc[12 - addrLen + i] = Character.getNumericValue(addr.charAt(i));
				}
				for (int i = 0; i < (12 - addrLen); i++) {
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
		} else {
			pcIncrement();
		}
		System.out.println("In function sob() :");
	}

	// JGE
	private void Jge(String addr, int reg, int indirect, MemorySystem ms) throws IOException {
		if (r[reg][0] == 0) {
			if (indirect == 0) {
				int addrLen = addr.length();
				// load address to register reg
				for (int i = 0; i < addrLen; i++) {
					pc[12 - addrLen + i] = Character.getNumericValue(addr.charAt(i));
				}
				for (int i = 0; i < (12 - addrLen); i++) {
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
		} else {
			for (int i = 1; i < 16; i++) {
				if (r[reg][i] == 1) {
					System.out.println("In function Jge() :");
					pcIncrement();
					return;
				}
			}
			if (indirect == 0) {
				int addrLen = addr.length();
				// load address to register reg
				for (int i = 0; i < addrLen; i++) {
					pc[12 - addrLen + i] = Character.getNumericValue(addr.charAt(i));
				}
				for (int i = 0; i < (12 - addrLen); i++) {
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
		System.out.println("In function Jge() :");
	}

	// MLT
	private void Mlt(int reg1, int reg2) throws IOException {
		// use reg2+1 as ac
		for (int i = 0; i < 16; i++) {
			r[reg2 + 1][i] = 0;
			r[reg1 + 1][i] = 0;
		}
		for (int i = 0; i < 16; i++)
			r[reg2 + 1][i] = r[reg1][i];
		for (int i = 1; i < 32; i++)
			r[reg1 + i / 16][i % 16] = 0;
		for (int i = 15; i > 0; i--) {
			if (r[reg2][i] == 1) {
				r[reg1][0] = 0;
				int vaule = 0;
				for (int j = (i + 15); j > 0; j--) {
					if (j > 15) {
						vaule = r[reg1 + 1][j - 15] + r[reg2 + 1][j - i] + r[reg1][0];
						r[reg1 + 1][j - 15] = vaule % 2;
						r[reg1][0] = vaule / 2;
					} else {
						if (j > i) {
							vaule = r[reg1][j] + r[reg2 + 1][j - i] + r[reg1][0];
							r[reg1][j] = vaule % 2;
							r[reg1][0] = vaule / 2;
						} else {
							vaule = r[reg1][j] + r[reg1][0];
							r[reg1][j] = vaule % 2;
							r[reg1][0] = vaule / 2;
						}
					}
				}
				if (r[reg1][0] == 1) {
					cc[0] = 1;
				}
			}
		}
		r[reg1][0] = 1;
		// check -*- or -*+ or +*+
		if (r[reg2 + 1][0] == r[reg2][0]) {
			r[reg1][0] = 0;
		}
		System.out.println("In function Mlt() :");
	}

	// Dvd
	public void Dvd(int reg1, int reg2) throws IOException {
		int k1 = 0, k2 = 0, k3 = 0;
		for (int i = 0; i < 16; i++) {
			r[reg2 + 1][i] = 0;
			r[reg1 + 1][i] = 0;
		}
		for (int i = 1; i < 16; i++) {
			if (r[reg1][i] == 1 && k1 == 0)
				k1 = i;
			if (r[reg2][i] == 1 && k2 == 0)
				k2 = i;
		}
		for (int i = 15; i > 0; i--) {
			if (r[reg1][i] == 1) {
				k3 = i;
				break;
			}
		}
		if (k2 == 0) {
			cc[2] = 1;
			System.out.println("In function Dvd() :");
			return;
		}
		for (int i = 0; i < 16; i++) {
			r[reg2 + 1][i] = r[reg1][i];
			r[reg1][i] = 0;
		}
		r[reg1][0] = 1;
		if (r[reg2][0] == r[reg2 + 1][0]) {
			r[reg1][0] = 0;
		}
		int index1 = k1;
		int index2 = k1;
		boolean loop = true;
		while (loop) {
			loop = false;
			for (int i = index1; i < 16; i++) {
				r[reg1 + 1][i] = r[reg2 + 1][i];
				if ((i - index2) >= (15 - k2)) {
					boolean same = true;
					for (int x = index2; x <= i; x++) {
						if (r[reg1 + 1][x] == 1 && r[reg2][x - index2 + k2] == 0) {
							r[reg1][i] = 1;
							index1 = i + 1;
							same = false;
							break;
						}
						if (r[reg1 + 1][x] == 0 && r[reg2][x - index2 + k2] == 1) {
							if (i < 15) {
								r[reg1 + 1][i + 1] = r[reg2 + 1][i + 1];
								r[reg1][i + 1] = 1;
								index1 = i + 2;
								same = false;
								break;
							} else {
								System.out.println("In function Dvd() :");
								return;
							}
						}
					}
					if (same) {
						r[reg1][i] = 1;
						index1 = i + 1;
					}
					for (int j = (index1 - 1); j >= index2; j--) {
						if (r[reg1 + 1][j] == 0 && r[reg2][j - index1 + 16] == 0) {
							r[reg1 + 1][j] = 0;
						} else if (r[reg1 + 1][j] == 1 && r[reg2][j - index1 + 16] == 0) {
							r[reg1 + 1][j] = 0;
						} else if (r[reg1 + 1][j] == 0 && r[reg2][j - index1 + 16] == 1) {
							r[reg1 + 1][j] = 1;
							for (int x = (j - 1); x >= index2; x--) {
								if (r[reg1 + 1][x] == 1) {
									r[reg1 + 1][x] = 0;
									break;
								}
								r[reg1 + 1][x] = 1;
							}
						} else {
							r[reg1 + 1][j] = 0;
						}
					}
					if (i == k3 && same) {
						return;
					}
					index2 = index1;
					for (int j = 1; j < 16; j++) {
						if (r[reg1 + 1][j] == 1) {
							index2 = j;
						}
					}
					loop = true;
					break;
				}
			}
		}
		System.out.println("In function Dvd() :");
	}

	// TRR
	private void Trr(int reg1, int reg2) throws IOException {
		for (int i = 0; i < 16; i++) {
			if (r[reg1][i] != r[reg2][i]) {
				cc[3] = 0;
				return;
			}
		}
		cc[3] = 1;
	}

	// AND
	public void And(int reg1, int reg2) throws IOException {
		for (int i = 0; i < 16; i++) {
			if (r[reg1][i] == 1 && r[reg2][i] == 1) {
				r[reg1][i] = 1;
			} else {
				r[reg1][i] = 0;
			}
		}
	}

	// ORR
	public void Orr(int reg1, int reg2) throws IOException {
		for (int i = 0; i < 16; i++) {
			if (r[reg1][i] == 1 || r[reg2][i] == 1) {
				r[reg1][i] = 1;
			} else {
				r[reg1][i] = 0;
			}
		}
	}

	// Not
	private void Not(int reg) throws IOException {
		for (int i = 0; i < 16; i++) {
			if (r[reg][i] == 1)
				r[reg][i] = 0;
			else
				r[reg][i] = 1;
		}
	}

	// instruction LDR
	public void Ldr(String addr, int reg, int indirect, MemorySystem ms) throws IOException {
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
	public void Str(String addr, int reg, int indirect, MemorySystem ms) throws IOException {
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
	public void Ldx(String addr, int index, int indirect, MemorySystem ms) throws IOException {
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
		int address = 0, a, b, res, temp_length;
		String tmp_str = "", tmp_str2 = "";
		boolean x = false, is_negative = false;
		moveAddrToMar(addr);
		address = calMemAddr();
		fetchFromMemToMbr(address, ms);
		moveMbrToRTemp();
		if (indirect == 0) { // direct addressing
			moveRegToMbr(r);
		} else { // indirect addressing
			moveRTempToMar();
			address = calMemAddr();
			fetchFromMemToMbr(address, ms);
			moveMbrToRTemp();
			moveRegToMbr(r);
		}
		// MBR += rTemp
		for (int i : mbr) {
			if (i == 0) {
				if (x) {
					tmp_str = tmp_str.concat(Integer.toString(i));
				}
			} else {
				tmp_str = tmp_str.concat(Integer.toString(i));
				if (!x) {
					x = true;
				}
			}
		}
		x = false;
		for (int i : rTemp) {
			if (i == 0) {
				if (x) {
					tmp_str2 = tmp_str2.concat(Integer.toString(i));
				}
			} else {
				tmp_str2 = tmp_str2.concat(Integer.toString(i));
				if (!x) {
					x = true;
				}
			}
		}
		// System.out.println(tmp_str + ',' + tmp_str2);
		// to judge if the content of mbr[] is a negative number
		a = tmp_str.length() == 16 ? -Integer.parseUnsignedInt(tmp_str.substring(1), 2)
				: tmp_str.length() == 0 ? 0 : Integer.parseUnsignedInt(tmp_str, 2);
		b = tmp_str2.length() == 16 ? -Integer.parseUnsignedInt(tmp_str2.substring(1), 2)
				: tmp_str2.length() == 0 ? 0 : Integer.parseUnsignedInt(tmp_str2, 2);
		res = a + b;
		// System.out.println(res);
		if (res >= 0) {
			tmp_str = Integer.toBinaryString(res);
		} else {
			tmp_str = Integer.toBinaryString(-res);
			is_negative = true;
		}
		// System.out.println("tmp_str: " + tmp_str);
		// judge if underflows
		if (tmp_str.length() >= 16) {
			cc[0] = 1;
			System.out.println("***Warning: Overflow detected in MBR, the result could be erroneous!***");
			System.out.println("In function AMR() :");
			UI.screen_update();
			return; // we can use trap code here instead of return
		}
		// fill the result to 16 bits
		temp_length = tmp_str.length();
		for (int i = 0; i < 16 - temp_length; i++) {
			if (i == 15 - temp_length && is_negative) {
				tmp_str = "1".concat(tmp_str);
			} else {
				tmp_str = "0".concat(tmp_str);
			}
		}
		for (int i = 0; i < 16; i++) {
			mbr[i] = Character.getNumericValue(tmp_str.charAt(i));
		}
		moveMbrToReg(r);
		System.out.println("In function AMR() :");
	}

	// SMR
	public void Smr(int r, String addr, int indirect, MemorySystem ms) throws IOException {
		int address = 0;
		int temp_length, res, a, b;
		boolean x = false, is_negative = false; // for cutting the tmp_str
		String tmp_str = "", tmp_str2 = "";
		moveAddrToMar(addr);
		address = calMemAddr();
		fetchFromMemToMbr(address, ms);
		moveMbrToRTemp();
		if (indirect == 0) { // direct addressing
			moveRegToMbr(r);
		} else { // indirect addressing
			moveRTempToMar();
			address = calMemAddr();
			fetchFromMemToMbr(address, ms);
			moveMbrToRTemp();
			moveRegToMbr(r);
		}
		// MBR -= rTemp
		for (int i : mbr) {
			if (i == 0) {
				if (x) {
					tmp_str = tmp_str.concat(Integer.toString(i));
				}
			} else {
				tmp_str = tmp_str.concat(Integer.toString(i));
				if (!x) {
					x = true;
				}
			}
		}
		x = false;
		for (int i : rTemp) {
			if (i == 0) {
				if (x) {
					tmp_str2 = tmp_str2.concat(Integer.toString(i));
				}
			} else {
				tmp_str2 = tmp_str2.concat(Integer.toString(i));
				if (!x) {
					x = true;
				}
			}
		}
		// to judge if the content of mbr[] is a negative number
		a = tmp_str.length() == 16 ? -Integer.parseUnsignedInt(tmp_str.substring(1), 2)
				: tmp_str.length() == 0 ? 0 : Integer.parseUnsignedInt(tmp_str, 2);
		b = tmp_str2.length() == 16 ? -Integer.parseUnsignedInt(tmp_str2.substring(1), 2)
				: tmp_str2.length() == 0 ? 0 : Integer.parseUnsignedInt(tmp_str2, 2);
		res = a - b;
		if (res >= 0) {
			tmp_str = Integer.toBinaryString(res);
		} else {
			tmp_str = Integer.toBinaryString(-res);
			is_negative = true;
		}
//			System.out.println("tmp_str: " + tmp_str);
		// judge if underflows
		if (tmp_str.length() >= 16) {
			cc[1] = 1;
			System.out.println("***Warning: Underflow detected in MBR, the result could be erroneous!***");
			System.out.println("In function SMR() :");
			UI.screen_update();
			return; // we can use trap code here instead of return
		}
		// fill the result to 16 bits
		temp_length = tmp_str.length();
		for (int i = 0; i < 16 - temp_length; i++) {
			if (i == 15 - temp_length && is_negative) {
				tmp_str = "1".concat(tmp_str);
			} else {
				tmp_str = "0".concat(tmp_str);
			}
		}
		for (int i = 0; i < 16; i++) {
			mbr[i] = Character.getNumericValue(tmp_str.charAt(i));
		}
		moveMbrToReg(r);
		System.out.println("In function SMR() :");
	}

	// AIR
	public void Air(int reg, String immed) {
		int temp_length, res, a, b;
		boolean x = false, is_negative = false; // for cutting the tmp_str
		String tmp_str = "";
		for (int i : r[reg]) {
			if (i == 0) {
				if (x) {
					tmp_str = tmp_str.concat(Integer.toString(i));
				}
			} else {
				tmp_str = tmp_str.concat(Integer.toString(i));
				if (!x) {
					x = true;
				}
			}
		}
		// to judge if the content of mbr[] is a negative number
		a = tmp_str.length() == 16 ? -Integer.parseUnsignedInt(tmp_str.substring(1), 2)
				: tmp_str.length() == 0 ? 0 : Integer.parseUnsignedInt(tmp_str, 2);
		b = Integer.parseUnsignedInt(immed, 2);
		res = a + b;
		System.out.println(a + ',' + b + ',' + res);
		if (res >= 0) {
			tmp_str = Integer.toBinaryString(res);
		} else {
			tmp_str = Integer.toBinaryString(-res);
			is_negative = true;
		}
		// judge if underflows
		if (tmp_str.length() >= 16) {
			cc[0] = 1;
			System.out.println("***Warning: Overflow detected in R[" + reg + "], the result could be erroneous!***");
			System.out.println("In function SIR() :");
			UI.screen_update();
			return; // we can use trap code here instead of return
		}
//		System.out.println("tmp_str: " + tmp_str);
		// fill the result to 16 bits
		temp_length = tmp_str.length();
		for (int i = 0; i < 16 - temp_length; i++) {
			if (i == 15 - temp_length && is_negative) {
				tmp_str = "1".concat(tmp_str);
			} else {
				tmp_str = "0".concat(tmp_str);
			}
		}
		for (int i = 0; i < 16; i++) {
			r[reg][i] = Character.getNumericValue(tmp_str.charAt(i));
		}
		System.out.println("In function AIR() :");
	}

	// SIR
	private void Sir(int reg, String immed) {
		int temp_length, res, a, b;
		boolean x = false, is_negative = false; // for cutting the tmp_str
		String tmp_str = "";
		for (int i : r[reg]) {
			if (i == 0) {
				if (x) {
					tmp_str = tmp_str.concat(Integer.toString(i));
				}
			} else {
				tmp_str = tmp_str.concat(Integer.toString(i));
				if (!x) {
					x = true;
				}
			}
		}
		// to judge if the content of mbr[] is a negative number
		a = tmp_str.length() == 16 ? -Integer.parseUnsignedInt(tmp_str.substring(1), 2)
				: tmp_str.length() == 0 ? 0 : Integer.parseUnsignedInt(tmp_str, 2);
		b = Integer.parseUnsignedInt(immed, 2);
		res = a - b;
		if (res >= 0) {
			tmp_str = Integer.toBinaryString(res);
		} else {
			tmp_str = Integer.toBinaryString(-res);
			is_negative = true;
		}
		// judge if underflows
		if (tmp_str.length() >= 16) {
			cc[1] = 1;
			System.out.println("***Warning: Underflow detected in R[" + reg + "], the result could be erroneous!***");
			System.out.println("In function SIR() :");
			UI.screen_update();
			return; // we can use trap code here instead of return
		}
//		System.out.println("tmp_str: " + tmp_str);
		// fill the result to 16 bits
		temp_length = tmp_str.length();
		for (int i = 0; i < 16 - temp_length; i++) {
			if (i == 15 - temp_length && is_negative) {
				tmp_str = "1".concat(tmp_str);
			} else {
				tmp_str = "0".concat(tmp_str);
			}
		}
		for (int i = 0; i < 16; i++) {
			r[reg][i] = Character.getNumericValue(tmp_str.charAt(i));
		}
		System.out.println("In function SIR() :");
	}

	// SRC
	public void Src(int reg, int count, int al, int lr) {
		for (int i = 0; i < count; i++) {
			if (lr == 1) { // shift left
				for (int j = 0; j < 16; j++) {
					if (j != 15) {
						r[reg][j] = r[reg][j + 1];
					} else {
						r[reg][j] = 0;
					}
				}
			} else { // shift right
				for (int j = 15; j >= 0; j--) {
					if (j != 0) {
						r[reg][j] = r[reg][j - 1];
					} else if (al == 1) { // logically shift
						r[reg][j] = 0;
					} // if it's arithmetically shift, keep the sign bit.
				}
			}
		}
		System.out.println("In function SRC() :");
	}

	// RRC
	private void Rrc(int reg, int count, int lr) {
		int temp;
		for (int i = 0; i < count; i++) {
			if (lr == 1) { // rotate left
				temp = r[reg][0];
				for (int j = 0; j < 16; j++) {
					if (j != 15) {
						r[reg][j] = r[reg][j + 1];
					} else {
						r[reg][j] = temp;
					}
				}
			} else { // rotate right
				temp = r[reg][15];
				for (int j = 15; j >= 0; j--) {
					if (j != 0) {
						r[reg][j] = r[reg][j - 1];
					} else {
						r[reg][j] = temp;
					}
				}
			}
		}
		System.out.println("In function RRC() :");
	}

//	//HLT
	private void Hlt() {
		is_halted = true;
		System.out.println("In function HLT() :");
	}

	// TRAP
	private void Trap(int trap_code, MemorySystem ms) throws IOException {
		int pcAddr;
		fetchFromPcToMar();
		// pc++ => mar++
		for (int i = 15; i >= 4; i--) {
			if (mar[i] == 0) {
				mar[i] = 1;
				break;
			}
			if (mar[i] == 1) {
				mar[i] = 0;
			}
		}
		// store pc+1 to mem[130], which is mem location 2.
		for (int i = 15; i >= 4; i--) {
			ms.setMemory(130, i, mar[i]);
		}
		
//		System.out.print("pc: ");
//		for (int i = 0; i < 12; i++) {
//			System.out.print(pc[i]);
//			if ((i + 1) % 4 == 0) {
//				System.out.print(' ');
//			}
//		}
//		System.out.print("\n");
//		for (int i = 0; i < 16; i++) {
//			System.out.print(ms.getMemory(130, i));
//		}
//		System.out.print("\n");
		
		// set mem[128] as mem location 0 and jump to mem[128].
		Jma("10000000", 0, ms);
		// parse the address in mem[128] (131)
		fetchFromPcToMar();
		pcAddr = calMemAddr();
		fetchFromMemToMbr(pcAddr, ms);
		
		// set the pc as 131+index of routines, it's the address of the entry of the
		// routine[index]
		pcAddr = 0;
		for (int i = 15; i >= 4; i--) {
			if (mbr[i] == 1) {
				pcAddr += (int) Math.pow(2, (15 - i));
			}
		}
//		System.out.println("pcaddr = " + pcAddr);
		Jma(Integer.toBinaryString(pcAddr + trap_code), 0, ms);
//		for (int i = 0; i < 12; i++) {
//			System.out.print(pc[i]);
//			if ((i + 1) % 4 == 0) {
//				System.out.print(' ');
//			}
//		}
//		System.out.print("\n");
		
		while (true) {
			fetchFromPcToMar();
			pcAddr = calMemAddr();
//			System.out.println("pcaddr = " + pcAddr);
			fetchFromMemToMbr(pcAddr, ms);
//			for (int i = 0; i < 16; i++) {
//				System.out.print(mbr[i]);
//			}
//			System.out.print("\n");
			moveMbrToPc();
			fetchFromPcToMar();
			pcAddr = calMemAddr();
			fetchFromMemToMbr(pcAddr, ms);
			moveMbrToIr();
			System.out.println("**********Trap to instruction: **********");
			for (int i = 0; i < 16; i++) {
				System.out.print(ir[i]);
			}
			System.out.print("\n");
			try {
				decode(ms);
			} catch (Exception e) {
			}
			if (is_halted) {
				is_halted = false; // nothing wrong here
				break;
			}
			pcIncrement();
			// print
			display(ms);
			System.out.println("End of this instruction: ");
			for (int i = 0; i < 16; i++) {
				System.out.print(ir[i]);
			}
			System.out.print(
					"\n--------------------------------------------------------------------------------------------------------\n");
		}
		// recover the original pc
		is_from_trap = true;
		System.out.println("**********Recovering from TRAP() :**********");
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
		int count = 0; // for instructions need count, like SRC, RRC.
		// calculate address in instruction.
		for (int i = 15; i >= 11; i--) {
			if (ir[i] == 1) {
				address = address + (int) Math.pow(ir[i] * 2, (15 - i));
			}
		}
		// calculate the number of count
		for (int i = 15; i >= 12; i--) {
			if (ir[i] == 1) {
				count += (int) Math.pow(ir[i] * 2, (15 - i));
			}
		}
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
		case "0":
			Hlt();
			break;
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
			Air(generalRegInUse, Integer.toBinaryString(address));
			break;
		case "7":
			Sir(generalRegInUse, Integer.toBinaryString(address));
			break;
		case "10":
			Jz(Integer.toBinaryString(effectiveAddress), generalRegInUse, indirect, ms);
			break;
		case "11":
			Jne(Integer.toBinaryString(effectiveAddress), generalRegInUse, indirect, ms);
			break;
		case "12":
			int ccindex = generalRegInUse;
			Jcc(Integer.toBinaryString(effectiveAddress), ccindex, indirect, ms);
			break;
		case "13":
			Jma(Integer.toBinaryString(effectiveAddress), indirect, ms);
			break;
		case "14":
			Jsr(Integer.toBinaryString(effectiveAddress), indirect, ms);
			break;
		case "15":
			Rfs(Integer.toBinaryString(address), generalRegInUse, indirect, ms);
			break;
		case "16":
			Sob(Integer.toBinaryString(effectiveAddress), generalRegInUse, indirect, ms);
			break;
		case "17":
			Jge(Integer.toBinaryString(effectiveAddress), generalRegInUse, indirect, ms);
			break;
		case "20":
			Mlt(generalRegInUse, indexRegInUse);
			break;
		case "21":
			Dvd(generalRegInUse, indexRegInUse);
			break;
		case "22":
			Trr(generalRegInUse, indexRegInUse);
			break;
		case "23":
			And(generalRegInUse, indexRegInUse);
			break;
		case "24":
			Orr(generalRegInUse, indexRegInUse);
			break;
		case "25":
			Not(generalRegInUse);
			break;
		case "31":
			Src(generalRegInUse, count, ir[8], ir[9]);
			break;
		case "32":
			Rrc(generalRegInUse, count, ir[9]);
			break;
		case "36":
			Trap(count, ms); // actually count == trap code
			break;
		// use general register bit [6],[7] to calculate index register, because the
		// index register is using these two bits in instruction ldx and stx.
		case "41":
			Ldx(Integer.toBinaryString(effectiveAddress), generalRegInUse, indirect, ms);
			break;
		case "42":
			Stx(Integer.toBinaryString(effectiveAddress), generalRegInUse, indirect, ms);
			break;
		case "61":
			In(Integer.toBinaryString(effectiveAddress), generalRegInUse, ms);
			break;
		case "62":
			Out(Integer.toBinaryString(effectiveAddress), generalRegInUse);
			break;
		case "63":
			Chk(Integer.toBinaryString(effectiveAddress), generalRegInUse);
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
//		ms.displayMainMem(7);
//		ms.displayMainMem(16);
//		ms.displayMainMem(20);
//		ms.displayMainMem(25);
//		ms.displayMainMem(256);
//		ms.displayMainMem(512);
//		ms.displayMainMem(513);
//		ms.displayMainMem(514);
		for (int i = 20; i < 33; i++) {
			ms.displayMainMem(i);
		}
		for (int i = 40; i < 45; i++) {
			ms.displayMainMem(i);
		}

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
				r[4][i] = 0;
				r[5][i] = 0;
				r[6][i] = 0;
				r[7][i] = 0;
				r[8][i] = 0;
				r[9][i] = 0;
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
			// set the content of mem[128] as "0000 0000 1000 0011" => mem[131], where
			// is the address of routines table
			for (int i = 0; i < 16; i++) {
				if (i == 8 || i == 14 || i == 15) {
					ms.setMemory(128, i, 1);
				} else {
					ms.setMemory(128, i, 0);
				}
			}
			// set the contents of routines table mem[131]~mem[146], for test here only has
			// 1 entry for routine[0]. Every routine has 20 instructions capacity,from
			// mem[147]~mem[467]
			String temp_str;
			int temp_len;
			for (int i = 0; i < 16; i++) {
				temp_str = Integer.toBinaryString(i * 20 + 147);
				temp_len = temp_str.length();
				for (int j = 0; j < 16 - temp_len; j++) {
					temp_str = "0".concat(temp_str);
				}
//				System.out.println(temp_str);
				for (int j = 0; j < 16; j++) {
					ms.setMemory(131 + i, j, Character.getNumericValue(temp_str.charAt(j)));
				}
//				for (int j = 0; j < 16; j++) {
//					System.out.print(ms.getMemory(131, j));
//				}
//				System.out.print("\n");
			}
			// instruction[0] of routine[0]: 0110010011000010 //SRC R0,2,1,1 [R0=4]
			for (int i = 0; i < 16; i++) {
				if (i == 1 || i == 2 || i == 5 || i == 8 || i == 9 || i == 14) {
					ms.setMemory(147, i, 1);
				} else {
					ms.setMemory(147, i, 0);
				}
			}
			// instruction[1] of routine[0]: 0000000000000000 //HLT
			for (int i = 0; i < 16; i++) {
				ms.setMemory(148, i, 0);
			}
			effectiveAddress = 0;
			instructionsNum = 0;
			stepByStep = 0;
			is_halted = false;
			is_from_trap = false;
//			is_over = false;
		} catch (Exception e) {
			throw new Exception("Error! init failed!");
		}
		// print
		System.out.println("In function init() :");
		display_preload(ms);
	}

	public void run(MemorySystem ms) throws IOException {
		int pcAddr;
		if (is_halted) {
			return;
		}
		try {
			loadFile("reProgram2.txt", ms);
		} catch (Exception e) {
		}
		while (true) {
			int pcValue = 0;
			for (int i = 11; i >= 0; i--) {
				if (pc[i] == 1) {
					pcValue = pcValue + (int) Math.pow(pc[i] * 2, (11 - i));
				}
			}
			int dis = pcValue;
//			System.out.println(pcValue);
			int[] pcBefore = new int[12];
			for (int i = 0; i < 12; i++) {
				pcBefore[i] = pc[i];
			}
			System.out.println("Successfully loaded.\nThe instruction is: " + current_instruction[dis - 1000]);
			fetchFromPcToMar();
			pcAddr = calMemAddr();
			fetchFromMemToMbr(pcAddr, ms);
			moveMbrToIr();
			try {
				decode(ms);
			} catch (Exception e) {
			}
			if (is_halted) {
				System.out.println("**Program has been halted or over.**");
				System.out.println("End of this instruction: " + current_instruction[dis - 1000]
						+ "\n--------------------------------------------------------------------------------------------------------\n");
				UI.screen_update();
				return;
			}
			if (is_from_trap) {
				// recover from original pc
				fetchFromMemToMbr(130, ms);
				moveMbrToPc();
				is_from_trap = false;
			} else {
				boolean ifInc = true;
				for (int i = 0; i < 12; i++) {
					if (pc[i] != pcBefore[i]) {
						ifInc = false;
						break;
					}
				}
				if (ifInc) {
					pcIncrement(); // U need to change this
				}
			}
			// print
			display(ms);
			System.out.println("End of this instruction: " + current_instruction[dis - 1000]
					+ "\n--------------------------------------------------------------------------------------------------------\n");
			effectiveAddress = 0;
			pcValue = 0;
			for (int i = 11; i >= 0; i--) {
				if (pc[i] == 1) {
					pcValue = pcValue + (int) Math.pow(pc[i] * 2, (11 - i));
				}
			}
//			System.out.println(pcValue);
//			System.out.println(insLen);
			if (pcValue > (1000 + instructionsNum)) {
				break;
			}
		}
		is_halted = true;
		System.out.println("**Program over.**");
	}

	public void run_single_step(MemorySystem ms) throws IOException {
		int pcAddr;
		if (is_halted) {
			return;
		}
		if (stepByStep == 0) {
			try {
				loadFile("test.txt", ms);
			} catch (Exception e) {
			}
		}
//		if (stepByStep == instructionsNum) {
//			System.out.println();
//			return;
//		}
		int pcValue = 0;
		for (int i = 11; i >= 0; i--) {
			if (pc[i] == 1) {
				pcValue = pcValue + (int) Math.pow(pc[i] * 2, (11 - i));
			}
		}
		int dis = pcValue;
//		System.out.println(pcValue);
		int[] pcBefore = new int[12];
		for (int i = 0; i < 12; i++) {
			pcBefore[i] = pc[i];
		}
		System.out.println("Successfully loaded.\nThe instruction is: " + current_instruction[dis - 1000]);
		fetchFromPcToMar();
		pcAddr = calMemAddr();
		fetchFromMemToMbr(pcAddr, ms);
		moveMbrToIr();
		try {
			decode(ms);
		} catch (Exception e) {
		}
		if (is_halted) {
			System.out.println("**Program has been halted or over.**");
			System.out.println("End of this instruction: " + current_instruction[dis - 1000]
					+ "\n--------------------------------------------------------------------------------------------------------\n");
			UI.screen_update();
			return;
		}
		if (is_from_trap) {
			fetchFromMemToMbr(130, ms);
			moveMbrToPc();
			is_from_trap = false;
		} else {
			boolean ifInc = true;
			for (int i = 0; i < 12; i++) {
				if (pc[i] != pcBefore[i]) {
					ifInc = false;
					break;
				}
			}
			if (ifInc) {
				pcIncrement(); // U need to change this
			}
		}
		// print
		display(ms);
		System.out.println("End of this instruction: " + current_instruction[dis - 1000]
				+ "\n--------------------------------------------------------------------------------------------------------\n");
		effectiveAddress = 0;
		pcValue = 0;
		for (int i = 11; i >= 0; i--) {
			if (pc[i] == 1) {
				pcValue = pcValue + (int) Math.pow(pc[i] * 2, (11 - i));
			}
		}
		if (pcValue > (1000 + instructionsNum)) {
			System.out.println("No more instructions!");
			return;
		}
	}
}