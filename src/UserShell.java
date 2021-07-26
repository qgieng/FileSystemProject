import java.util.Scanner;
public class UserShell {
	
	//class to simulate the presentation shell/driver between user and FS
	private FileSystem fs= null;
	UserShell(){
		this.fs = new FileSystem();
	}
	
	public void start() {
		Scanner in = new Scanner(System.in);
		while(true) {
			System.out.println("Please enter an input: ");
			String user_input = in.nextLine();
			System.out.println("Printing line here: " +user_input);
			String command = user_input.substring(0,2);
			if(command.equals("cr")) {
				String filename = user_input.substring(2);
				fs.create(filename);
			}
			else if(command.equals("de")) {
				String filename = user_input.substring(2);
				fs.destroy(filename);
			}
			else if(command.equals("op")) {
				String filename = user_input.substring(2);
				fs.open(filename);
			}
			else if(command.equals("cl")) {
				String filename = user_input.substring(2);
				fs.close(filename);
			}
			else if(command.equals("rd")) {
				String[] arrInput = user_input.split(" "
														, 3);
				int index = Integer.valueOf(
											arrInput[1]);
				int count = Integer.valueOf(
											arrInput[2]);
				char[] mem_area = new char[count];
				fs.read(index, 
						mem_area, 
						count);
				System.out.println(mem_area);
			}
			else if(command.equals("wr")) {
				String[] arrInput = user_input.split(" ",
														4);
				int index = Integer.valueOf(arrInput[1]);
				char c = arrInput[2].charAt(0);
				int count = Integer.valueOf(arrInput[3]);
				fs.write(index, c, count);
			}
			else if(command.equals("sk")) {
				break;
			}
			else if(command.equals("dr")) {
				fs.directory();
			}
			else if(command.equals("in")) {
				String[] arrInput = user_input.split(" " , 3);
				if(arrInput.length == 1) {
					fs.init();
				}
				else {
					String fileName = user_input.substring(2);
					if(fileName.length() > 4) {
						System.out.println("Error: file length > 4");
						break;
					}
					else{
						fs.init(fileName);
					}
				}
				
			}
			else if(command.equals("sv")) {
				String filename = user_input.substring(2);
				break;
			}else {
				break;
			}
			
			
			if(user_input.equals("exit")) {
				System.out.println("System has exited");
				break;
			}
		}
		in.close();
		
	}
	
	public static void main(String[] args) {
		UserShell shell = new UserShell();
		shell.start();
		
	}
	
	
}
