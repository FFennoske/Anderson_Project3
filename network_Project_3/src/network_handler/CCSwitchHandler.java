package network_handler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class CCSwitchHandler extends Thread{
	final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket soc; 
  
    // Constructor 
    public CCSwitchHandler(Socket soc, DataInputStream dis, DataOutputStream dos)  
    { 
        this.soc = soc; 
        this.dis = dis; 
        this.dos = dos; 
    }
}
