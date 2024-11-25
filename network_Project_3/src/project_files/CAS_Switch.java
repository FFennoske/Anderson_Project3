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

public class CAS_Switch implements Runnable {

	int port;
	
	static protected Map <Integer, Socket> switchingTable = new HashMap <Integer, Socket> (); //Lists all nodes and corresponding sockets in hashmap
	static protected List<Frame> GlobalFrameBuffer =  Collections.synchronizedList(new ArrayList<>()); //Stores all frames to be sent out
	
	public CAS_Switch (int port) {
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
                Thread t = new CASwitchHandler(soc, dis, dos); 
                t.start();    
            }
            catch (Exception e){ 
                e.printStackTrace(); 
            }
            
		}
	}
}
