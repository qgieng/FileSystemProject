
public class IOSystem {
	
	private PackableMemory[] LDISK= null;
	IOSystem(){
		this.LDISK = new PackableMemory[CONSTANTS.LDISK_SIZE];
		//bitmap
		this.LDISK[0] = new PackableMemory(CONSTANTS.BLOCK_SIZE);
		this.LDISK[0].pack(0, 0);
		this.LDISK[0].pack(0, 4);
		
		for(int i = 1; i < CONSTANTS.LDISK_SIZE; i++) {
			LDISK[i] = new PackableMemory(CONSTANTS.BLOCK_SIZE);
			
			if( i <=  CONSTANTS.FILEDESCRIPTORS) {
				for(int loc_index = 0; loc_index < (CONSTANTS.BLOCK_SIZE/Integer.BYTES) ; loc_index++) {
					this.LDISK[i].pack(-1, loc_index * Integer.BYTES);
				}
			}
			else {
				LDISK[i].pack(-1, 0);
			}
		}
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

	public void IOTest_4() {
		System.out.println("This is a test to check if all the allocated blocks are packed with correct data information");
		
		System.out.println("block[0] bitmap unpacked data: loc(0) " + this.LDISK[0].unpack(0));
		System.out.println("block[0] bitmap unpacked data: loc(4) " + this.LDISK[0].unpack(0));
		
		System.out.println("block[1] file descriptor directory: " + this.LDISK[1].unpack(0));
		
		System.out.println("block[22] free block: " + this.LDISK[22].unpack(0));
		System.out.println("block[22] free block(loc[4]): " + this.LDISK[22].unpack(4));
		System.out.println("block[23] free block: " + this.LDISK[23].unpack(0));
		System.out.println("block[23] free block(loc[4]): " + this.LDISK[23].unpack(4));
		
		System.out.println("block[24] free block: " + this.LDISK[24].unpack(0));
		System.out.println("block[24] free block(loc[4]): " + this.LDISK[24].unpack(4));
		System.out.println("block[30] free block: " + this.LDISK[30].unpack(0));
		System.out.println("block[30] free block(loc[4]): " + this.LDISK[30].unpack(4));
		System.out.println("block[63] free block: " + this.LDISK[63].unpack(0));
		System.out.println("block[63] free block(loc[4]): " + this.LDISK[63].unpack(4));
	}
	
	public static void main(String[] args) {
		IOSystem ldisk_1 = new IOSystem();
		IOSystem ldisk_4 = new IOSystem();
		ldisk_1.IOTest_1();
		//ldisk.IOTest_2();
		//ldisk.IOTest_3();
		ldisk_4.IOTest_4();
	}
}
