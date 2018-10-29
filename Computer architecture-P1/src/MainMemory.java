import java.util.Arrays;

public class MainMemory {
	private int[][] memory1 = new int[2048][16];
	private int[][] memory2 = new int[2048][16]; // make sure the memory can expand when required

	public void init() {
		for (int i = 0; i < 2048; i++) {
			for (int j = 0; j < 16; j++) {
				memory1[i][j] = 0;
				memory2[i][j] = 0;
			}
		}
	}

	public void setMem(int indexOne, int indexTwo, int content) {
		memory1[indexOne][indexTwo] = content;
	}

	public int getMem(int indexOne, int indexTwo) {
		int result;
		result = memory1[indexOne][indexTwo];
		return result;
	}

	public void display(int addr) {
		System.out.println("memory[" + addr + "]=>" + Arrays.toString(memory1[addr]));
	}
}
