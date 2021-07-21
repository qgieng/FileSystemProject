
public class FileSystem {
	private IOSystem filesystem;
	FileSystem(){
		 this.filesystem = new IOSystem();
	}
	
	
	
	public void create(String filename) {
		//find free file descriptor
		int free_fd = find_free_fd();
		//fine free directory entry
		int free_directory_entry = find_free_directory_entry();
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
	/**
	 * finds free directory entry
	 * @return
	 */
	private int find_free_directory_entry() {
		return 0;
	}
	private void set_fd(int file_desc,int val) {
		PackableMemory fd_block = new PackableMemory(CONSTANTS.BLOCK_SIZE);
		int block_num = file_desc/CONSTANTS.DESCRIPTOR_SIZE+1;
		int fd_offset = 16*(file_desc - (CONSTANTS.DESCRIPTOR_SIZE * (block_num-1)));
		this.filesystem.read_block(block_num, fd_block);
		fd_block.pack(val, fd_offset);
		this.filesystem.write_block(block_num, fd_block);
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
	private int calculate_free_fd(int block_i, int fd_loc) {
		return ((block_i-1) * CONSTANTS.DESCRIPTOR_SIZE) + (fd_loc/16);
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
		//fsystem.test_calculate_free_fd();
		//fsystem.test_find_free_fd();
		//fsystem1.test_find_free_fd_1();
	}
	
	
	
}
