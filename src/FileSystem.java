import java.io.File;
import java.io.IOException;

public class FileSystem {
	private IOSystem filesystem;
	private OpenFileTable OFT;
	private boolean init = false;
	FileSystem(){
		 this.filesystem = new IOSystem();
	}
	
	
	public void init() {
		this.OFT = new OpenFileTable();
		this.loadOFTable(0);
	}
	
	public void init(String filename) {
		this.init = true;
		//load directory into OFT.
		
		
	}
	
	
	
	public void create(String filename) {
		if(filename.length() > CONSTANTS.DIRECTORY_FILE_NAME_SIZE || filename.length() == 0) {
			System.out.println("Error File: length too long or no filename");
		}
		
		char[] cfilename = filename.toCharArray();
		//find free file descriptor
		int free_fd = find_free_fd();
		//fine free directory entry
		int free_directory_entry = find_free_directory_entry(cfilename);
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
	public void lseek(int index, 
						int pos) {
		
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
		for(int  block_i = 1; block_i < CONSTANTS.FILEDESCRIPTORS; block_i++) {
			
			this.filesystem.read_block(block_i, rd_buffer);
			
			//if block 1, then skip the directory file descriptor else start at 0
			int byte_i = (block_i == 1)? (Integer.BYTES * CONSTANTS.DESCRIPTOR_SIZE) : 0;
			
			for(; byte_i < CONSTANTS.BLOCK_SIZE; byte_i += (Integer.BYTES *CONSTANTS.DESCRIPTOR_SIZE)) {
				if(rd_buffer.unpack(byte_i) == -1) {
					free_fd = calculate_free_fd(block_i, byte_i);
					return free_fd;
				}
			}
		}
		return free_fd;
	}
	private int find_free_block() {
		return 0 ;
	}
	
	/**
	 * finds free directory entry
	 * @return
	 */
	private int find_free_directory_entry(char [] filename) {
		PackableMemory directory_block = new PackableMemory(CONSTANTS.BLOCK_SIZE);
		this.filesystem.read_block(1, directory_block);
		int directory_length =directory_block.unpack(0);
		if(directory_length == -1) {
			set_fd(0,filename.length+Integer.BYTES);
			//find free block/storage
		}
		else {
			
		}
		
		return 0;
	}
	/**
	 * this will set the length of a file descriptor to val
	 * @param file_desc
	 * @param val
	 */
	private void set_fd(int file_desc,int val) {
		PackableMemory fd_block = new PackableMemory(CONSTANTS.BLOCK_SIZE);
		int block_num = file_desc/CONSTANTS.DESCRIPTOR_SIZE+1;
		int fd_offset = 16*(file_desc - (CONSTANTS.DESCRIPTOR_SIZE * (block_num-1)));
		this.filesystem.read_block(block_num, fd_block);
		fd_block.pack(val, fd_offset);
		this.filesystem.write_block(block_num, fd_block);
	}
	/**
	 * reads the file descriptor block and gets the length of file descriptor index
	 * @param file_descriptor index
	 */
	private int get_fd_length(int file_desc) {
		PackableMemory fd_block = new PackableMemory(CONSTANTS.BLOCK_SIZE);
		int block_num = file_desc/CONSTANTS.DESCRIPTOR_SIZE+1;
		int fd_offset = 16*(file_desc - (CONSTANTS.DESCRIPTOR_SIZE * (block_num-1)));
		this.filesystem.read_block(block_num, fd_block);
		
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
		offset = (len_value/CONSTANTS.BLOCK_SIZE) * Integer.BYTES ;
		fd_block.pack(free_block_index, fd_offset+offset);
		this.filesystem.write_block(block_num,  fd_block);
	}
	
	/**
	 * get the block LDISK[i] index i from file descriptor
	 * @param file_desc
	 * @param offset
	 * @return
	 */
	private int get_block_index(int file_desc, int offset) {
		PackableMemory fd_block = new PackableMemory(CONSTANTS.BLOCK_SIZE);
		int block_num = file_desc/CONSTANTS.DESCRIPTOR_SIZE+1;
		int fd_offset = 16*(file_desc - (CONSTANTS.DESCRIPTOR_SIZE * (block_num-1)));
		this.filesystem.read_block(block_num, fd_block);
		
		return fd_block.unpack(fd_offset+offset);
	}
	
	
	private int calculate_free_fd(int block_i, int fd_loc) {
		return ((block_i-1) * CONSTANTS.DESCRIPTOR_SIZE) + (fd_loc/16);
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

	/*
	 * free_fd(block_i , fd_loc) = ((block_i -1) * 4) + (byte_i/16)
	 * given the current design  of the ldisk, will calculate the file descriptor given block number and location number(0,16,32,48);
	 * */
	
	
	public void test_get_fd_length() {
		System.out.println("Testing get_fd_length");
		int free_fd = this.find_free_fd();
		this.set_fd(free_fd, 10);
		System.out.println("setting free_fd[1] with length 10.... getting fd length of fd[1] " + this.get_fd_length(free_fd));
		
		this.set_fd(10, 100);
		System.out.println("setting fd[10] with length 100.... getting fd length of fd[10] " + this.get_fd_length(10));
		
		this.set_fd(20, 64);
		System.out.println("setting fd[20] with length 64.... getting fd length of fd[20] " + this.get_fd_length(20));
	}
	
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
	
	public static void main(String[] args) {
		FileSystem fsystem = new FileSystem();
		FileSystem fsystem1 = new FileSystem();
		FileSystem fsystem2 = new FileSystem();
		//fsystem.test_calculate_free_fd();
		//fsystem.test_find_free_fd();
		//fsystem1.test_find_free_fd_1();
		fsystem2.test_get_fd_length();
	}
	
	
	
}
