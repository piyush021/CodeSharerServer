import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JOptionPane;

public class TCPServerForFileTransfer {
	
	private ServerWindow referenceToServerWindow=null;
	private TCPServer referenceToTCPServer=null;
	private ServerSocket serverSocket=null;
	private LinkedList linkedList=null;
	private Socket socket=null;
	private String directoryToStoreRecievedFiles="";
	
	public TCPServerForFileTransfer(ServerWindow referenceToServerWindow,TCPServer referenceToTCPServer,String defaultDirectory){
		this.referenceToServerWindow=referenceToServerWindow;
		this.referenceToTCPServer=referenceToTCPServer;
		this.directoryToStoreRecievedFiles=defaultDirectory;
	}
	
	public void startTCPServerForFileTransfer(){
		try {
			serverSocket = new ServerSocket(3333);
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
	
	private class SeperateThreadForEveryClient extends Thread{
		Socket socketReferenceToCurrentClient=null;
		
		public SeperateThreadForEveryClient(Socket socketReferenceToCurrentClient){
			this.socketReferenceToCurrentClient=socketReferenceToCurrentClient;
		}
		
		@Override
		public void run(){
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
			
			
			while(true){
				try {
					message=dataInputStream.readUTF();					
				}catch(SocketException|NullPointerException e){
					System.err.println(e);
					linkedList.delete(socketReferenceToCurrentClient);
					try {
						socketReferenceToCurrentClient.close();
					} catch (IOException e1) {
						
					}
					return;
				}
				catch (IOException e) {
					System.err.println(e);
				}
				
				if(message.trim().startsWith("INITIATE_FILE_TRANSFER_FROM_CLIENT_TO_SERVER")){
					
					linkedList.sendCommandToParticularClient(socketReferenceToCurrentClient, "START_SENDING");
					
					//storing as "fileName(senderName)"
					final String recievedFileName="("+linkedList.getNameOfClient(socketReferenceToCurrentClient)+")"+message.trim().substring(message.trim().lastIndexOf('#')+1, message.trim().length());
					final int recievedFileLength=Integer.valueOf(message.trim().substring(message.trim().lastIndexOf('@')+1, message.trim().lastIndexOf('#')));
					final String absolutePathOfRecievedFile=directoryToStoreRecievedFiles+"\\"+recievedFileName;
					int totalBytesRead=0;
					int bytesReadThisTime=0;
					//delete the old file with same name
					File temp=new File(absolutePathOfRecievedFile);
					if(temp.exists())
						temp.delete();
					temp=null;
					
					FileOutputStream fileOutputStream=null;
					BufferedOutputStream bufferedOutputStream=null;
					try{
						InputStream inputStream=socketReferenceToCurrentClient.getInputStream();
						fileOutputStream=new FileOutputStream(absolutePathOfRecievedFile,true);
						bufferedOutputStream=new BufferedOutputStream(fileOutputStream);
						byte recievedbytes[]=new byte[5242880];
						bytesReadThisTime=inputStream.read(recievedbytes,0,recievedbytes.length);
						bufferedOutputStream.write(recievedbytes, 0, bytesReadThisTime);
					
						totalBytesRead=bytesReadThisTime;
					
						while(totalBytesRead<recievedFileLength){
							bytesReadThisTime=inputStream.read(recievedbytes,0, recievedbytes.length);
							bufferedOutputStream.write(recievedbytes, 0, bytesReadThisTime);
							totalBytesRead+=bytesReadThisTime;
						}
						//notify clients about the file
						referenceToTCPServer.linkedList.sendNotificationToAllClients(linkedList.getNameOfClient(socketReferenceToCurrentClient)+" sent a file "+recievedFileName);
						//populate jlist of client
						linkedList.sendCommandToAllClients(recievedFileName);
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
				
				else if(message.trim().startsWith("INITIATE_FILE_TRANSFER_FROM_SERVER_TO_CLIENT")){
					if(referenceToServerWindow.isRunningInSafeMode){
						continue;
					}
					final String fileRequestedByClient=message.trim().substring(message.trim().lastIndexOf('#')+1, message.trim().length());
					String nameOfFileToSend=directoryToStoreRecievedFiles+"\\"+fileRequestedByClient;
					linkedList.sendCommandToParticularClient(socketReferenceToCurrentClient, "START_RECIEVING@"+new File(nameOfFileToSend).length()+"#"+fileRequestedByClient);
					File fileToSend=null;
					FileInputStream fileInputStream=null;
					BufferedInputStream bufferedInputStream=null;
					try{
						OutputStream outputStream=socketReferenceToCurrentClient.getOutputStream();
						fileToSend=new File(nameOfFileToSend);
						byte byteArrayOfFileToSend[]=new byte[5242880];
						fileInputStream=new FileInputStream(fileToSend);
						bufferedInputStream=new BufferedInputStream(fileInputStream);
						int bytesRead=bufferedInputStream.read(byteArrayOfFileToSend, 0, byteArrayOfFileToSend.length);
						outputStream.write(byteArrayOfFileToSend, 0,bytesRead);
						while((bytesRead=bufferedInputStream.read(byteArrayOfFileToSend, 0, byteArrayOfFileToSend.length))!=-1){
							outputStream.write(byteArrayOfFileToSend, 0, bytesRead);
						}
						outputStream.flush();
					}catch(Exception e){
						System.err.println(e);
					}finally{
						try {
							bufferedInputStream.close();
							fileInputStream.close();
						} catch (IOException e) {
						}
					}
								
				}
			}
		}
	}
}
