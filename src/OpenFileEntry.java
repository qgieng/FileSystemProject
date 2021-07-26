
public class OpenFileEntry {
	private int pos = 0;
	private int FileDescriptorIndex = 0;
	private PackableMemory buf =  null;
	
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
	 * Load LDISK data into buffer
	 * @param p
	 */
	public void readBuffer(byte[] p) {
		for(int i = 0; i < CONSTANTS.BLOCK_SIZE; i++) {
			p[i] =  this.buf.mem[i];
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
