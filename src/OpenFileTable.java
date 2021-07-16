
public class OpenFileTable {
	//directory + 3 other open files;
	public OpenFileEntry[] table = new OpenFileEntry[CONSTANTS.OFT_SIZE];
	OpenFileTable(){
		for(int i = 0; i < CONSTANTS.OFT_SIZE; i++) {
			table[i] = new OpenFileEntry(-1, -1 );
		}
		
		//set up for directory
		table[0].setPosition(0);
		//directory FD = 0 / LDISK=1
		table[0].setFileDescriptorIndex(0);
	}
	
}
