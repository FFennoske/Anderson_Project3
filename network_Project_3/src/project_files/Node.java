package project_files;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Node implements Runnable {
	private int node_num;
	private int network_num;
	private int port;
	
	public Node (int nodeN, int networkN, int port) throws Exception{
		this.node_num = nodeN;
		this.network_num = networkN;
		this.port = port; 
	}

	@Override
	public void run() {
		InetAddress ip;
		Frame rec_f; //Receiving frame
		int len; //Length of frame
		
		try {
			ip = InetAddress.getByName("localhost");
			
			// establish the connection with server port
	        Socket s = new Socket(ip, port); 
	  
	        // obtaining input and out streams 
	        DataInputStream dis = new DataInputStream(s.getInputStream()); 
	        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
	        
	        //All frames in input file for node is sent
	        sendFrame(this.node_num, this.network_num, dos);
	  
	        //The loop handles reading incoming frames and what to do with them
	        while (true)  
	        { 
	            len = dis.readInt();
                byte [] received = new byte[len];
                dis.readFully(received);                
                rec_f = Frame.convertFromBytes(received);
                
                if (rec_f.getData().contains("CLOSE")) {
                	break;
                }
	            
	        }
	        
	        //Closes out of node
	        System.out.println("CLOSING NODE");
	        closeNode(s, dis, dos);
	        
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Reads input files, converting frames to bytes, and then sending to switch
	public void sendFrame(int nodeN, int networkN, DataOutputStream dos) throws IOException {
			String row;
			Frame frame = new Frame();
			
			//Reads input file
			try {
				BufferedReader br = new BufferedReader(new FileReader("inputfiles/node"+networkN+"_"+nodeN+".txt"));
				while ((row = br.readLine()) != null) {
					String[] line  = row.split("[_:]+");
					frame = new Frame(Integer.parseInt(line[1]), Integer.parseInt(line[0]), nodeN, networkN, (byte) (Integer.parseInt(line[1])+Integer.parseInt(line[0])+nodeN+networkN), line[2].length(), 0, line[2]);
					
					//Serializes frames into bytes before sending to switch
					byte[] data = frame.serialize(frame);
					dos.writeInt(data.length);
					dos.write(data);
					System.out.println("SENT SENT SENT");
					dos.flush();
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Closes out of node
		public void closeNode(Socket s, DataInputStream dis, DataOutputStream dos) throws IOException {
			s.close(); 
	        dis.close(); 
	        dos.close(); 
		}
}
