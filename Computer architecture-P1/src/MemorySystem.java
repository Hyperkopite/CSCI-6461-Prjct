public class MemorySystem {

	private int[] cacheInstruction = new int[14];
	private MainMemory memory = new MainMemory();
	private Cache cache = new Cache();

	public void init() {
		memory.init();
		cache.init();
	}

	public void setMemory(int indexOne, int indexTwo, int content) {
		memory.setMem(indexOne, indexTwo, content);
	}

	public int getMemory(int indexOne, int indexTwo) {
		int result;
		result = memory.getMem(indexOne, indexTwo);
		return result;
	}

	public void instructionTrans(int addr) {
		String address = Integer.toBinaryString(addr);
		int addrLen = address.length();

		for (int i = 0; i < addrLen; i++) {
			cacheInstruction[i] = Character.getNumericValue(address.charAt(i));
		}
		for (int i = addrLen; i < 14; i++) {
			cacheInstruction[i] = 0;
		}

	}

	// used when cache miss
	public int fetchFromMem(int addr) {
		int[] temp = new int[16];
		int tempResult = 0;

		for (int i = 0; i < 16; i++) {
			temp[i] = getMemory(addr, i);
		}
		for (int i = 0; i < 16; i++) {
			if (temp[i] == 1) {
				tempResult = tempResult + (int) Math.pow(temp[i] * 2, (15 - i));
			}
		}

		return tempResult;
	}

	public void storeToMem(int addr, int[] mbr) {
		for (int i = 0; i < 16; i++) {
			setMemory(addr, i, mbr[i]);
		}
	}

	// read from or write to address and return in int type; if is read, argument
	// mbr means nothing.
	public int execute(int addr, int isRead, int[] mbr) {
		int content = 0;
		int temp;

		instructionTrans(addr);
		temp = cache.isHit(cacheInstruction);

		if (isRead == 1) {
			// if cache hit, fetch from cache; if miss, fetch from memory and replace cache
			if (temp != -1) {
				content = cache.fetchFromCache(cacheInstruction, temp);
			} else {
				content = fetchFromMem(addr);
				cache.replacementPolicy(cacheInstruction, content);
			}
		} else {
			// if cache hit, store to cache and the main memory; if miss, store to maim
			// memory only.
			if (temp != -1) {
				cache.storeToCache(cacheInstruction, temp, mbr);
				storeToMem(addr, mbr);
			} else {
				storeToMem(addr, mbr);
			}
		}

		return content;
	}

	// display content in Main Memory
	public void displayMainMem(int addr) {
		memory.display(addr);
	}
}
