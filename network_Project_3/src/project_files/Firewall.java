package project_files;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Firewall {
	private int node; //What node is the firewall rule related to. Zero if rule == 0
	private int network; //Which switch is the firewall rule related to
	private int rule; //0 for local traffic for switch, 1 for local traffic for node
	
	public Firewall() {
		super();
	}
	
	public Firewall(int node, int network, int rule) {
		this.node = node;
		this.network = network;
		this.rule = rule;
	}
	
	//Converts a firewall rule into bytes, ready to be send through data stream
		static byte[] serialize(final Object obj) {
		    ByteArrayOutputStream bos = new ByteArrayOutputStream();

		    try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
		        out.writeObject(obj);
		        out.flush();
		        return bos.toByteArray();
		    } catch (Exception ex) {
		        throw new RuntimeException(ex);
		    }
		}
		
		//Converts a bunch of bytes into frame
		static Firewall convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
		    try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInputStream in = new ObjectInputStream(bis)) 
		    {
		        return (Firewall) in.readObject();
		    } 
		}

		public int getNode() {
			return node;
		}

		public void setNode(int node) {
			this.node = node;
		}

		public int getNetwork() {
			return network;
		}

		public void setNetwork(int network) {
			this.network = network;
		}

		public int getRule() {
			return rule;
		}

		public void setRule(int rule) {
			this.rule = rule;
		}
}
