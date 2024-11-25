package project_files;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
	
	public CCS_Switch(int port) {
		this.port = port;
	}
	
	@Override
	public void run() {
		ServerSocket ss = null;
		
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Switch is now active at - " + ss);
		
		while (true) { 
            try { 
                // Socket object to receive incoming node requests 
                Socket soc = ss.accept(); 
                  
                System.out.println("A new node is connected : " + soc); 
                  
                // Obtaining input and out streams 
                DataInputStream dis = new DataInputStream(soc.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(soc.getOutputStream());        
  
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
  	public static void forwardFrame(int networkN, Map <Integer, Socket> switchingTable, List<Frame> GlobalFrameBuffer, Socket globalSocket) throws IOException, InterruptedException, ClassNotFoundException {
  		Socket soc; //Socket that is saved from switchingTable
  		int len; // length of data

  		// Ensures GlobalFrameBuffer & switchingTable is accessed by one thread at a time
  		synchronized(GlobalFrameBuffer) {
  			synchronized(switchingTable) {
			/**
			 * THE SMART METHOD
			 * If the switching table matches to a network, it saves the socket onto a temp socket variable. 
			 * The corresponding frame is found in the GFB and is then serialized and sent out. The frame is then removed from the GFB.
			 */
  				if (switchingTable.get(networkN) != null) { //Node found in ST
  					soc = switchingTable.get(networkN);
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
