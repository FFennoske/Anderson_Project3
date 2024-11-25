package project_files;

public class Main {

	public static void main(String[] args) throws Exception {
		
		int N = 0; // # of nodes per switch
		
		//Parses argument of # of nodes to create
		if (args[0] != null) {
			N = Integer.parseInt(args[0]);
		} else {
			N = 1;
		}
		
		CCS_Switch ccs = new CCS_Switch(7540);
		Thread ccs_thd = new Thread(ccs);
		ccs_thd.start();
		Thread.sleep(1000);
		CAS_Switch cas1 = new CAS_Switch(1, ccs.port);
		Thread cas1_thd = new Thread(cas1);
		cas1_thd.start();
		Thread.sleep(1000);
		CAS_Switch cas2 = new CAS_Switch(2, ccs.port);
		Thread cas2_thd = new Thread(cas2);
		cas2_thd.start();
		Thread.sleep(1000);
		
		
		for (int n = 1; n<=2; n++) {
			for (int i = 1; i<=N; i++) {
				Node node = new Node(i, n, n);
				Thread node_thd = new Thread(node);
				node_thd.start();
			}
		}
	}

}
