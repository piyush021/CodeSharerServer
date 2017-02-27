import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JOptionPane;
import javax.xml.crypto.Data;


public class TCPServer {
	
	final private String directoryToStoreRecievedFiles="C:\\Users\\Varsha yadav\\Saurabh";
	private ServerSocket serverSocket=null;
	private Socket socket=null;
	private LinkedList linkedList;
	private String absolutePathOfLastFileRecieved="";
	private String nameOfLastFileSender="";
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
		
			}
			String userName="";
			try {
				//first message sent by client is userName. 
				userName=dataInputStream.readUTF();
				linkedList.setNameOfClient(socketReferenceToCurrentClient,userName);
			} catch (IOException e) {

			}
			linkedList.sendNotificationToAllClients(userName +" joined");
			
			while(true){
				try {
					message=dataInputStream.readUTF();					
				}catch(SocketException e){
					linkedList.delete(socketReferenceToCurrentClient);
					linkedList.sendNotificationToAllClients(userName+" left");
					try {
						socketReferenceToCurrentClient.close();
					} catch (IOException e1) {
						
					}
					return;
				}
				catch (IOException e) {

				}
				
				if(message.trim().startsWith("INITIATE_FILE_TRANSFER_FROM_CLIENT_TO_SERVER")){
					
					linkedList.sendCommandToParticularClient(socketReferenceToCurrentClient, "START_SENDING");
					final String recievedFileName=message.trim().substring(message.trim().lastIndexOf('#')+1, message.trim().length());
					final int recievedFileLength=Integer.valueOf(message.trim().substring(message.trim().lastIndexOf('@')+1, message.trim().lastIndexOf('#')));
					final String absolutePathOfRecievedFile=directoryToStoreRecievedFiles+"\\"+recievedFileName;
					int totalBytesRead=0;
					int bytesReadThisTime=0;
					//outer class attribute
					absolutePathOfLastFileRecieved=absolutePathOfRecievedFile;
					//delete the old file with same name
					File temp=new File(absolutePathOfRecievedFile);
					if(temp.exists())
						temp.delete();
					temp=null;
					//outer class attribute
					nameOfLastFileSender=linkedList.getNameOfClient(socketReferenceToCurrentClient);
					FileOutputStream fileOutputStream=null;
					BufferedOutputStream bufferedOutputStream=null;
					try{
						InputStream inputStream=socketReferenceToCurrentClient.getInputStream();
						fileOutputStream=new FileOutputStream(absolutePathOfRecievedFile,true);
						bufferedOutputStream=new BufferedOutputStream(fileOutputStream);
						byte recievedbytes[]=new byte[99999999];
						bytesReadThisTime=inputStream.read(recievedbytes,0,recievedbytes.length);
						bufferedOutputStream.write(recievedbytes, 0, bytesReadThisTime);
						totalBytesRead=bytesReadThisTime;
					
					
						while(totalBytesRead<recievedFileLength){
							bytesReadThisTime=inputStream.read(recievedbytes,0, recievedbytes.length);
							bufferedOutputStream.write(recievedbytes, 0, bytesReadThisTime);
							totalBytesRead+=bytesReadThisTime;
						}
						//send files to all clients except who sent it
						linkedList.sendCommandExceptToClient(socketReferenceToCurrentClient,"INITIATE_FILE_TRANSFER_FROM_SERVER_TO_CLIENT@"+recievedFileLength+"#"+recievedFileName);
						
					}catch(Exception e){

					}
					finally{
						try {
							bufferedOutputStream.flush();
							bufferedOutputStream.close();
							fileOutputStream.flush();
							fileOutputStream.close();
						} catch (IOException e) {
						}
					}
				}
				else if(message.trim().equals("START_SENDING")){
					linkedList.sendFileToParticularClient(socketReferenceToCurrentClient,absolutePathOfLastFileRecieved,nameOfLastFileSender);
				}
				else
					linkedList.distributeMessage(message.trim());					
			}
		}
	}
	
}
