import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
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
	
	
	public void sendNamesOfActiveClientsToParticularClient(Socket socket){
		String userNamesOfActiveClients="";
		Node temp=head;
		Node client=null;
		while(temp!=null){
			if(temp.socket==socket)
				client=temp;
			userNamesOfActiveClients+=(temp.userName+"+");
			temp=temp.next;
		}
		try {
			client.dataOutputStream.writeUTF(userNamesOfActiveClients);
		} catch (IOException e) {
			
		}
	}
	
	
	
	
	
	//used by class TCPServer only	
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
	
	//used by class TCPServer only
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

	//used by class TCPServer only
	public void sendNotificationToAllClientsExcept(Socket socket,String message){
		Node temp=head;
		while(temp!=null){
			if(temp.dataOutputStream!=null&&temp.socket!=socket)
				try {
					temp.dataOutputStream.writeUTF("\n******************************************************************************************\n"
							+message+"...\n"
							+"******************************************************************************************\n");
							
				} catch (IOException e) {
		
				}
			temp=temp.next;
		}
	}

	
	//used by class TCPServerForFileTransfer only
	//send commands like START_SENDING,STARRT_RECIEVING
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
	
	
	public void sendCommandToAllClientsExcept(Socket socket,String message){
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

	//used by class TCPServerForFileTransfer only
	//notify all clients about file sent by someone
	//message will have the filename with format like "fileName(senderName)"
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
	
}

