
public class IOSystem {
	
	private PackableMemory LDISK[]= null;
	IOSystem(){
		this.LDISK = new PackableMemory[CONSTANTS.LDISK_SIZE];
		for(int i = 0; i < CONSTANTS.LDISK_SIZE; i++) {
			LDISK[i] = new PackableMemory(CONSTANTS.BLOCK_SIZE);
			
			for(int c = 0; c < CONSTANTS.BLOCK_SIZE/Integer.BYTES ; c++) {
				LDISK[i].pack(-1, c*Integer.BYTES);
			}
		}
		//bitmap
		this.LDISK[0].pack(0, 0);
		this.LDISK[0].pack(0, 4);
		
		
	}
	/**
	 * This copies the logical block ldisk[i] into main memory starting at the location
		specified by the pointer p. The number of characters copied corresponds to the
		block length, B
	 * @param i
	 * @param p
	 */
	public void read_block(int block_num , char[] p) {
		
		for(int index = 0; index < CONSTANTS.BLOCK_SIZE; index++){
			p[index] =(char) this.LDISK[block_num].mem[index];
		}
	}
	
	public void read_block(int block_num, PackableMemory p) {
		for(int i = 0; i < CONSTANTS.BLOCK_SIZE; i++) {
			p.mem[i] = this.LDISK[block_num].mem[i];
		}
	}
	/**
	 * This copies the number of character corresponding to the block length, B, from
		main memory starting at the location specified by the pointer p, into the logical
		block ldisk[i]
	 * @param i
	 * @param p
	 */
	public void write_block(int block_num , char[] p ) {
		for(int index = 0;  index < CONSTANTS.BLOCK_SIZE; index++) {
			this.LDISK[block_num].mem[index] = (byte) p[index];
		}
	}
	
	public void write_block(int block_num , PackableMemory p ) {
		for(int index = 0;  index < CONSTANTS.BLOCK_SIZE; index++) {
			this.LDISK[block_num].mem[index] =  p.mem[index];
		}
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
	
	
	
	public void IOTest_1() {
		System.out.println("IOTest_1");
		char[] test1 = "TESTTEST                                                         ".toCharArray();
		char[] test1_result = new char[test1.length];
		this.write_block(23, test1 );
		System.out.println("testing write into block23");
		System.out.println( test1);
		
		System.out.println("testing reading from block23");
		this.read_block(23, test1_result);
		System.out.println(test1_result );
		
		
		
	}
	public void IOTest_2() {
		System.out.println("IOTest_2");
		char[] test1 = "1234567812345678123456781234567812345678123456781234567812345678".toCharArray();
		char[] test1_result = new char[test1.length];
		this.write_block(1, test1 );
		System.out.println("testing write into block 0, 64 chars");
		System.out.println( test1);
		
		System.out.println("testing reading from block 0, 64 chars");
		this.read_block(1, test1_result);
		System.out.println(test1_result );
		
	}
	public void IOTest_3() {
		try {
		System.out.println("IOTest_3");
		char[] test1 = "12345678123456781234567812345678123456781234567812345678123456781".toCharArray();
		char[] test1_result = new char[test1.length];
		this.write_block(1, test1 );
		System.out.println("testing write into block 0, 65 chars");
		System.out.println( test1);
		
		System.out.println("testing reading from block 0, 65 chars");
		this.read_block(1, test1_result);
		System.out.println(test1_result );
		}catch(Exception e){
			System.out.println("Array out of bound due to size of LDISK Block Size being 64, cannot write more than 64 characters into block");
		}
	}
	
	
	
	public static void main(String[] args) {
		IOSystem ldisk = new IOSystem();
		ldisk.IOTest_1();
		//ldisk.IOTest_2();
		//ldisk.IOTest_3();
	}
}
