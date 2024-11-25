package project_files;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Frame implements Serializable {
	private int dstN; //The node it's destined for
	private int dstS; //The network/switch it's destined for
	private int srcN; //The node it originated from
	private int srcS; //The network/switch it originated from
	private byte crc; //Checksum for values of frame
	private int len; //Acts as either len of data or the ACK if 0
	private int ack; //ACK field type if it's an ACK
	private String data; //Data
	
	public Frame () {
		super();
	}
	
	public Frame (int dstN, int dstS, int srcN, int srcS, byte crc, int len, int ack, String data) {
		this.dstN = dstN;
		this.dstS = dstS;
		this.srcN = srcN;
		this.srcS = srcS;
		this.crc = crc;
		this.len = len;
		this.ack = ack;
		this.data = data;
	}
	
	//Converts a frame into bytes, ready to be send through data stream
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
	static Frame convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
	    try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInputStream in = new ObjectInputStream(bis)) 
	    {
	        return (Frame) in.readObject();
	    } 
	}

	public byte getCrc() {
		return crc;
	}

	public void setCrc(byte crc) {
		this.crc = crc;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public int getAck() {
		return ack;
	}

	public void setAck(int ack) {
		this.ack = ack;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getDstN() {
		return dstN;
	}

	public void setDstN(int dstN) {
		this.dstN = dstN;
	}

	public int getDstS() {
		return dstS;
	}

	public void setDstS(int dstS) {
		this.dstS = dstS;
	}

	public int getSrcN() {
		return srcN;
	}

	public void setSrcN(int srcN) {
		this.srcN = srcN;
	}

	public int getSrcS() {
		return srcS;
	}

	public void setSrcS(int srcS) {
		this.srcS = srcS;
	}
		
		
}
