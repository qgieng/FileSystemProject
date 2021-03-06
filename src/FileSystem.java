import java.io.File;
import java.io.IOException;

public class FileSystem {
	private IOSystem filesystem;
	private OpenFileTable OFT;
	private boolean init = false;
	
	private long[] BITMAP;
	private long[] MASK;
	private long[] MASK2;
	FileSystem(){
		 this.filesystem = new IOSystem();
		 
		 this.MASK = new long[CONSTANTS.LDISK_SIZE];
		 this.MASK2 = new long[CONSTANTS.LDISK_SIZE];
		 this.BITMAP = new long[CONSTANTS.LDISK_SIZE];
		 
		 
		 
		 this.MASK[CONSTANTS.LDISK_SIZE-1]=1;
		 for(int i = CONSTANTS.LDISK_SIZE-2; i >=0; i--){
			 this.MASK[i] = this.MASK[i+1] << 1; 
		 }
		 for(int i = 0; i < CONSTANTS.LDISK_SIZE; i++) {
			 this.MASK2[i] = ~this.MASK[i];
		 }
		 
		 for(int i = 0; i < CONSTANTS.LDISK_SIZE; i++) {
			 this.BITMAP[i] = 0;
		 }
		 for(int i = 0; i <= CONSTANTS.FILEDESCRIPTORS/CONSTANTS.DESCRIPTOR_SIZE; i++) {
			this.set_bitmap(i, 1);
		 }
		 
		 
	}
	
	/**
	 * set up the OFT and load the directory by reading the block and loading it into OFT.
	 */
	public void init() {
		this.init = true;
		this.OFT = new OpenFileTable();
		//find free block and use it for directory.
		int free_directory_block_index = this.find_free_block();
		this.set_fd_freeblock(CONSTANTS.DIRECTORY_FILEDESCRIPTOR_INDEX, 
								CONSTANTS.FIRST_INT_OFFSET, 
								0, 
								free_directory_block_index);
		this.set_bitmap(free_directory_block_index, CONSTANTS.USEDBLOCK);
		this.set_fd(0, 0);
		
		//get directory block and load block into OFT buffer.
		PackableMemory directory_block = new PackableMemory(CONSTANTS.BLOCK_SIZE);
		this.filesystem.read_block(free_directory_block_index, directory_block);
		this.OFT.table[0].readBuffer(directory_block);
	}
	
	/**
	 * must read the data in the given file. loading the bitmap, OFT, directory, and etc. 
	 * @param filename
	 */
	public void init(String filename) {
		this.init = true;
		//load directory into OFT.
		
		//load bit mask
	}
	
	
	
	public void create(String filename) {
		if(filename.length() > CONSTANTS.DIRECTORY_FILE_NAME_SIZE || filename.length() == 0) {
			System.out.println("Error File: length too long or no filename");
		}
		
		char[] cfilename = filename.toCharArray();
		//find free file descriptor
		int free_fd = find_free_fd();
		//find free directory entry
		//int free_directory_entry = find_free_directory_entry(cfilename);
		int directory_length = this.get_fd_length(0);
		for(int i = 0; i < directory_length; i++) {
			
		}
		
		//fill both entries
		this.set_fd(free_fd, 0);
		
		
		
	}
	
	public void destroy(String filename) {
		
	}
	public void open(String filename) {
		
	}
	public void close(String filename) {
		
	}
	public void read(int index, 
					char[] mem_area,
					int count) {
		
	}
	
	public void write(	
						int index, 
						char mem_area, 
						int count) {
		
		
	}
	/**
	 * OFT Index. Moves position of OFT Index to pos value.
	 * note. This is will do checks such that if pos is not within opened block it will load the block.
	 * @param index
	 * @param pos
	 */
	public void lseek(int index, 
						int pos) {
		int file_descriptor_index = this.OFT.table[index].getFileDescriptorIndex();
		int file_descriptor_length = this.get_fd_length(file_descriptor_index);
		PackableMemory buf = new PackableMemory(CONSTANTS.BLOCK_SIZE);
		
		if(pos > file_descriptor_length) {
			System.out.println("Error, the position is not valid because position > file length");
			return;
		}
		
		//take current OFT file block and save it into memory.
		int current_position = this.OFT.table[index].getPosition();
		int block_offset = ((current_position/CONSTANTS.BLOCK_SIZE)+1) * Integer.BYTES;
		int block_reference =  this.get_block_index(file_descriptor_index, block_offset); 
		this.OFT.table[index].writeBuffer(buf);
		this.filesystem.write_block(block_reference, buf);
		
		//load from memory into OFT.
		// 65/64 = 1,63/64 = 0, 
		block_offset = ((pos/CONSTANTS.BLOCK_SIZE)+1) * Integer.BYTES;
		block_reference = this.get_block_index(file_descriptor_index, block_offset); 
		this.filesystem.read_block(block_reference, buf);
		this.OFT.table[index].readBuffer(buf);
		
		//set new position
		this.OFT.table[index].setPosition(pos);
		
		
	}
	/**
	 * List the names of all files and their length
	 */
	public void directory() {
		
	}
	
	private void loadOFTable(int FileDescriptorIndex) {
		PackableMemory directory_block_fd = new PackableMemory(CONSTANTS.BLOCK_SIZE);
		this.filesystem.read_block(1, directory_block_fd);
		
		//this.OFT.table[FileDescriptorIndex].readBuffer();
		
	}
	
	/**
	 * find free file descriptor
	 * @return
	 */
	private int find_free_fd() {
		int free_fd = -1;
		
		PackableMemory rd_buffer = new PackableMemory(CONSTANTS.BLOCK_SIZE);
		//blocks1 - block 6 should be read....
		for(int  block_i = 1; block_i <= CONSTANTS.FILEDESCRIPTORS/CONSTANTS.DESCRIPTOR_SIZE; block_i++) {
			
			this.filesystem.read_block(block_i, rd_buffer);
			//if block 1, then skip the directory file descriptor else start at 0
			int byte_i = (block_i == 1)? (Integer.BYTES * CONSTANTS.DESCRIPTOR_SIZE) : 0;
			
			for(; byte_i < CONSTANTS.BLOCK_SIZE; byte_i += (Integer.BYTES *CONSTANTS.DESCRIPTOR_SIZE)) {
				if(rd_buffer.unpack(byte_i) == -1) {
					free_fd = calculate_free_fd(block_i, 
												byte_i);
					return free_fd;
				}
			}
		}
		return free_fd;
	}
	/**
	 * Using the bitmap, finds a free unallocated block.
	 * @return
	 */
	private int find_free_block() {
		int free_block_i=-1;
		long bm = condense_bitmap();
		for(int mask_i = 0; mask_i < CONSTANTS.BLOCK_SIZE;mask_i++) {
			long mask_temp = (bm & this.MASK[mask_i]);
			if( mask_temp == 0) {
				free_block_i = mask_i;
				return mask_i;
			}
		}
		
		return free_block_i;
	}
	
	/**
	 * finds free directory entry
	 * @return
	 */
	private int find_free_directory_entry(char [] filename) {
		
		
		return 0;
	}
	
	/**
	 * sets or resets bitmap[index]. 
	 * @param index
	 * @param value
	 */
	private void set_bitmap(int index, int value) {
		if(value == 0) {
			this.BITMAP[index] = this.BITMAP[index] & this.MASK2[index];
		}
		else if(value == 1) {
			//System.out.println("before: "  + Long.toBinaryString(this.BITMAP[index]));
			this.BITMAP[index] = this.BITMAP[index] | this.MASK[index];
			//System.out.println("after: " + Long.toBinaryString(this.BITMAP[index]));
		}
	}
	/**
	 * condense bitmap data array into a bitmap long
	 * @return bitmap long
	 */
	private long condense_bitmap() {
		long bm = 0;
		
		for(long mask : this.BITMAP) {
			bm = bm | mask;
		}
		//System.out.println(Long.toBinaryString(bm));
		return bm;
	}
	
	/**
	 * set the length of a file  to value
	 * @param file_desc
	 * @param val
	 */
	private void set_fd(int file_desc_index ,int val) {
		PackableMemory fd_block = new PackableMemory(CONSTANTS.BLOCK_SIZE);
		int block_num = file_desc_index/CONSTANTS.DESCRIPTOR_SIZE+1;
		int fd_offset = 16*(file_desc_index - (CONSTANTS.DESCRIPTOR_SIZE * (block_num-1)));
		this.filesystem.read_block(block_num, 
											fd_block);
		fd_block.pack(val, 
						fd_offset);
		this.filesystem.write_block(block_num, 
											fd_block);
	}
	/**
	 * reads the file descriptor block and gets the length of file descriptor index
	 * @param file_descriptor index
	 */
	private int get_fd_length(int file_desc) {
		PackableMemory fd_block = new PackableMemory(CONSTANTS.BLOCK_SIZE);
		int block_num = file_desc/CONSTANTS.DESCRIPTOR_SIZE+1;
		int fd_offset = 16*(file_desc - (CONSTANTS.DESCRIPTOR_SIZE * (block_num-1)));
		this.filesystem.read_block(block_num, 
											fd_block);
		return fd_block.unpack(fd_offset);
	}
	
	/**
	 * sets a file descriptor reference to free block/page
	 * @param file_desc
	 * @param offset
	 */
	private void set_fd_freeblock(int file_desc, int offset, int len_value, int free_block_index) {
		PackableMemory fd_block = new PackableMemory(CONSTANTS.BLOCK_SIZE);
		int block_num = file_desc/CONSTANTS.DESCRIPTOR_SIZE+1;
		int fd_offset = 16*(file_desc - (CONSTANTS.DESCRIPTOR_SIZE * (block_num-1)));
		this.filesystem.read_block(block_num, fd_block);
		//logic
		//offset = (len_value/CONSTANTS.BLOCK_SIZE) * Integer.BYTES ;
		fd_block.pack(free_block_index, fd_offset+offset);
		this.filesystem.write_block(block_num,  fd_block);
	}
	
	/**
	 * get the block LDISK[i] (index i) from file descriptor
	 * note: offset should be multiples of 4(4, 8, 12) for how file descriptors are designed.
	 * note: offset should not be 0 since this value represents the length.
	 * @param file_desc
	 * @param offset
	 * @return
	 */
	private int get_block_index(int file_desc, int offset) {
		if(offset == 0) {
			System.out.println("Error: function should have offset not be 0");
			return -1;
		}
		PackableMemory fd_block = new PackableMemory(CONSTANTS.BLOCK_SIZE);
		int block_num = file_desc/CONSTANTS.DESCRIPTOR_SIZE+1;
		int fd_offset = 16*(file_desc - (CONSTANTS.DESCRIPTOR_SIZE * (block_num-1)));
		this.filesystem.read_block(block_num, 
											fd_block);
		
		return fd_block.unpack(fd_offset+offset);
	}
	
	
	private int calculate_free_fd(int block_i, int fd_loc) {
		return ((block_i-1) * CONSTANTS.DESCRIPTOR_SIZE) + (fd_loc/16);
	}
	
	/**
	 * test set_bitmap...
	 */
	public void test_set_bitmap() {
		System.out.println("Testing setbitmap");
		int free_block = this.find_free_block();
		System.out.println("Free block initial iteration: Expected 7 "  + free_block);
		this.set_bitmap(free_block, 1);
		free_block = this.find_free_block();
		System.out.println("Free block after setting free block7: Expected 8 result:"  + free_block);
		this.set_bitmap(free_block, 1);
		free_block = this.find_free_block();
		System.out.println("Free block after setting free block 8: Expected 9  result:"  + free_block);
	}
	
	/**
	 * test set bitmap to 0 and set 1...test find free_block
	 */
	public void test_set_bitmap2() {
		System.out.println("Testing setbitmap 2");
		int free_block = this.find_free_block();
		System.out.println("Free block initial iteration: Expected 7 "  + free_block);
		this.set_bitmap(free_block, 1);
		free_block = this.find_free_block();
		System.out.println("Free block after setting free block 7: Expected 8 result: "  + free_block);
		this.set_bitmap(free_block, 1);
		free_block = this.find_free_block();
		System.out.println("Free block after setting free block 8: Expected 9  result: "  + free_block);
		this.set_bitmap(free_block, 1);
		
		System.out.println("Free block 7 after setting blocks 7,8,9");
		this.set_bitmap(7, 0);
		
		free_block = this.find_free_block();
		System.out.println("Free block after freeing block 7.... expected 7 result:  "  + free_block);
		this.set_bitmap(free_block, 1);
		free_block = this.find_free_block();
		System.out.println("Free block after freeing block 7.... expected 10 result:  "  + free_block);
	}
	
	
	/**
	 * test find_free_fd and set_fd
	 */
	public void test_find_free_fd() {
		System.out.println("Test 0 to test finding free_fd given data");
		int free_fd = -1;
		free_fd=this.find_free_fd();
		System.out.println("free fd upon initial system : " + free_fd + " result: " + (free_fd == 1));
		this.set_fd(free_fd, 0);
		free_fd =this.find_free_fd();
		System.out.println("free fd after setting first free fd : " + free_fd + " result: " + (free_fd == 2));
		this.set_fd(free_fd, 0);
		this.set_fd(3, 0);
		this.set_fd(4, 0);
		this.set_fd(5, 0);
		free_fd =this.find_free_fd();
		System.out.println("free fd after setting free_fd,3,4,5 expected[6]: " + free_fd + " result: " + (free_fd == 6));
		
	}
	/**
	 * test find_free_fd and set_fd
	 */
	public void test_find_free_fd_1() {
		System.out.println("Test 1 to test finding free_fd given data");
		int free_fd = -1;
		this.set_fd(1, 0);
		this.set_fd(2, 0);
		free_fd=this.find_free_fd();
		System.out.println("free fd upon initial system(setting 1,2) expected [3]: " + free_fd + " result: " + (free_fd == 3));
		this.set_fd(free_fd, 0);
		free_fd =this.find_free_fd();
		System.out.println("free fd after setting first free fd expected[4] : " + free_fd + " result: " + (free_fd == 4));
	
		this.set_fd(free_fd, 0);
		this.set_fd(5, 0);
		this.set_fd(6, 0);
		
		free_fd=this.find_free_fd();
		this.set_fd(free_fd, 0);;
		System.out.println("free fd upon after setting 4,5,6 : " + free_fd  + " result: " + (free_fd == 7));
		free_fd=this.find_free_fd();
		this.set_fd(free_fd, 0);;
		System.out.println("free fd upon after setting free_fd expected[8]: " + free_fd + " result: " + (free_fd == 8));
		free_fd=this.find_free_fd();
		this.set_fd(free_fd, 0);;
		System.out.println("free fd upon after setting free_fd expected[9] : " + free_fd  + " result: " + (free_fd == 9));
	}


	public void test_find_free_fd_2() {
		System.out.println("testing find free fd where there is no free fd");
		
		for(int i = 1; i < CONSTANTS.FILEDESCRIPTORS; i++) {
			this.set_fd(i, 1);
		}
		int free_fd=this.find_free_fd();
		System.out.println("free fd after allocating all fds [should be -1] :" + free_fd);
	}
	
	public void test_get_fd_length() {
		System.out.println("Testing get_fd_length");
		int free_fd = this.find_free_fd();
		this.set_fd(free_fd, 10);
		System.out.println("setting free_fd[1] with length 10.... getting fd length of fd[1] " + this.get_fd_length(free_fd));
		
		this.set_fd(10, 100);
		System.out.println("setting fd[10] with length 100.... getting fd length of fd[10] " + this.get_fd_length(10));
		
		this.set_fd(20, 64);
		System.out.println("setting fd[20] with length 64.... getting fd length of fd[20] " + this.get_fd_length(20));
		this.set_fd(24, 4);
		System.out.println("setting fd[24] with length 4.... getting fd length of fd[24] " + this.get_fd_length(24));
	}
	
	public void test_get_set_block_index() {
		System.out.println("testing set_fd_freeblock/ get_block_index() functions");
		this.set_fd_freeblock(1, 4, 0, 8);
		System.out.println("this.set_fd_freeblock(1, 4, 0, 8); should get back 8 when getting block index(1,4) : " + this.get_block_index(1, 4));
		this.set_fd_freeblock(1, 8, 0, 10);
		System.out.println("this.set_fd_freeblock(1, 8, 0, 10); should get back 10 when getting block index(1,4) : " + this.get_block_index(1, 8));
		this.set_fd_freeblock(1, 12, 0, 11);
		System.out.println("this.set_fd_freeblock(1, 12, 0, 11); should get back 11 when getting block index(1,4) : " + this.get_block_index(1, 12));
	
		this.set_fd_freeblock(23, 4, 0, 18);
		System.out.println("this.set_fd_freeblock(23, 4, 0, 18); should get back 18 when getting block index(1,4) : " + this.get_block_index(23, 4));
		this.set_fd_freeblock(23, 8, 0, 19);
		System.out.println("this.set_fd_freeblock(23, 8, 0, 19); should get back 19 when getting block index(1,4) : " + this.get_block_index(23, 8));
		this.set_fd_freeblock(23, 12, 0, 20);
		System.out.println("this.set_fd_freeblock(23, 12, 0, 20); should get back 20 when getting block index(1,4) : " + this.get_block_index(23, 12));
	}
	/*
	 * free_fd(block_i , fd_loc) = ((block_i -1) * 4) + (byte_i/16)
	 * given the current design  of the ldisk, will calculate the file descriptor given block number and location number(0,16,32,48);
	 * */
	public void test_calculate_free_fd() {
		System.out.println("testing function that calculates free file descriptor");
		
		System.out.println("free_fd(1,16) = "  + calculate_free_fd(1,16) + "  expected: free_fd num 1");
		System.out.println("free_fd(1,32) = "  + calculate_free_fd(1,32) + "  expected: free_fd num 2");
		System.out.println("free_fd(1,48) = "  + calculate_free_fd(1,32+16) + "  expected: free_fd num 3");
		
		System.out.println("free_fd(2,0) = "  + calculate_free_fd(2,0) + "  expected: free_fd num 4");
		System.out.println("free_fd(2,16) = "  + calculate_free_fd(2,16) + "  expected: free_fd num 5");
		System.out.println("free_fd(2,32) = "  + calculate_free_fd(2,32) + "  expected: free_fd num 6");
		System.out.println("free_fd(2,48) = "  + calculate_free_fd(2,48) + "  expected: free_fd num 7");
		
		System.out.println("free_fd(3,0) = "  + calculate_free_fd(3,0) + "  expected: free_fd num 8");
		System.out.println("free_fd(3,16) = "  + calculate_free_fd(3,16) + "  expected: free_fd num 9");
		System.out.println("free_fd(3,32) = "  + calculate_free_fd(3,32) + "  expected: free_fd num 10");
		System.out.println("free_fd(3,48) = "  + calculate_free_fd(3,48) + "  expected: free_fd num 11");
		
		System.out.println("free_fd(4,0) = "   + calculate_free_fd(4,0) +  "  expected: free_fd num 12");
		System.out.println("free_fd(4,16) = "  + calculate_free_fd(4,16) + "  expected: free_fd num 13");
		System.out.println("free_fd(4,32) = "  + calculate_free_fd(4,32) + "  expected: free_fd num 14");
		System.out.println("free_fd(4,48) = "  + calculate_free_fd(4,48) + "  expected: free_fd num 15");
		
		System.out.println("free_fd(6,0) = "   + calculate_free_fd(6,0) +  "  expected: free_fd num 20");
		System.out.println("free_fd(6,16) = "  + calculate_free_fd(6,16) + "  expected: free_fd num 21");
		System.out.println("free_fd(6,32) = "  + calculate_free_fd(6,32) + "  expected: free_fd num 22");
		System.out.println("free_fd(6,48) = "  + calculate_free_fd(6,48) + "  expected: free_fd num 23");
	}
	
	
	public void test_find_free_block() {
		
	}
	
	public static void main(String[] args) {
		FileSystem fsystem = new FileSystem();
		FileSystem fsystem1 = new FileSystem();
		FileSystem fsystem2 = new FileSystem();
		//fsystem.test_calculate_free_fd();
		//fsystem.test_find_free_fd();
		//fsystem1.test_find_free_fd_1();
		//fsystem1.test_find_free_fd_2();
		//fsystem2.test_get_fd_length();
		//fsystem1.test_get_set_block_index();
		fsystem.test_set_bitmap2();
	}
	
	
	
}
