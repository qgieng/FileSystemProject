
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
	public void readBuffer(char[] p) {
		
	}
	public void writeBuffer(char[] p) {
		
	}
}
