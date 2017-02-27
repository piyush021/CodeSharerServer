import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

class LinkedList{

	private class Node{
		Socket socket=null;
		DataOutputStream dataOutputStream=null;
		String userName="";
		Node next;
		
		public Node(Socket socket){
			this.socket=socket;
			next=null;
		}
	}

	Node head=null;
	Node tail=null;

	public void add(Socket socket){
		Node newNode=new Node(socket);
		if(head==null){
			head=newNode;
		}
		else{
		    tail.next=newNode;
		}
		tail=newNode;
		try {
			newNode.dataOutputStream=new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			
		}
	}

	public void delete(Socket socket){
            Node current=head;
            Node previous=null;
            while(current!=null && current.socket!=socket){
                previous=current;
                current = current.next;
            }
            if(current==null)
            	return;
            if(current==head)
                head=head.next;
            else if(current==tail)
            {
                previous.next = null;
                tail=previous;
            }
            else
                previous.next=current.next;
        }

	public void distributeMessage(String message){
		Node temp=head;
		while(temp!=null){
			if(temp.dataOutputStream!=null)
				try {
					temp.dataOutputStream.writeUTF(
							"________________________________________________________________\n"+message+
							"\n________________________________________________________________\n");
							
				} catch (IOException e) {
		
				}
			temp=temp.next;
		}
	}
	
	
	void sendFileToParticularClient(Socket socket,final String fileFullPath,final String senderName){
		//run on same thread
		//we dont want to send message in between file transfer
		//that will corrupt the file
		File fileToSend=null;
		FileInputStream fileInputStream=null;
		BufferedInputStream bufferedInputStream=null;
		try{
			OutputStream outputStream=socket.getOutputStream();
			fileToSend=new File(fileFullPath);
			byte byteArrayOfFileToSend[]=new byte[99999999];
			fileInputStream=new FileInputStream(fileToSend);
			bufferedInputStream=new BufferedInputStream(fileInputStream);
			int bytesRead=bufferedInputStream.read(byteArrayOfFileToSend, 0, byteArrayOfFileToSend.length);
			outputStream.write(byteArrayOfFileToSend, 0,bytesRead);
			while((bytesRead=bufferedInputStream.read(byteArrayOfFileToSend, 0, byteArrayOfFileToSend.length))!=-1){
				outputStream.write(byteArrayOfFileToSend, 0, bytesRead);
			}
			
			outputStream.flush();
			sendNotificationToParticularClient(socket,senderName+" sent a file \""+fileToSend.getName()+"\"."
					+ "\nPlease check your download directory of CodeSharer");
		}catch(Exception e){

		}finally{
			try {
				bufferedInputStream.close();
				fileInputStream.close();
			} catch (IOException e) {
			}
		}
	}
	
	

	public void sendNotificationToParticularClient(Socket socket,String message){
		Node temp=head;
		while(temp!=null&&temp.socket!=socket)
			temp=temp.next;
		try {
			temp.dataOutputStream.writeUTF("\n******************************************************************************************\n"
					+message+"...\n"
					+"******************************************************************************************\n");
		
		} catch (IOException e) {
	
		}		
	}
	public void sendNotificationToAllClients(String message){
		Node temp=head;
		while(temp!=null){
			if(temp.dataOutputStream!=null)
				try {
					temp.dataOutputStream.writeUTF("\n******************************************************************************************\n"
							+message+"...\n"
							+"******************************************************************************************\n");
							
				} catch (IOException e) {
		
				}
			temp=temp.next;
		}
	}
	
	void setNameOfClient(Socket socket,String userName){
		Node temp=head;
		while(temp!=null&&temp.socket!=socket)
			temp=temp.next;
		if(temp==null)
			return;
		temp.userName=userName;
	}
	

	String getNameOfClient(Socket socket){
		Node temp=head;
		while(temp!=null&&temp.socket!=socket)
			temp=temp.next;
		if(temp==null)
			return "";
		return temp.userName;
	}
	
	
	
	public void sendCommandToParticularClient(Socket socket,String message){
		Node temp=head;
		while(temp!=null&&temp.socket!=socket)
			temp=temp.next;
		try {
			if(temp!=null)
				temp.dataOutputStream.writeUTF(message);
		} catch (IOException e) {
	
		}		
	}
	
	
	void sendCommandToAllClients(String message){
		Node temp=head;
		while(temp!=null){
			if(temp.dataOutputStream!=null)
				try {
					temp.dataOutputStream.writeUTF(message);			
				} catch (IOException e) {
		
				}
			temp=temp.next;
		}
	}
	void sendCommandExceptToClient(Socket socket,String message){
		Node temp=head;
		while(temp!=null){
			if(temp.dataOutputStream!=null&&temp.socket!=socket)
				try {
					temp.dataOutputStream.writeUTF(message);			
				} catch (IOException e) {
		
				}
			temp=temp.next;
		}
	}
}

