import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.swing.JOptionPane;

public class UDPServerToSendIP {
	
	private DatagramSocket serverSocket=null;
	private ServerWindow referenceToServerWindow=null;
	
	public UDPServerToSendIP(ServerWindow referenceToServerWindow){
		this.referenceToServerWindow=referenceToServerWindow;
	}

	public void startUDPServer(){
		try {
			serverSocket=new DatagramSocket(1111,InetAddress.getByName("0.0.0.0"));
			serverSocket.setBroadcast(true);
		} catch (SocketException | UnknownHostException e) {
			//server failed to initialize, restart the application
			JOptionPane.showMessageDialog(referenceToServerWindow,"FATAL ERROR : Failed to start the server, please start the application again."
					+ "\n"+e.toString(),"ERROR",JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		byte bufferToRecieve[]=new byte[99999];
		DatagramPacket recievedPacket=new DatagramPacket(bufferToRecieve,bufferToRecieve.length);

		while(true){
			try {
				serverSocket.receive(recievedPacket);
				byte recievedBytes[]=recievedPacket.getData();
				String recievedString=new String(recievedBytes,0,recievedBytes.length);
				recievedString=recievedString.trim();
				if(recievedString.equals("IAMTHECLIENT")){
					DatagramPacket packetToSend = new DatagramPacket("IAMTHESERVER".getBytes() 
							, "IAMTHESERVER".getBytes().length , recievedPacket.getAddress()
							, recievedPacket.getPort());
					serverSocket.send(packetToSend);
				}
			} catch (IOException e) {
			
			}
		}

	}

}
