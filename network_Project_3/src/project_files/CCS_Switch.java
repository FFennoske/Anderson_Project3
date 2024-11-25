package project_files;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import network_handler.CASwitchHandler;
import network_handler.CCSwitchHandler;

public class CCS_Switch implements Runnable {

	int port;
	
	static protected Map <Integer, Socket> networkTable = new HashMap <Integer, Socket> (); //Lists all switches and corresponding sockets in hashmap
	static protected List<Frame> GlobalFrameBuffer =  Collections.synchronizedList(new ArrayList<>()); //Stores all frames to be sent out
	static protected List<Firewall> FirewallRules = Collections.synchronizedList(new ArrayList<>()); //Stores all rules for itself to execute and send
	
	
	public CCS_Switch(int port) {
		this.port = port;
	}
	
	@Override
	public void run() {
		ServerSocket ss = null;
		int i = 1;
		
		try {
			ss = new ServerSocket(port);
			getRules();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("CCS Switch is now active at - " + ss);
		
		while (true) { 
            try { 
                // Socket object to receive incoming node requests 
                Socket soc = ss.accept(); 
                  
                System.out.println("A new switch has connected to the CCS : " + soc); 
                  
                // Obtaining input and out streams 
                DataInputStream dis = new DataInputStream(soc.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(soc.getOutputStream());       
                
                networkTable.put(i, soc);
                System.out.println(Arrays.asList(FirewallRules));
                forwardRules(i, networkTable, FirewallRules);
                //System.out.println(Arrays.asList(networkTable));
                i++;
  
                // Create a new thread object and starting it
                Thread t = new CCSwitchHandler(soc, dis, dos); 
                t.start();    
            }
            catch (Exception e){ 
                e.printStackTrace(); 
            }
            
		}	
	}
	
	//Forwards frame to a node depending on a network
  	public static void forwardFrame(int networkN, Map <Integer, Socket> networkTable, List<Frame> GlobalFrameBuffer, Socket globalSocket) throws IOException, InterruptedException, ClassNotFoundException {
  		Socket soc; //Socket that is saved from switchingTable
  		int len; // length of data

  		// Ensures GlobalFrameBuffer & networkTable is accessed by one thread at a time
  		synchronized(GlobalFrameBuffer) {
  			synchronized(networkTable) {
			/**
			 * THE SMART METHOD
			 * If the switching table matches to a network, it saves the socket onto a temp socket variable. 
			 * The corresponding frame is found in the GFB and is then serialized and sent out. The frame is then removed from the GFB.
			 */
  				if (networkTable.get(networkN) != null) { //Network found in NT
  					soc = networkTable.get(networkN);
  					for (Frame f : GlobalFrameBuffer) {
  						if (f.getDstS() == networkN) {
  							DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
  							byte[] data = f.serialize(f);
  							dos.writeInt(data.length);
  							dos.write(data);
  							dos.flush();
  							System.out.println("DATA SENT");
  							CCS_Switch.GlobalFrameBuffer.remove(f);
  			                break;
  						}
  					}	
  					
  				}
  				/**
  				 * THE FLOODING METHOD
  				 * If the networking table does not match to any, it cycles through the whole GFB before sending it to all sockets.
  				 */
  				else {
  					for (Frame f : GlobalFrameBuffer) {
  						DataOutputStream dos = new DataOutputStream(globalSocket.getOutputStream());
  						byte[] data = f.serialize(f);
  						dos.writeInt(data.length);
  						dos.write(data);
  						dos.flush();
  						System.out.println("FLOODED");
  					}
  				}
  			}
  		}
  	}
  	
  	public static void forwardRules(int networkN, Map <Integer, Socket> networkTable, List<Firewall> firewallSend) throws IOException, InterruptedException, ClassNotFoundException {
  		Socket soc; //Socket that is saved from networkTable
  		
  		synchronized(networkTable) {
  				if (networkTable.get(networkN) != null) { //Network found in NT
  					soc = networkTable.get(networkN);
  					for (Firewall r : firewallSend) {
  						if (r.getNetwork() == networkN) {
  							DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
  							byte[] data = r.serialize(r);
  							dos.writeInt(data.length);
  							dos.write(data);
  							dos.flush();
  							System.out.println("RULES SENT TO SWITCH #"+networkN);
  						}
  					}	
  				}
  		}
  	}
  	
  	public void getRules() throws IOException {
		String row;
		int condition, node;
		Firewall rule = new Firewall();
		
		//Reads input file
		try {
			BufferedReader br = new BufferedReader(new FileReader("firewall/firewall.txt"));
			while ((row = br.readLine()) != null) {
				String[] line  = row.split("[_:]+");
				if (line[1].equals("#")) {
					condition = 0;
					node = 0;
				} else {
					condition = 1;
					node = Integer.parseInt(line[1]);
				}
				rule = new Firewall(node, Integer.parseInt(line[0]), condition);
				FirewallRules.add(rule);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
  	
  	
}
