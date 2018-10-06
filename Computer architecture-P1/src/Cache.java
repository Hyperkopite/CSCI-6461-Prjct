import java.util.Random;

public class Cache {

	private int[][][] tagStore = new int[4][2][11]; // int[setNumber][two blocks in set][tag]
	private int[][][] dataStore = new int[4][2][16]; // int[setNumber][two blocks in set][data]

	public void init() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; j++) {
				for (int k = 0; k < 11; k++) {
					tagStore[i][j][k] = 0;
				}
				for (int l = 0; l < 16; l++) {
					dataStore[i][j][l] = 0;
				}
			}
		}
	}

	// implementation of cache replacement policy, use random policy, so block is
	// random picked from 0 to 1
	public void replacementPolicy(int[] cacheInstruction, int content) {
		Random r = new Random();
		String temp = Integer.toBinaryString(content);
		int low = 0;
		int high = 2;
		int block = r.nextInt(high - low) + low;
		int tempLen = temp.length();
		int[] tempContent = new int[16];

		for (int i = 0; i < tempLen; i++) {
			tempContent[i + (16 - tempLen)] = Character.getNumericValue(temp.charAt(i));
		}
		for (int i = 0; i < (16 - tempLen); i++) {
			tempContent[i] = 0;
		}

		storeToCache(cacheInstruction, block, tempContent);
	}

	// if miss, return -1; if hit, return the block number in set.
	public int isHit(int[] cacheInstruction) {
		int result = -1;
		int tempIndex;
		int cacheTag = 0;
		int storeTag0 = 0;
		int storeTag1 = 0;

		tempIndex = calIndex(cacheInstruction);

		// calculate tag in cacheInstruction and tagStore
		for (int i = 0; i < 10; i++) {
			if (cacheInstruction[i] == 1) {
				cacheTag = cacheTag + (int) Math.pow(cacheInstruction[i] * 2, (9 - i));
			}
			if (tagStore[tempIndex][0][i + 1] == 1) {
				storeTag0 = storeTag0 + (int) Math.pow(tagStore[tempIndex][0][i + 1] * 2, (10 - i));
			}
			if (tagStore[tempIndex][1][i + 1] == 1) {
				storeTag1 = storeTag1 + (int) Math.pow(tagStore[tempIndex][1][i + 1] * 2, (10 - i));
			}
		}

		for (int i = 0; i < 2; i++) {
			// do the compare only if the valid number in tagStore is 1
			if (tagStore[tempIndex][i][0] == 1) {
				if (cacheTag == storeTag0) {
					result = 0;
					break;
				}
				if (cacheTag == storeTag1) {
					result = 1;
					break;
				}
			}
		}

		return result;
	}

	// calculate index
	public int calIndex(int[] cacheInstruction) {
		int index = 0;

		// calculate index number
		for (int i = 10; i < 12; i++) {
			if (cacheInstruction[i] == 1) {
				index = index + (int) Math.pow(cacheInstruction[i] * 2, (11 - i));
			}
		}

		return index;
	}

	// used when there is a hit
	public int fetchFromCache(int[] cacheInstruction, int block) {
		int result = 0;
		int tempIndex;
		int[] temp = new int[16];

		tempIndex = calIndex(cacheInstruction);
		for (int i = 0; i < 16; i++) {
			temp[i] = dataStore[tempIndex][block][i];
		}
		for (int i = 0; i < 16; i++) {
			if (temp[i] == 1) {
				result = result + (int) Math.pow(cacheInstruction[i] * 2, (15 - i));
			}
		}

		return result;
	}

	// if hit, store; if not, do nothing
	public void storeToCache(int[] cacheInstruction, int block, int[] content) {
		int tempIndex;

		tempIndex = calIndex(cacheInstruction);
		// store data to dataStore
		for (int i = 0; i < 16; i++) {
			dataStore[tempIndex][block][i] = content[i];
		}
		// update tagStore
		tagStore[tempIndex][block][0] = 1;
		for (int i = 1; i < 11; i++) {
			tagStore[tempIndex][block][i] = cacheInstruction[i - 1];
		}
	}

}
