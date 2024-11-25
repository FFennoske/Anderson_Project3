package project_files;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CCS_Switch implements Runnable {

	int port;
	
	static protected Map <Integer, Socket> networkTable = new HashMap <Integer, Socket> (); //Lists all switches and corresponding sockets in hashmap
	static protected List<Frame> GlobalFrameBuffer =  Collections.synchronizedList(new ArrayList<>()); //Stores all frames to be sent out
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
