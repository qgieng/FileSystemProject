
public class CONSTANTS {
	
	public final static int LDISK_SIZE = 64;
	//16 integers
	public final static int BLOCK_SIZE = 64;
	//4 integers, 1 file, 3 block#
	public final static int DESCRIPTOR_SIZE = 4;
	public final static int FILEDESCRIPTORS = 24;
	public final static int OFT_SIZE = 4;
	public final static int DIRECTORY_FILE_NAME_SIZE = 4;	//	4 bytes
	
	public final static int FREEBLOCK = 0;
	public final static int USEDBLOCK = 1;
	public final static int FIRST_INT_OFFSET = 4;
	public final static int SECOND_INT_OFFSET = 8;
	public final static int THIRD_INT_OFFSET = 12;
	
	public final static int DIRECTORY_FILEDESCRIPTOR_INDEX = 0;
}
