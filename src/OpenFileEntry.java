
public class OpenFileEntry {
	private int pos = 0;
	private int FileDescriptorIndex = 0;
	public PackableMemory buf =  null;
	
	OpenFileEntry(int pos, int index){
		this.pos = pos;
		this.FileDescriptorIndex = index;
		this.buf = new PackableMemory(CONSTANTS.BLOCK_SIZE);
	}
	
	public void setPosition(int val) {
		this.pos = val;
	}
	public void setFileDescriptorIndex(int val) {
		this.FileDescriptorIndex = val;
	}
	/**
	 * returns the current position of the opened file entry.
	 * @return
	 */
	public int getPosition() {
		return this.pos;
	}
	/**
	 * returns the file descriptor index which file entry reference to.
	 * @return
	 */
	public int getFileDescriptorIndex() {
		return this.FileDescriptorIndex;
	}
	
	/**
	 * set data into buffer
	 * Load LDISK data into buffer
	 * @param p
	 */
	public void readBuffer(byte[] p) {
		for(int i = 0; i < CONSTANTS.BLOCK_SIZE; i++) {
			this.buf.mem[i] = p[i];
		}
	}
	/**
	 * set data into buffer
	 * Load LDISK data into buffer
	 * @param p
	 */
	public void readBuffer(PackableMemory p) {
		for(int i = 0; i < CONSTANTS.BLOCK_SIZE; i++) {
			this.buf.mem[i] = p.mem[i];
		}
	}
	/**
	 * write buffer data into LDISK
	 * @param p
	 */
	public void writeBuffer(PackableMemory p) {
		for(int i = 0; i < CONSTANTS.BLOCK_SIZE; i++) {
			p.mem[i] = this.buf.mem[i];
		}
	}
}
