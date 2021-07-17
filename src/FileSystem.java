
public class FileSystem {
	private IOSystem fs;
	FileSystem(){
		IOSystem fs = new IOSystem();
	}
	
	
	
	public void create(String filename) {
		//find free file descriptor
		int free_fd = find_free_fd();
		//fine free directory entry
		
		//fill both entries
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
	
	private int find_free_fd() {
		int free_fd = -1;
		
		
		return free_fd;
	}
	
	
}
