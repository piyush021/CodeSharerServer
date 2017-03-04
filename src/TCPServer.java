import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JOptionPane;


public class TCPServer {
	
	private ServerSocket serverSocket=null;
	private Socket socket=null;
	public LinkedList linkedList;
	
	private ServerWindow referenceToServerWindow=null;
	
	public TCPServer(ServerWindow referenceToServerWindow){
		this.referenceToServerWindow=referenceToServerWindow;
	}
	
	public void startTCPServer(){
		try {
			serverSocket = new ServerSocket(2222);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(referenceToServerWindow,"FATAL ERROR : Failed to start the server, please start the application again.","ERROR",JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		linkedList=new LinkedList();
		
		while(true){
			try {
				socket=serverSocket.accept();
			} catch (IOException e) {
		
			}
		    linkedList.add(socket);
		    new SeperateThreadForEveryClient(socket).start();
		}
	}
	
	//Inner class can access all attributes of its parent class directly 
	private class SeperateThreadForEveryClient extends Thread{
		Socket socketReferenceToCurrentClient=null;
		
		public SeperateThreadForEveryClient(Socket socketReferenceToCurrentClient){
			this.socketReferenceToCurrentClient=socketReferenceToCurrentClient;	
		}
		
		@Override
		public void run() {
			String message="";
			DataInputStream dataInputStream=null;
			try {
				dataInputStream = new DataInputStream(socketReferenceToCurrentClient.getInputStream());
			} catch (IOException e1) {
				return;
			}
			String userName="";
			try {
				//first message sent by client is userName. 
				userName=dataInputStream.readUTF();
				linkedList.setNameOfClient(socketReferenceToCurrentClient,userName);
			} catch (IOException e) {

			}
			
			//sending names for active users as first message
			linkedList.sendNamesOfActiveClientsToParticularClient(socketReferenceToCurrentClient);
			
			
			linkedList.sendNotificationToAllClientsExcept(socketReferenceToCurrentClient,userName +" joined");
			linkedList.sendCommandToAllClientsExcept(socketReferenceToCurrentClient,"INSERT@"+userName);
			
			while(true){
				try {
					message=dataInputStream.readUTF();					
				}catch(SocketException|NullPointerException e){
					System.err.println(e);
					linkedList.delete(socketReferenceToCurrentClient);
					linkedList.sendNotificationToAllClients(userName+" left");
					linkedList.sendCommandToAllClients("REMOVE@"+userName);
					try {
						socketReferenceToCurrentClient.close();
					} catch (IOException e1) {
						
					}
					return;
				}
				catch (IOException e) {

				}
				//distribute messages if not running in safe mode
				if(!referenceToServerWindow.isRunningInSafeMode)
					linkedList.distributeMessage(message.trim());

			}
		}
	}
	
}
