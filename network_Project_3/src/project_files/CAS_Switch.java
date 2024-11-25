package project_files;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import network_handler.CASwitchHandler;

public class CAS_Switch implements Runnable {

	int port;
	int ccsPort;
	
	static protected Map <Integer, Socket> switchingTable = new HashMap <Integer, Socket> (); //Lists all nodes and corresponding sockets in hashmap
	static protected List<Frame> GlobalFrameBuffer =  Collections.synchronizedList(new ArrayList<>()); //Stores all frames to be sent out
	static protected List<Firewall> FirewallRules = Collections.synchronizedList(new ArrayList<>()); //Stores all rules for itself to execute
	
	public CAS_Switch (int port, int ccsPort) {
		this.port = port;
		this.ccsPort = ccsPort;
	}
	
	@Override
	public void run() {
		ServerSocket ss = null;
		InetAddress ip;
		
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("A CAS Switch is now active at - " + ss);
		
		try {
			ip = InetAddress.getByName("localhost");
			// establish the connection with server port
	        Socket s = new Socket(ip, ccsPort);
	        
	        while (true) { 
	            try { 
	                // Socket object to receive incoming node requests 
	                Socket soc = ss.accept(); 
	                  
	                System.out.println("A new node is connected : " + soc); 
	                  
	                // Obtaining input and out streams 
	                DataInputStream dis = new DataInputStream(soc.getInputStream()); 
	                DataOutputStream dos = new DataOutputStream(soc.getOutputStream());        
	  
	                // Create a new thread object and starting it
	                Thread t = new CASwitchHandler(soc, dis, dos); 
	                t.start();    
	            }
	            catch (Exception e){ 
	                e.printStackTrace(); 
	            }
	            
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Forwards frame to a node depending on a node check
  	public static void forwardFrame(int nodeN, Map <Integer, Socket> switchingTable, List<Frame> GlobalFrameBuffer, Socket globalSocket) throws IOException, InterruptedException, ClassNotFoundException {
  		Socket soc; //Socket that is saved from switchingTable
  		int len; // length of data

  		// Ensures GlobalFrameBuffer & switchingTable is accessed by one thread at a time
  		synchronized(GlobalFrameBuffer) {
  			synchronized(switchingTable) {
			/**
			 * THE SMART METHOD
			 * If the switching table matches to a node, it saves the socket onto a temp socket variable. 
			 * The corresponding frame is found in the GFB and is then serialized and sent out. The frame is then removed from the GFB.
			 */
  				if (switchingTable.get(nodeN) != null) { //Node found in ST
  					soc = switchingTable.get(nodeN);
  					for (Frame f : GlobalFrameBuffer) {
  						if (f.getDstN() == nodeN) {
  							DataOutputStream dos = new DataOutputStream(soc.getOutputStream());
  							byte[] data = f.serialize(f);
  							dos.writeInt(data.length);
  							dos.write(data);
  							dos.flush();
  							System.out.println("DATA SENT");
  							CAS_Switch.GlobalFrameBuffer.remove(f);
  			                break;
  						}
  					}	
  					
  				}
  				/**
  				 * THE FLOODING METHOD
  				 * If the switching table does not match to any, it cycles through the whole GFB before sending it to all sockets.
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
}
