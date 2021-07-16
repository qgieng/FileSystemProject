
public class IOSystem {
	
	private PackableMemory LDISK[];
	IOSystem(){
		this.LDISK = new PackableMemory[CONSTANTS.LDISK_SIZE];
		for(int i = 0; i < CONSTANTS.LDISK_SIZE; i++) {
			LDISK[i] = new PackableMemory(CONSTANTS.BLOCK_SIZE);
		}
		
	}
	
	public void read_block(int i , char[] p) {
		
	}
	
	public void write_block(int i , char[] p ) {
		
	}
	/**
	 * saves the Ldisk memory into a a physical block which can be restored at a later time
	 */
	public void save() {
		
	}
	/**
	 * restores Ldisk memory into memory.
	 */
	public void restore() {
		
	}
}
