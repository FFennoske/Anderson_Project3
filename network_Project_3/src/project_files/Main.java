package project_files;

public class Main {

	public static void main(String[] args) {
		
		int N = 0; // # of nodes per switch
		
		//Parses argument of # of nodes to create
		if (args[0] != null) {
			N = Integer.parseInt(args[0]);
		} else {
			N = 1;
		}

	}

}
